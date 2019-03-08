import { Moment } from 'moment';
import { IBadgeSkill } from 'app/shared/model/badge-skill.model';
import { IDimension } from 'app/shared/model/dimension.model';

export interface IBadge {
    id?: number;
    name?: string;
    description?: string;
    availableUntil?: Moment;
    availableAmount?: number;
    requiredScore?: number;
    instantMultiplier?: number;
    completionBonus?: number;
    skills?: IBadgeSkill[];
    dimensions?: IDimension[];
    imageName?: string;
    imageId?: number;
    imageHash?: string;
}

export class Badge implements IBadge {
    constructor(
        public id?: number,
        public name?: string,
        public description?: string,
        public availableUntil?: Moment,
        public availableAmount?: number,
        public requiredScore?: number,
        public instantMultiplier?: number,
        public completionBonus?: number,
        public skills?: IBadgeSkill[],
        public dimensions?: IDimension[],
        public imageName?: string,
        public imageId?: number,
        public imageHash?: string
    ) {}
}
