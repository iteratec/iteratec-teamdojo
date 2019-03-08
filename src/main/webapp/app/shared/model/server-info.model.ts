import { Moment } from 'moment';

export interface IServerInfo {
    time: Moment;
}

export class ServerInfo implements IServerInfo {
    constructor(public time: Moment) {}
}
