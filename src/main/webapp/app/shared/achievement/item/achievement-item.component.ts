import { Component, EventEmitter, Input, Output, ViewChild } from '@angular/core';
import { IBadge } from 'app/shared/model/badge.model';
import { ILevel } from 'app/shared/model/level.model';
import { HttpResponse } from '@angular/common/http';
import { ISkill } from 'app/shared/model/skill.model';
import { BadgeService } from 'app/entities/badge';
import { LevelService } from 'app/entities/level';
import { NgbPopover } from '@ng-bootstrap/ng-bootstrap';

@Component({
    selector: 'jhi-achievement-item',
    templateUrl: './achievement-item.component.html',
    styleUrls: ['./achievement-item.scss']
})
export class AchievementItemComponent {
    @Input() item: any;
    @Input() irrelevancePercentage: number;
    @Input() progress: number;
    @Input() type = '';
    @Input() hasStatus = false;
    @Input() size = '10vh';
    @Input() completable = false;
    @Output() onItemSelected = new EventEmitter<ILevel | IBadge>();
    @ViewChild('popover') popover: NgbPopover;
    @Input() hasAuthority: boolean;
    inEditMode: boolean;
    private _active: boolean;

    get active(): boolean {
        return this._active;
    }

    @Input()
    set active(active: boolean) {
        this._active = active;
        if (!active) {
            this.inEditMode = false;
            this.popover.close();
        }
    }

    constructor(private badgeService: BadgeService, private levelService: LevelService) {}

    saveInstantMultiplier(newInstantMultiplier) {
        if (newInstantMultiplier || newInstantMultiplier === 0) {
            this.item.instantMultiplier = newInstantMultiplier;
            switch (this.type) {
                case 'item-badge':
                    this.badgeService.update(this.item).subscribe((res: HttpResponse<ISkill>) => {
                        this.inEditMode = false;
                    });
                    break;
                case 'item-level':
                    this.levelService.update(this.item).subscribe((res: HttpResponse<ISkill>) => {
                        this.inEditMode = false;
                    });
                    break;
            }
        }
        if (!newInstantMultiplier) {
            this.popover.close();
        }
    }

    selectItem(event) {
        event.preventDefault();
        event.stopPropagation();
        this.inEditMode = false;
        if (!this._active) {
            this.onItemSelected.emit(this.item);
            if (!this.popover.isOpen()) {
                this.popover.open();
            }
        } else {
            if (this.popover.isOpen()) {
                this.popover.close();
            }
        }
    }

    toggleEditMode(event) {
        event.preventDefault();
        event.stopPropagation();
        if (this.hasAuthority) {
            this.inEditMode = !this.inEditMode;
        }
    }

    onPopupEnter() {
        if (this.inEditMode) {
            this.popover.close();
        }
        this.popover.open();
    }

    onPopupLeave() {
        if (!this.inEditMode) {
            this.popover.close();
        }
    }

    get progressWidth() {
        return (this.progress * (100 - this.irrelevancePercentage)) / 100.0;
    }

    get itemStatusCssClass() {
        let itemStatus;
        const requiredScore = this.item.requiredScore * 100;
        if (this.progress >= requiredScore && this.completable) {
            itemStatus = 'complete';
        } else if (this.progress > 0) {
            itemStatus = 'incomplete';
        } else {
            itemStatus = 'disabled';
        }
        return this.hasStatus ? (this.type ? `${this.type}-${itemStatus}` : `item-${itemStatus}`) : '';
    }
}
