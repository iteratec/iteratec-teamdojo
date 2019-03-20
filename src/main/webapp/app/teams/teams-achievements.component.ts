import { Component, Input, OnChanges, OnInit, SimpleChanges } from '@angular/core';
import { IBadge } from 'app/shared/model/badge.model';
import { ITeam } from 'app/shared/model/team.model';
import { ILevel } from 'app/shared/model/level.model';
import { JhiAlertService } from 'ng-jhipster';
import { ActivatedRoute, ParamMap, Router } from '@angular/router';
import { IDimension } from 'app/shared/model/dimension.model';
import { RelevanceCheck } from 'app/shared';
import { CompletionCheck } from 'app/shared/util/completion-check';
import { IProgress, Progress } from 'app/shared/achievement/model/progress.model';
import { ITeamSkill } from 'app/shared/model/team-skill.model';
import 'simplebar';
import { ISkill } from 'app/shared/model/skill.model';
import { AccountService } from 'app/core';
import { ILevelSkill } from 'app/shared/model/level-skill.model';
import { NgbPanelChangeEvent } from '@ng-bootstrap/ng-bootstrap';

const ROLES_ALLOWED_TO_UPDATE = ['ROLE_ADMIN'];

@Component({
    selector: 'jhi-teams-achievements',
    templateUrl: './teams-achievements.component.html',
    styleUrls: ['./teams-achievements.scss']
})
export class TeamsAchievementsComponent implements OnInit, OnChanges {
    @Input() team: ITeam;
    @Input() teamSkills: ITeamSkill[];
    @Input() badges: IBadge[];
    @Input() skills: ISkill[];
    generalBadges: IBadge[];
    activeItemIds: { badge: number; level: number; dimension: number };
    expandedDimensions: string[];
    hasAuthority = false;

    constructor(
        private route: ActivatedRoute,
        private jhiAlertService: JhiAlertService,
        private router: Router,
        private accountService: AccountService
    ) {}

    ngOnInit() {
        this.generalBadges = this.badges.filter((badge: IBadge) => !badge.dimensions || !badge.dimensions.length);
        this.expandedDimensions = [];
        this.team.skills = this.teamSkills;

        this.route.queryParamMap.subscribe((params: ParamMap) => {
            const dimensionId = this.getParamAsNumber('dimension', params);
            const levelId = this.getParamAsNumber('level', params);
            const badgeId = this.getParamAsNumber('badge', params);
            this.activeItemIds = {
                badge: null,
                level: null,
                dimension: null
            };
            if (dimensionId) {
                const dimension = this.team.participations.find((d: IDimension) => d.id === dimensionId);
                if (dimension) {
                    this.activeItemIds.dimension = dimensionId;
                    this.setExpandedDimensionId(dimensionId);
                }
            }
            if (levelId) {
                const dimension = this.team.participations.find((d: IDimension) => d.levels.some((l: ILevel) => l.id === levelId));
                if (dimension) {
                    this.activeItemIds.dimension = dimension.id;
                    this.setExpandedDimensionId(dimension.id);
                    const level = dimension.levels.find((l: ILevel) => l.id === levelId);
                    if (level) {
                        this.activeItemIds.level = level.id;
                    }
                }
            } else if (badgeId) {
                const dimension = this.team.participations.find((d: IDimension) => d.badges.some((b: IBadge) => b.id === badgeId));
                let badge;
                if (dimension) {
                    this.activeItemIds.dimension = dimension.id;
                    this.setExpandedDimensionId(dimension.id);
                    badge = dimension.badges.find((b: IBadge) => b.id === badgeId);
                } else {
                    badge = this.generalBadges.find((b: IBadge) => b.id === badgeId);
                }
                if (badge) {
                    this.activeItemIds.badge = badge.id;
                }
            } else if (this.team.participations && this.team.participations.length) {
                const completedSkills: Array<ITeamSkill> = this.teamSkills.filter(teamSkill => teamSkill.completedAt);
                const dimensions: Array<IDimension> = completedSkills
                    .map(completedSkill => {
                        return this.team.participations.find(
                            (dimension: IDimension) =>
                                dimension.levels &&
                                dimension.levels.some(
                                    (level: ILevel) =>
                                        level.skills && level.skills.some((skill: ILevelSkill) => skill.skillId === completedSkill.skillId)
                                )
                        );
                    })
                    .filter(dimension => dimension !== undefined);
                const dimensionIds: Array<number> = dimensions.map(dimension => dimension.id);
                const uniqueDimensionIds = dimensionIds.filter((el, i, a) => i === a.indexOf(el)); // filter duplicates
                uniqueDimensionIds.forEach(id => this.setExpandedDimensionId(id));
            }
        });

        this.accountService.identity().then(identity => {
            this.hasAuthority = this.accountService.hasAnyAuthority(ROLES_ALLOWED_TO_UPDATE);
        });
    }

