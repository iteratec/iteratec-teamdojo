import { Component, EventEmitter, Input, OnChanges, OnInit, Output, SimpleChanges, ViewChild } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { ITeam } from 'app/shared/model/team.model';
import { ISkill } from 'app/shared/model/skill.model';
import { IBadge } from 'app/shared/model/badge.model';
import { ILevel } from 'app/shared/model/level.model';
import * as moment from 'moment';
import { HttpResponse } from '@angular/common/http';
import { IAchievableSkill } from 'app/shared/model/achievable-skill.model';
import { TeamsSelectionService } from 'app/shared/teams-selection/teams-selection.service';
import { ISkillRate } from 'app/shared/model/skill-rate.model';
import { IComment } from 'app/shared/model/comment.model';
import { SkillDetailsRatingComponent } from 'app/teams/skill-details/skill-details-rating/skill-details-rating.component';
import { IBadgeSkill } from 'app/shared/model/badge-skill.model';
import { ILevelSkill } from 'app/shared/model/level-skill.model';
import { ITeamSkill } from 'app/shared/model/team-skill.model';
import { TeamSkillService } from 'app/entities/team-skill';
import { ITraining } from 'app/shared/model/training.model';
import { NgbModal, NgbModalRef } from '@ng-bootstrap/ng-bootstrap';
import { TrainingsAddComponent } from 'app/shared/trainings/trainings-add.component';
import { SkillStatus, SkillStatusUtils } from 'app/shared/model/skill-status';
import { ServerInfoService } from 'app/server-info';
import { IServerInfo } from 'app/shared/model/server-info.model';

@Component({
    selector: 'jhi-skill-details-info',
    templateUrl: './skill-details-info.component.html',
    styleUrls: ['./skill-details-info.scss']
})
export class SkillDetailsInfoComponent implements OnInit, OnChanges {
    @Input() team: ITeam;

    @Input() skill: ISkill;

    @Input() achievableSkill: IAchievableSkill;

    @Output() onSkillChanged = new EventEmitter<IAchievableSkill>();

    @Output() onVoteSubmitted = new EventEmitter<ISkillRate>();

    @Output() onCommentSubmitted = new EventEmitter<IComment>();

    @ViewChild(SkillDetailsRatingComponent) skillRating;

    achievedByTeams: ITeam[] = [];

    neededForLevels: ILevel[] = [];

    neededForBadges: IBadge[] = [];

    trainings: ITraining[] = [];

    isTrainingPopupOpen = false;

    private _levels: ILevel[] = [];
    private _badges: IBadge[] = [];
    private _teams: ITeam[] = [];
    private _levelSkills: ILevelSkill[] = [];
    private _badgeSkills: IBadgeSkill[] = [];
    private _teamSkills: ITeamSkill[] = [];
    private _allTrainings: ITraining[] = [];

    constructor(
        private route: ActivatedRoute,
        private teamSkillsService: TeamSkillService,
        private teamsSelectionService: TeamsSelectionService,
        private modalService: NgbModal,
        private serverInfoService: ServerInfoService
    ) {}

    ngOnInit(): void {
        this.route.data.subscribe(({ dojoModel: { teams, teamSkills, levels, badges, levelSkills, badgeSkills }, trainings }) => {
            this._levels = (levels && levels.body ? levels.body : levels) || [];
            this._badges = (badges && badges.body ? badges.body : badges) || [];
            this._teams = (teams && teams.body ? teams.body : teams) || [];
            this._levelSkills = (levelSkills && levelSkills.body ? levelSkills.body : levelSkills) || [];
            this._badgeSkills = (badgeSkills && badgeSkills.body ? badgeSkills.body : badgeSkills) || [];
            this._teamSkills = (teamSkills && teamSkills.body ? teamSkills.body : teamSkills) || [];
            this._allTrainings = (trainings && trainings.body ? trainings.body : trainings) || [];
            this.loadData();
        });
    }

    ngOnChanges(changes: SimpleChanges) {
        if (!changes.skill) {
            this.loadData();
        }
    }

