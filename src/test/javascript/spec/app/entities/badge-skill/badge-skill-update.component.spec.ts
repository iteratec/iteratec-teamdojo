/* tslint:disable max-line-length */
import { ComponentFixture, TestBed, fakeAsync, tick } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { Observable, of } from 'rxjs';

import { TeamdojoTestModule } from '../../../test.module';
import { BadgeSkillUpdateComponent } from 'app/entities/badge-skill/badge-skill-update.component';
import { BadgeSkillService } from 'app/entities/badge-skill/badge-skill.service';
import { BadgeSkill } from 'app/shared/model/badge-skill.model';

describe('Component Tests', () => {
    describe('BadgeSkill Management Update Component', () => {
        let comp: BadgeSkillUpdateComponent;
        let fixture: ComponentFixture<BadgeSkillUpdateComponent>;
        let service: BadgeSkillService;

        beforeEach(() => {
            TestBed.configureTestingModule({
                imports: [TeamdojoTestModule],
                declarations: [BadgeSkillUpdateComponent]
            })
                .overrideTemplate(BadgeSkillUpdateComponent, '')
                .compileComponents();

            fixture = TestBed.createComponent(BadgeSkillUpdateComponent);
            comp = fixture.componentInstance;
            service = fixture.debugElement.injector.get(BadgeSkillService);
        });

        describe('save', () => {
            it('Should call update service on save for existing entity', fakeAsync(() => {
                // GIVEN
                const entity = new BadgeSkill(123);
                spyOn(service, 'update').and.returnValue(of(new HttpResponse({ body: entity })));
                comp.badgeSkill = entity;
                // WHEN
                comp.save();
                tick(); // simulate async

                // THEN
                expect(service.update).toHaveBeenCalledWith(entity);
                expect(comp.isSaving).toEqual(false);
            }));

            it('Should call create service on save for new entity', fakeAsync(() => {
                // GIVEN
                const entity = new BadgeSkill();
                spyOn(service, 'create').and.returnValue(of(new HttpResponse({ body: entity })));
                comp.badgeSkill = entity;
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
