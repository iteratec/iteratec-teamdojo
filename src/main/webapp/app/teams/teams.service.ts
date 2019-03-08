import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { SERVER_API_URL } from 'app/app.constants';
import { createRequestOption } from 'app/shared';
import { ITeam } from 'app/shared/model/team.model';
import { map } from 'rxjs/operators';

export type EntityResponseType = HttpResponse<ITeam>;
export type EntityArrayResponseType = HttpResponse<ITeam[]>;

@Injectable()
export class TeamsService {
    private resourceUrl = SERVER_API_URL + 'api/teams';

    constructor(private http: HttpClient) {}

    find(id: number): Observable<EntityResponseType> {
        return this.http
            .get<ITeam>(`${this.resourceUrl}/${id}`, { observe: 'response' })
            .pipe(map((res: EntityResponseType) => this.convertResponse(res)));
    }

    query(req?: any): Observable<EntityArrayResponseType> {
        const options = createRequestOption(req);
        return this.http
            .get<ITeam[]>(this.resourceUrl, { params: options, observe: 'response' })
            .pipe(map((res: EntityArrayResponseType) => this.convertArrayResponse(res)));
    }

    private convertResponse(res: EntityResponseType): EntityResponseType {
        const body: ITeam = this.convertItemFromServer(res.body);
        return res.clone({ body });
    }

    private convertArrayResponse(res: EntityArrayResponseType): EntityArrayResponseType {
        const jsonResponse: ITeam[] = res.body;
        const body: ITeam[] = [];
        for (let i = 0; i < jsonResponse.length; i++) {
            body.push(this.convertItemFromServer(jsonResponse[i]));
        }
        return res.clone({ body });
    }

    /**
     * Convert a returned JSON object to Team.
     */
    private convertItemFromServer(team: ITeam): ITeam {
        const copy: ITeam = Object.assign({}, team, {});
        return copy;
    }

    /**
     * Convert a Team to a JSON which can be sent to the server.
     */
    private convert(team: ITeam): ITeam {
        const copy: ITeam = Object.assign({}, team, {});
        return copy;
    }
}
