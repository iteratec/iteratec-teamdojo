import { Component, OnInit } from '@angular/core';
import { NgbActiveModal, NgbModal, NgbModalRef } from '@ng-bootstrap/ng-bootstrap';
import { TeamsSelectionService } from './teams-selection.service';
import { TeamsService } from 'app/teams/teams.service';
import { ITeam } from 'app/shared/model/team.model';
import { Router } from '@angular/router';
import { TeamsEditComponent } from 'app/shared/teams/teams-edit.component';

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
        private router: Router,
        private modalService: NgbModal
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

    createNewTeam(): NgbModalRef {
        this.activeModal.close('Create new Team');
        const modalRef = this.modalService.open(TeamsEditComponent, { size: 'lg' });
        (<TeamsEditComponent>modalRef.componentInstance).team = { official: false, pureTrainingTeam: true };
        modalRef.result.then(team => {
            this.selectedTeam = team;
            this.highlightedTeam = team;
            this.teamsSelectionService.selectedTeam = team;
            this.teamsSelectionService.query().subscribe();
            this.router.navigate(['/teams/', (<ITeam>team).shortName]);
        });
        return modalRef;
    }

    cancelTeamSelection() {
        this.activeModal.dismiss('Team selected cancelled');
    }
}
