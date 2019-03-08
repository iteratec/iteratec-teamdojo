import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { SERVER_API_URL } from 'app/app.constants';
import { createRequestOption } from 'app/shared';
import { Observable } from 'rxjs';
import { IAchievableSkill } from 'app/shared/model/achievable-skill.model';
import * as moment from 'moment';
import { map } from 'rxjs/operators';

export type EntityArrayResponseType = HttpResponse<IAchievableSkill[]>;
export type EntityResponseType = HttpResponse<IAchievableSkill>;

@Injectable()
export class TeamsSkillsService {
    private resourceUrl = SERVER_API_URL + 'api/teams';

    constructor(private http: HttpClient) {}

    findAchievableSkill(teamId: number, skillId: number, req?: any): Observable<IAchievableSkill> {
        const options = createRequestOption(req);
        return this.http
            .get<IAchievableSkill>(`${this.resourceUrl}/${teamId}/achievable-skills/${skillId}`, {
                params: options,
                observe: 'response'
            })
            .pipe(map(res => this.convertItemFromServer(res.body)));
    }

    queryAchievableSkills(teamId: number, req?: any): Observable<EntityArrayResponseType> {
        const options = createRequestOption(req);
        return this.http
            .get<IAchievableSkill[]>(`${this.resourceUrl}/${teamId}/achievable-skills`, {
                params: options,
                observe: 'response'
            })
            .pipe(map((res: EntityArrayResponseType) => this.convertArrayResponse(res)));
    }

    updateAchievableSkill(teamId: number, skill: IAchievableSkill): Observable<EntityResponseType> {
        const copy = this.convert(skill);
        return this.http
            .put<IAchievableSkill>(`${this.resourceUrl}/${teamId}/achievable-skills`, copy, { observe: 'response' })
            .pipe(map((res: EntityResponseType) => this.convertResponse(res)));
    }

    private convertResponse(res: EntityResponseType): EntityResponseType {
        const body: IAchievableSkill = this.convertItemFromServer(res.body);
        return res.clone({ body });
    }

    private convertArrayResponse(res: EntityArrayResponseType): EntityArrayResponseType {
        const jsonResponse: IAchievableSkill[] = res.body;
        const body: IAchievableSkill[] = [];
        for (let i = 0; i < jsonResponse.length; i++) {
            body.push(this.convertItemFromServer(jsonResponse[i]));
        }
        return res.clone({ body });
    }

    /**
     * Convert a returned JSON object to Skill.
     */
    private convertItemFromServer(achievableSkill: IAchievableSkill): IAchievableSkill {
        const copy: IAchievableSkill = Object.assign({}, achievableSkill, {
            achievedAt: achievableSkill.achievedAt != null ? moment(achievableSkill.achievedAt) : achievableSkill.achievedAt,
            verifiedAt: achievableSkill.verifiedAt != null ? moment(achievableSkill.verifiedAt) : achievableSkill.verifiedAt
        });
        return copy;
    }

    /**
     * Convert a Skill to a JSON which can be sent to the server.
     */
    private convert(achievableSkill: IAchievableSkill): IAchievableSkill {
        const copy: IAchievableSkill = Object.assign({}, achievableSkill, {
            completedAt:
                achievableSkill.achievedAt != null && achievableSkill.achievedAt.isValid() ? achievableSkill.achievedAt.toJSON() : null,
            verifiedAt:
                achievableSkill.verifiedAt != null && achievableSkill.verifiedAt.isValid() ? achievableSkill.verifiedAt.toJSON() : null
        });
        return copy;
    }
}
