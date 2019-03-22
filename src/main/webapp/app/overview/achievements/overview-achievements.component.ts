import { Component, Input, OnInit } from '@angular/core';
import { ILevel } from 'app/shared/model/level.model';
import { DimensionService } from 'app/entities/dimension';
import { IDimension } from 'app/shared/model/dimension.model';
import { HttpResponse } from '@angular/common/http';
import { IBadge } from 'app/shared/model/badge.model';
import { ITeam } from 'app/shared/model/team.model';
import { CompletionCheck } from 'app/shared/util/completion-check';
import { ActivatedRoute, ParamMap, Router } from '@angular/router';
import { RelevanceCheck, sortLevels } from 'app/shared';
import 'simplebar';
import { ISkill } from 'app/shared/model/skill.model';
import { NgbPanelChangeEvent } from '@ng-bootstrap/ng-bootstrap';
import { AccountService } from 'app/core';

const ROLES_ALLOWED_TO_UPDATE = ['ROLE_ADMIN'];

@Component({
    selector: 'jhi-overview-achievements',
    templateUrl: './overview-achievements.component.html',
    styleUrls: ['./overview-achievements.scss']
})
export class OverviewAchievementsComponent implements OnInit {
    @Input() teams: ITeam[];
    @Input() levels: ILevel[];
    @Input() badges: IBadge[];
    @Input() skills: ISkill[];
    dimensions: IDimension[];
    generalBadges: IBadge[];
    activeItemIds: { [key: string]: number };
    expandedDimensions: string[];
    hasAuthority = false;

    constructor(
        private route: ActivatedRoute,
        private dimensionService: DimensionService,
        private router: Router,
        private accountService: AccountService
    ) {}

    ngOnInit(): void {
        this.dimensions = [];
        this.activeItemIds = {
            badge: null,
            level: null,
            dimension: null
        };
        this.generalBadges = [];
        this.expandedDimensions = [];
        this.dimensionService.query().subscribe((res: HttpResponse<IDimension[]>) => {
            this.dimensions = res.body;
            const levelsByDimensionId = {};
            this.levels.forEach((level: ILevel) => {
                levelsByDimensionId[level.dimensionId] = levelsByDimensionId[level.dimensionId] || [];
                levelsByDimensionId[level.dimensionId].push(Object.assign(level));
            });

            const badgesByDimensionId = {};
            this.badges.forEach((badge: IBadge) => {
                if (badge.dimensions && badge.dimensions.length) {
                    badge.dimensions.forEach((dimension: IDimension) => {
                        badgesByDimensionId[dimension.id] = badgesByDimensionId[dimension.id] || [];
                        badgesByDimensionId[dimension.id].push(Object.assign(badge));
                    });
                } else {
                    this.generalBadges.push(Object.assign(badge));
                }
            });

            this.dimensions.forEach((dimension: IDimension) => {
                dimension.levels = (sortLevels(levelsByDimensionId[dimension.id]) || []).reverse();
                dimension.badges = badgesByDimensionId[dimension.id] || [];
            });
        });

        this.route.queryParamMap.subscribe((params: ParamMap) => {
            const levelId = this.getParamAsNumber('level', params);
            const badgeId = this.getParamAsNumber('badge', params);
            const dimensionId = this.getParamAsNumber('dimension', params);
            this.activeItemIds = {
                badge: null,
                level: null,
                dimension: null
            };

            if (levelId) {
                this.activeItemIds.level = levelId;
                this.levels
                    .filter(l => l.id === levelId)
                    .forEach(l => {
                        if (!this.expandedDimensions.includes(`achievements-dimension-${l.dimensionId}`)) {
                            this.expandedDimensions.push(`achievements-dimension-${l.dimensionId}`);
                        }
                    });
            } else if (badgeId) {
                this.activeItemIds.badge = badgeId;
                const foundBadge = this.badges.find(b => b.id === badgeId);
                if (foundBadge) {
                    foundBadge.dimensions.forEach(d => {
                        this.expandedDimensions.push(`achievements-dimension-${d.id}`);
                    });
                }
            } else if (dimensionId) {
                this.activeItemIds.dimension = dimensionId;
            }
        });

        setTimeout(() => {
            this.accountService.identity().then(identity => {
                this.hasAuthority = this.accountService.hasAnyAuthority(ROLES_ALLOWED_TO_UPDATE);
            });
        }, 0);
    }

    handleDimensionToggle(panelId: string) {
        if (this.expandedDimensions.includes(panelId)) {
            const idx = this.expandedDimensions.findIndex(d => panelId === d);
            if (idx !== -1) {
                this.expandedDimensions.splice(idx, 1);
            }
        } else {
            this.expandedDimensions.push(panelId);
        }
    }

    getAchievementProgress(item: ILevel | IBadge) {
        let baseCount = 0;
        let completedCount = 0;
        this.teams.forEach((team: ITeam) => {
            if (this.isRelevant(team, item)) {
                baseCount++;
                if (this.isLevelOrBadgeCompleted(team, item)) {
                    completedCount++;
                }
            }
        });
        return baseCount === 0 ? 0 : (completedCount / baseCount) * 100;
    }

    private isLevelOrBadgeCompleted(team: ITeam, item: ILevel | IBadge): boolean {
        return new CompletionCheck(team, item, this.skills).isCompleted();
    }

    private isRelevant(team: ITeam, item: ILevel | IBadge): boolean {
        return new RelevanceCheck(team).isRelevantLevelOrBadge(item);
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
                this.router.navigate(['.']);
            } else {
                this.activeItemIds[itemType] = itemId;
                this.router.navigate(['.'], {
                    queryParams: { [itemType]: this.activeItemIds[itemType] }
                });
            }
        }
    }

    private getParamAsNumber(name: string, params: ParamMap): number {
        return Number.parseInt(params.get(name), 10);
    }
}
