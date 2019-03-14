import { AchievableSkill, IAchievableSkill } from 'app/shared/model/achievable-skill.model';
import { HttpErrorResponse, HttpResponse } from '@angular/common/http';
import { ISkill } from 'app/shared/model/skill.model';
import { Component, EventEmitter, Input, OnChanges, OnInit, Output, ViewChild } from '@angular/core';
import { SkillService } from 'app/entities/skill';
import { empty, of } from 'rxjs';
import { map } from 'rxjs/operators';
import { NgbPopover } from '@ng-bootstrap/ng-bootstrap';

@Component({
    selector: 'jhi-skill-score',
    templateUrl: './skill-score.component.html',
    styleUrls: ['./skill-score.scss']
})
export class SkillScoreComponent {
    @Input() skill;
    @Input() hasAuthority: false;
    @Output() onSkillChanged = new EventEmitter<{ iSkill: ISkill; aSkill: IAchievableSkill }>();
    private _isEditingScore = {};
    @ViewChild('scorePopover') popover: NgbPopover;

    constructor(private skillService: SkillService) {}

    updateScore(newScore) {
        if (newScore || newScore === 0) {
            const skillPromise = this.skill.skillId
                ? this.skillService.find(this.skill.skillId).pipe(map(res => res.body))
                : of(this.skill);

            skillPromise.subscribe(
                skill => {
                    skill.score = newScore;
                    this.skillService.update(skill).subscribe((res: HttpResponse<ISkill>) => {
                        this.onSkillChanged.emit({
                            iSkill: res.body,
                            aSkill: null
                        });
                        this.skill = res.body;
                        this.popover.close();
                    });
                },
                (res: HttpErrorResponse) => {
                    console.log(res);
                }
            );
        } else {
            this.popover.close();
        }
    }

    onPopupEnter(popover, isEditing) {
        this._isEditingScore[this.skill.skillId || this.skill.id] = isEditing && this.hasAuthority;
        if (this.isEditingScore()) {
            popover.close();
        }
        popover.open();
    }

    onPopupLeave(popover) {
        if (!this.isEditingScore()) {
            popover.close();
        }
    }

    isEditingScore() {
        return this._isEditingScore[this.skill.skillId || this.skill.id];
    }
}
