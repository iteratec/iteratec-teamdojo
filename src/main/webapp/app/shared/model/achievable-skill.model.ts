import { Moment } from 'moment';
import { SkillStatus } from 'app/shared/model/skill-status';

export interface IAchievableSkill {
    teamSkillId?: number;
    skillId?: number;
    title?: string;
    description?: string;
    achievedAt?: Moment;
    verifiedAt?: Moment;
    vote?: number;
    voters?: string;
    irrelevant?: boolean;
    score?: number;
    skillStatus?: SkillStatus;
    rateScore?: number;
    rateCount?: number;
}

export class AchievableSkill implements IAchievableSkill {
    constructor(
        public teamSkillId?: number,
        public skillId?: number,
        public title?: string,
        public description?: string,
        public achievedAt?: Moment,
        public verifiedAt?: Moment,
        public vote?: number,
        public voters?: string,
        public irrelevant?: boolean,
        public score?: number,
        public skillStatus?: SkillStatus,
        public rateScore?: number,
        public rateCount?: number
    ) {}
}
