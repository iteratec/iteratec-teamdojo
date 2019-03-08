import { Component, ElementRef, OnInit } from '@angular/core';
import { HttpResponse, HttpErrorResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { JhiAlertService, JhiDataUtils } from 'ng-jhipster';

import { ITeam } from 'app/shared/model/team.model';
import { DimensionService } from 'app/entities/dimension';
import { IImage } from 'app/shared/model/image.model';
import { ImageService } from 'app/entities/image';
import { TeamService } from 'app/entities/team';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

@Component({
    selector: 'jhi-teams-quickedit',
    templateUrl: './teams-edit.component.html',
    styleUrls: ['teams-edit.scss']
})
export class TeamsEditComponent implements OnInit {
    team: ITeam;
    isSaving: boolean;
    image: IImage;

    constructor(
        private activeModal: NgbActiveModal,
        private jhiAlertService: JhiAlertService,
        private teamService: TeamService,
        private dimensionService: DimensionService,
        private imageService: ImageService,
        private dataUtils: JhiDataUtils,
        private elementRef: ElementRef
    ) {}

    ngOnInit() {
        this.isSaving = false;
        this.image = {};
        if (this.team.imageId) {
            this.imageService
                .find(this.team.imageId)
                .subscribe((res: HttpResponse<IImage>) => (this.image = res.body), (res: HttpErrorResponse) => this.onError(res.message));
        }
    }

    cancel() {
        this.activeModal.dismiss('Edit has been cancelled');
    }

    save() {
        this.isSaving = true;
        this.image.name = this.team.shortName + '-logo-' + Date.now();

        let imageResult: Observable<HttpResponse<IImage>>;
        if (this.image.id !== undefined) {
            imageResult = this.imageService.update(this.image);
        } else {
            imageResult = this.imageService.create(this.image);
        }
        imageResult.subscribe(
            (imgRes: HttpResponse<IImage>) => {
                this.team.imageId = imgRes.body.id;
                this.teamService.update(this.team).subscribe(
                    (res: HttpResponse<ITeam>) => {
                        this.isSaving = false;
                        this.activeModal.close(res.body);
                    },
                    (res: HttpErrorResponse) => {
                        this.isSaving = false;
                        console.log('Failed to update team', res);
                    }
                );
            },
            (res: HttpErrorResponse) => {
                this.isSaving = false;
                console.log('Image upload failed', res);
            }
        );
    }

    byteSize(field) {
        return this.dataUtils.byteSize(field);
    }

    setFileData(event, entity, field, isImage) {
        this.dataUtils.setFileData(event, entity, field, isImage);
    }

    clearInputImage(field: string, fieldContentType: string, idInput: string) {
        this.dataUtils.clearInputImage(this.image, this.elementRef, field, fieldContentType, idInput);
    }

    private onError(errorMessage: string) {
        this.jhiAlertService.error(errorMessage, null, null);
    }
}