    ngOnChanges(changes: SimpleChanges): void {
        this.team.skills = this.teamSkills;
    }

    selectItem(itemType: string, itemId: number) {
        if (itemType && itemId >= 0) {
            for (const availableItemType in this.activeItemIds) {
                if (this.activeItemIds.hasOwnProperty(availableItemType) && availableItemType !== itemType) {
                    this.activeItemIds[availableItemType] = null;
                }
            }
            if (this.activeItemIds[itemType] === itemId) {
                this.activeItemIds[itemType] = null;
                this.router.navigate(['teams', this.team.shortName]);
            } else {
                this.activeItemIds[itemType] = itemId;
                this.router.navigate(['teams', this.team.shortName], {
                    queryParams: { [itemType]: this.activeItemIds[itemType] }
                });
            }
        }
    }

    getAchievementProgress(item: ILevel | IBadge): number {
        const scoreProgress = this.isRelevant(item) ? this.getLevelOrBadgeProgress(item) : new Progress(0, 0, 0);
        return scoreProgress.getPercentage();
    }

    getAchievementIrrelevancy(item: ILevel | IBadge): number {
        return new CompletionCheck(this.team, item, this.skills).getIrrelevancy();
    }

    getHighestAchievedLevel(dimension: IDimension): ILevel {
        let currentLevel;
        for (const level of dimension.levels) {
            const levelProgress = this.getLevelOrBadgeProgress(level);
            if (!levelProgress.isCompleted()) {
                break;
            }
            currentLevel = level;
        }
        return currentLevel;
    }

    isCompletable(level: ILevel, dimension: IDimension): boolean {
        return !dimension || !dimension.levels
            ? false
            : dimension.levels
                  .slice(0, dimension.levels.findIndex(l => l.id === level.id) || 0)
                  .every(l => this.getLevelOrBadgeProgress(l).isCompleted());
    }

    handleDimensionToggle(event: NgbPanelChangeEvent) {
        this.setDimensionPanelActiveState(event.panelId, event.nextState);
    }

    setDimensionPanelActiveState(panelId: string, expanded: boolean) {
        if (expanded) {
            if (!this.expandedDimensions.includes(panelId)) {
                this.expandedDimensions.push(panelId);
            }
        } else {
            const idx = this.expandedDimensions.findIndex(d => panelId === d);
            if (idx !== -1) {
                this.expandedDimensions.splice(idx, 1);
            }
        }
    }

    private getLevelOrBadgeProgress(item: ILevel | IBadge): IProgress {
        return new CompletionCheck(this.team, item, this.skills).getProgress();
    }

    private isRelevant(item: ILevel | IBadge): boolean {
        return new RelevanceCheck(this.team).isRelevantLevelOrBadge(item);
    }

    private setExpandedDimensionId(dimensionId: number) {
        this.expandedDimensions.push(`achievements-dimension-${dimensionId}`);
    }

    private onError(errorMessage: string) {
        this.jhiAlertService.error(errorMessage, null, null);
    }

    private getParamAsNumber(name: string, params: ParamMap): number {
        return Number.parseInt(params.get(name), 10);
    }
}
