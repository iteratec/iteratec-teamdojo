import { Component, EventEmitter, OnInit, Output } from '@angular/core';
import { JhiDataUtils } from 'ng-jhipster';
import { ActivatedRoute } from '@angular/router';
import { ITeam } from 'app/shared/model/team.model';
import { IBadge } from 'app/shared/model/badge.model';
import { TeamSkillService } from 'app/entities/team-skill';
import { ITeamSkill } from 'app/shared/model/team-skill.model';
import { ISkill } from 'app/shared/model/skill.model';
import * as moment from 'moment';

@Component({
    selector: 'jhi-teams',
    templateUrl: './teams.component.html',
    styleUrls: ['./teams.scss']
})
export class TeamsComponent implements OnInit {
    @Output() changeTeam = new EventEmitter<any>();

    team: ITeam;
    teamSkills: ITeamSkill[];
    badges: IBadge[];
    skills: ISkill[];

    constructor(private dataUtils: JhiDataUtils, private route: ActivatedRoute, private teamSkillService: TeamSkillService) {}

    ngOnInit() {
        this.route.data.subscribe(({ dojoModel: { teams, levels, levelSkills, badges, badgeSkills }, team, skills }) => {
            const teamFromRoute = team && team.body ? team.body : team;
            this.team = (teams || []).find(t => t.id === teamFromRoute.id) || teamFromRoute;
            this.teamSkills = team && team.skills ? team.skills : [];
            this.badges = (badges && badges.body ? badges.body : badges) || [];
            this.skills = (skills && skills.body ? skills.body : skills) || [];
        });
        this.changeTeam.emit(this.team);
    }

    loadTeamSkills() {
        this.teamSkillService.query({ 'teamId.equals': this.team.id }).subscribe(teamSkillResponse => {
            this.team.skills = this.teamSkills = teamSkillResponse.body;
            if (this.team.skills.find(skill => moment().diff(skill.completedAt, 'seconds') < 2)) {
                document.getElementById('pyro').style.display = 'block';
                setTimeout(function() {
                    document.getElementById('pyro').style.display = 'none';
                }, 2000);
            }
        });
    }

    byteSize(field) {
        return this.dataUtils.byteSize(field);
    }

    openFile(contentType, field) {
        return this.dataUtils.openFile(contentType, field);
    }

    previousState() {
        window.history.back();
    }
}
