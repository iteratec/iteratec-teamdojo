import { Component, Input, OnChanges, OnInit, SimpleChanges } from '@angular/core';
import { ITeam } from 'app/shared/model/team.model';
import { IDimension } from 'app/shared/model/dimension.model';
import { IBadge } from 'app/shared/model/badge.model';
import { CompletionCheck, RelevanceCheck } from 'app/shared';
import { Router } from '@angular/router';
import { HighestLevel, IHighestLevel } from 'app/shared/achievement';
import { ITeamSkill } from 'app/shared/model/team-skill.model';
import { ISkill } from 'app/shared/model/skill.model';
import { TeamScoreCalculation } from 'app/shared/util/team-score-calculation';
import { OrganizationService } from 'app/entities/organization';
import { NgbModal, NgbModalRef } from '@ng-bootstrap/ng-bootstrap';
import { TeamsEditComponent } from 'app/teams/teams-edit.component';
import { BreadcrumbService } from 'app/layouts/navbar/breadcrumb.service';
import { TeamsSelectionService } from 'app/shared/teams-selection/teams-selection.service';
import { take } from 'rxjs/operators';
import { trigger, transition, useAnimation } from '@angular/animations';
import { flash } from 'ng-animate';

@Component({
    selector: 'jhi-teams-status',
    templateUrl: './teams-status.component.html',
    styleUrls: ['teams-status.scss'],
    animations: [trigger('score', [transition('* <=> *', useAnimation(flash))])]
})
export class TeamsStatusComponent implements OnInit, OnChanges {
    @Input() team: ITeam;
    @Input() teamSkills: ITeamSkill[];
    @Input() badges: IBadge[];
    @Input() skills: ISkill[];
    completedBadges: IBadge[];
    highestAchievedLevels: IHighestLevel[];
    teamScore: number;
    levelUpScore: number;
    isTeamEditOpen: boolean;

    constructor(
        private organizationService: OrganizationService,
        private router: Router,
        private breadcrumbService: BreadcrumbService,
        private teamSelectionService: TeamsSelectionService,
        private modalService: NgbModal
    ) {}

    ngOnInit(): void {
        this.team.skills = this.teamSkills;
        this.organizationService
            .query()
            .pipe(take(1))
            .subscribe(res => {
                this.levelUpScore = res && res.body[0] ? res.body[0].levelUpScore : 0;
            });
        this.calculateStatus();
    }

    ngOnChanges(changes: SimpleChanges): void {
        this.team.skills = this.teamSkills;
        this.calculateStatus();
    }

    editTeam(): NgbModalRef {
        if (this.isTeamEditOpen) {
            return;
        }
        this.isTeamEditOpen = true;
        const modalRef = this.modalService.open(TeamsEditComponent, { size: 'lg' });
        (<TeamsEditComponent>modalRef.componentInstance).team = Object.assign({}, this.team);
        modalRef.result.then(
            team => {
                this.team = team;
                this.isTeamEditOpen = false;
                this.teamSelectionService.query().subscribe();
                this.router.navigate(['/teams/', (<ITeam>team).shortName]);
            },
            reason => {
                this.isTeamEditOpen = false;
            }
        );
        return modalRef;
    }

    private hasTeamChanged(team: any) {
        return team && team.previousValue && team.previousValue.id !== team.currentValue.id;
    }

    private calculateStatus() {
        this.teamScore = TeamScoreCalculation.calcTeamScore(this.team, this.skills, this.badges);
        this.completedBadges = this.getCompletedBadges();
        this.highestAchievedLevels = this.getHighestAchievedLevels();
    }

    selectItem(itemType: string, id: number) {
        this.router.navigate(['teams', this.team.shortName], {
            queryParams: { [itemType]: id }
        });
    }

    isSameTeamSelected() {
        const selectedTeam = this.teamSelectionService.selectedTeam;
        return selectedTeam && selectedTeam.id === this.team.id;
    }

    private getCompletedBadges() {
        return this.badges.filter(
            (badge: IBadge) =>
                new RelevanceCheck(this.team).isRelevantBadge(badge) && new CompletionCheck(this.team, badge, this.skills).isCompleted()
        );
    }

    private isLevelCompleted(level) {
        return new CompletionCheck(this.team, level, this.skills).isCompleted();
    }

    private getHighestAchievedLevels(): IHighestLevel[] {
        const highestAchievedLevels = [];
        this.team.participations.forEach((dimension: IDimension) => {
            let ordinal = 0;
            let achievedLevel;
            for (const level of dimension.levels || []) {
                if (!this.isLevelCompleted(level)) {
                    break;
                }
                achievedLevel = level;
                ordinal++;
            }
            if (achievedLevel) {
                highestAchievedLevels.push(new HighestLevel(dimension, achievedLevel, ordinal));
            }
        });
        return highestAchievedLevels;
    }

    get hasLeveledUp() {
        return this.levelUpScore > 0 && this.teamScore >= this.levelUpScore;
    }
}
