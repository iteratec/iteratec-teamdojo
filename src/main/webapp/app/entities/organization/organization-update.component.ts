import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { HttpErrorResponse, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { IOrganization } from 'app/shared/model/organization.model';
import { OrganizationService } from './organization.service';

@Component({
    selector: 'jhi-organization-update',
    templateUrl: './organization-update.component.html'
})
export class OrganizationUpdateComponent implements OnInit {
    organization: IOrganization;
    isSaving: boolean;

    constructor(protected organizationService: OrganizationService, protected activatedRoute: ActivatedRoute) {}

    ngOnInit() {
        this.isSaving = false;
        this.activatedRoute.data.subscribe(({ organization }) => {
            this.organization = organization;
        });
    }

    previousState() {
        window.history.back();
    }

    save() {
        this.isSaving = true;
        if (this.organization.id !== undefined) {
            this.subscribeToSaveResponse(this.organizationService.update(this.organization));
        } else {
            this.subscribeToSaveResponse(this.organizationService.create(this.organization));
        }
        // reload organization
        this.organizationService.findCurrent();
    }

    protected subscribeToSaveResponse(result: Observable<HttpResponse<IOrganization>>) {
        result.subscribe((res: HttpResponse<IOrganization>) => this.onSaveSuccess(), (res: HttpErrorResponse) => this.onSaveError());
    }

    protected onSaveSuccess() {
        this.isSaving = false;
        this.previousState();
    }

    protected onSaveError() {
        this.isSaving = false;
    }
}