    loadData() {
        this.achievedByTeams = this._teams.filter((team: ITeam) =>
            this._teamSkills.some(
                (teamSkill: ITeamSkill) =>
                    team.id === teamSkill.teamId && teamSkill.skillId === this.skill.id && SkillStatusUtils.isValid(teamSkill.skillStatus)
            )
        );
        this.neededForLevels = this._levels.filter((level: ILevel) =>
            this._levelSkills.some((levelSkill: ILevelSkill) => level.id === levelSkill.levelId && levelSkill.skillId === this.skill.id)
        );
        this.neededForBadges = this._badges.filter((badge: IBadge) =>
            this._badgeSkills.some((badgeSkill: IBadgeSkill) => badge.id === badgeSkill.badgeId && badgeSkill.skillId === this.skill.id)
        );
        this.serverInfoService.query().subscribe((serverInfo: IServerInfo) => {
            this.trainings = (this._allTrainings || []).filter(
                training =>
                    (training.skills || []).find(skill => skill.id === this.skill.id) &&
                    (training.validUntil === null || training.validUntil > moment(serverInfo.time))
            );
        });
    }

    onVoteSubmittedFromChild(vote: ISkillRate) {
        this.onVoteSubmitted.emit(vote);
        this.onSkillChanged.emit(this.achievableSkill);
    }

    onCommentSubmittedFromChild(comment: IComment) {
        this.onCommentSubmitted.emit(comment);
    }

    onSkillInListChanged(skillObjs) {
        this.achievableSkill = skillObjs.aSkill;
        this.skill = skillObjs.iSkill;
        this.skillRating.onSkillChanged(skillObjs.iSkill);
        this.teamSkillsService.query().subscribe((res: HttpResponse<ITeamSkill[]>) => {
            this._teamSkills = res.body || [];
            this.loadData();
        });
    }

    onSkillInListClicked(skillObjs) {
        this.achievableSkill = skillObjs.aSkill;
        this.skill = skillObjs.iSkill;
        this.loadData();
        this.skillRating.onSkillChanged(skillObjs.iSkill);
    }

    onToggleSkill() {
        const isActivating = !SkillStatusUtils.isValid(this.achievableSkill.skillStatus);
        this.achievableSkill.achievedAt = isActivating ? moment() : null;
        this.onSkillChanged.emit(this.achievableSkill);
    }

    onToggleIrrelevance() {
        const isIrrelevant = this.achievableSkill.skillStatus !== SkillStatus.IRRELEVANT;
        if (isIrrelevant) {
            this.achievableSkill.achievedAt = null;
        }
        this.achievableSkill.irrelevant = isIrrelevant;
        this.onSkillChanged.emit(this.achievableSkill);
    }

    getStatusClass(skill: IAchievableSkill): string {
        return SkillStatusUtils.getLowerCaseValue(skill.skillStatus);
    }

    getSkillStatusTranslationKey(skill: IAchievableSkill): string {
        return SkillStatusUtils.getLowerCaseValue(skill.skillStatus);
    }

    updateSkillRating(skill: ISkill) {
        this.skillRating.onSkillChanged(skill);
    }

    get isSkillAchieved() {
        return this.achievableSkill && !!this.achievableSkill.achievedAt;
    }

    isSameTeamSelected() {
        const selectedTeam = this.teamsSelectionService.selectedTeam;
        return selectedTeam && this.team && selectedTeam.id === this.team.id;
    }

    addTraining(): NgbModalRef {
        if (this.isTrainingPopupOpen) {
            return;
        }
        this.isTrainingPopupOpen = true;
        const modalRef = this.modalService.open(TrainingsAddComponent, { size: 'lg' });
        modalRef.componentInstance.skills = [this.skill];
        modalRef.result.then(
            training => {
                this.isTrainingPopupOpen = false;
                this._allTrainings = (this._allTrainings || []).concat(training);
                this.trainings = (this.trainings || []).concat(training);
            },
            reason => {
                this.isTrainingPopupOpen = false;
            }
        );
        return modalRef;
    }
}
