import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { HttpResponse, HttpErrorResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { filter, map } from 'rxjs/operators';
import * as moment from 'moment';
import { DATE_TIME_FORMAT } from 'app/shared/constants/input.constants';
import { JhiAlertService } from 'ng-jhipster';
import { ITeam } from 'app/shared/model/team.model';
import { TeamService } from './team.service';
import { IDimension } from 'app/shared/model/dimension.model';
import { DimensionService } from 'app/entities/dimension';
import { IImage } from 'app/shared/model/image.model';
import { ImageService } from 'app/entities/image';

@Component({
    selector: 'jhi-team-update',
    templateUrl: './team-update.component.html'
})
export class TeamUpdateComponent implements OnInit {
    team: ITeam;
    isSaving: boolean;

    dimensions: IDimension[];

    images: IImage[];
    validUntil: string;

    constructor(
        protected jhiAlertService: JhiAlertService,
        protected teamService: TeamService,
        protected dimensionService: DimensionService,
        protected imageService: ImageService,
        protected activatedRoute: ActivatedRoute
    ) {}

    ngOnInit() {
        this.isSaving = false;
        this.activatedRoute.data.subscribe(({ team }) => {
            this.team = team;
            this.validUntil = this.team.validUntil != null ? this.team.validUntil.format(DATE_TIME_FORMAT) : null;
        });
        this.dimensionService
            .query()
            .pipe(
                filter((mayBeOk: HttpResponse<IDimension[]>) => mayBeOk.ok),
                map((response: HttpResponse<IDimension[]>) => response.body)
            )
            .subscribe((res: IDimension[]) => (this.dimensions = res), (res: HttpErrorResponse) => this.onError(res.message));
        this.imageService
            .query()
            .pipe(
                filter((mayBeOk: HttpResponse<IImage[]>) => mayBeOk.ok),
                map((response: HttpResponse<IImage[]>) => response.body)
            )
            .subscribe((res: IImage[]) => (this.images = res), (res: HttpErrorResponse) => this.onError(res.message));
    }

    previousState() {
        window.history.back();
    }

    save() {
        this.isSaving = true;
        this.team.validUntil = this.validUntil != null ? moment(this.validUntil, DATE_TIME_FORMAT) : null;
        if (this.team.id !== undefined) {
            this.subscribeToSaveResponse(this.teamService.update(this.team));
        } else {
            this.subscribeToSaveResponse(this.teamService.create(this.team));
        }
    }

    protected subscribeToSaveResponse(result: Observable<HttpResponse<ITeam>>) {
        result.subscribe((res: HttpResponse<ITeam>) => this.onSaveSuccess(), (res: HttpErrorResponse) => this.onSaveError());
    }

    protected onSaveSuccess() {
        this.isSaving = false;
        this.previousState();
    }

    protected onSaveError() {
        this.isSaving = false;
    }

    protected onError(errorMessage: string) {
        this.jhiAlertService.error(errorMessage, null, null);
    }

    trackDimensionById(index: number, item: IDimension) {
        return item.id;
    }

    trackImageById(index: number, item: IImage) {
        return item.id;
    }

    getSelected(selectedVals: Array<any>, option: any) {
        if (selectedVals) {
            for (let i = 0; i < selectedVals.length; i++) {
                if (option.id === selectedVals[i].id) {
                    return selectedVals[i];
                }
            }
        }
        return option;
    }
}
