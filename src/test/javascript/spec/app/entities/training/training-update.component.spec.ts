/* tslint:disable max-line-length */
import { ComponentFixture, TestBed, fakeAsync, tick } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { of } from 'rxjs';

import { TeamdojoTestModule } from '../../../test.module';
import { TrainingUpdateComponent } from 'app/entities/training/training-update.component';
import { TrainingService } from 'app/entities/training/training.service';
import { Training } from 'app/shared/model/training.model';

describe('Component Tests', () => {
    describe('Training Management Update Component', () => {
        let comp: TrainingUpdateComponent;
        let fixture: ComponentFixture<TrainingUpdateComponent>;
        let service: TrainingService;

        beforeEach(() => {
            TestBed.configureTestingModule({
                imports: [TeamdojoTestModule],
                declarations: [TrainingUpdateComponent]
            })
                .overrideTemplate(TrainingUpdateComponent, '')
                .compileComponents();

            fixture = TestBed.createComponent(TrainingUpdateComponent);
            comp = fixture.componentInstance;
            service = fixture.debugElement.injector.get(TrainingService);
        });

        describe('save', () => {
            it('Should call update service on save for existing entity', fakeAsync(() => {
                // GIVEN
                const entity = new Training(123);
                spyOn(service, 'update').and.returnValue(of(new HttpResponse({ body: entity })));
                comp.training = entity;
                // WHEN
                comp.save();
                tick(); // simulate async

                // THEN
                expect(service.update).toHaveBeenCalledWith(entity);
                expect(comp.isSaving).toEqual(false);
            }));

            it('Should call create service on save for new entity', fakeAsync(() => {
                // GIVEN
                const entity = new Training();
                spyOn(service, 'create').and.returnValue(of(new HttpResponse({ body: entity })));
                comp.training = entity;
                // WHEN
                comp.save();
                tick(); // simulate async

                // THEN
                expect(service.create).toHaveBeenCalledWith(entity);
                expect(comp.isSaving).toEqual(false);
            }));
        });
    });
});
