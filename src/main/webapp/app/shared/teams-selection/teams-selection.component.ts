import { Component, OnInit } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { TeamsSelectionService } from './teams-selection.service';
import { TeamsService } from 'app/teams/teams.service';
import { ITeam } from 'app/shared/model/team.model';
import { Router } from '@angular/router';

@Component({
    selector: 'jhi-teams-selection',
    templateUrl: './teams-selection.component.html',
    styleUrls: ['./teams-selection.scss']
})
export class TeamsSelectionComponent implements OnInit {
    highlightedTeam: ITeam = null;
    selectedTeam: ITeam;
    teams: ITeam[] = [];

    constructor(
        private activeModal: NgbActiveModal,
        private teamsSelectionService: TeamsSelectionService,
        private teamsService: TeamsService,
        private router: Router
    ) {}

    ngOnInit(): void {
        this.teamsService.query().subscribe(teams => {
            this.teams = teams.body.filter(team => !team.expired).sort((a, b) => a.shortName.localeCompare(b.shortName));
        });
        this.teamsSelectionService.query().subscribe(selectedTeam => {
            this.selectedTeam = this.highlightedTeam = selectedTeam;
        });
    }

    selectTeam(team: ITeam) {
        this.highlightedTeam = team;
        this.teamsSelectionService.selectedTeam = team;
        this.activeModal.close('Team selected');
        this.router.navigate(['teams', team.shortName]);
    }

    deselectTeam() {
        this.highlightedTeam = null;
        this.teamsSelectionService.selectedTeam = null;
        this.activeModal.close('No team selected');
        this.router.navigate(['']);
    }

    createNewTeam() {
        this.activeModal.close('Create new Team');
        this.router.navigate(['/team/new']);
    }

    cancelTeamSelection() {
        this.activeModal.dismiss('Team selected cancelled');
    }
}
