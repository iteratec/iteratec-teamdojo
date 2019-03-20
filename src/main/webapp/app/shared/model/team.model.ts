import { Moment } from 'moment';
import { IDimension } from 'app/shared/model/dimension.model';
import { ITeamSkill } from 'app/shared/model/team-skill.model';

export interface ITeam {
    id?: number;
    name?: string;
    shortName?: string;
    slogan?: string;
    contactPerson?: string;
    validUntil?: Moment;
    pureTrainingTeam?: boolean;
    participations?: IDimension[];
    skills?: ITeamSkill[];
    imageName?: string;
    imageId?: number;
    daysUntilExpiration?: number;
    expired?: boolean;
}

export class Team implements ITeam {
    constructor(
        public id?: number,
        public name?: string,
        public shortName?: string,
        public slogan?: string,
        public contactPerson?: string,
        public validUntil?: Moment,
        public pureTrainingTeam?: boolean,
        public participations?: IDimension[],
        public skills?: ITeamSkill[],
        public imageName?: string,
        public imageId?: number,
        public expired?: boolean,
        public daysUntilExpiration?: number
    ) {
        this.pureTrainingTeam = this.pureTrainingTeam || false;
    }
}
