/* tslint:disable max-line-length */
import { getTestBed, TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { map, take } from 'rxjs/operators';
import * as moment from 'moment';
import { DATE_TIME_FORMAT } from 'app/shared/constants/input.constants';
import { TeamSkillService } from 'app/entities/team-skill/team-skill.service';
import { ITeamSkill, TeamSkill } from 'app/shared/model/team-skill.model';
import { SkillStatus } from 'app/shared/model/skill-status';

describe('Service Tests', () => {
    describe('TeamSkill Service', () => {
        let injector: TestBed;
        let service: TeamSkillService;
        let httpMock: HttpTestingController;
        let elemDefault: ITeamSkill;
        let currentDate: moment.Moment;
        beforeEach(() => {
            TestBed.configureTestingModule({
                imports: [HttpClientTestingModule]
            });
            injector = getTestBed();
            service = injector.get(TeamSkillService);
            httpMock = injector.get(HttpTestingController);
            currentDate = moment();

            elemDefault = new TeamSkill(0, currentDate, currentDate, false, SkillStatus.OPEN, 'AAAAAAA', 0, 'AAAAAAA');
        });

        describe('Service methods', async () => {
            it('should find an element', async () => {
                const returnedFromService = Object.assign(
                    {
                        completedAt: currentDate.format(DATE_TIME_FORMAT),
                        verifiedAt: currentDate.format(DATE_TIME_FORMAT)
                    },
                    elemDefault
                );
                service
                    .find(123)
                    .pipe(take(1))
                    .subscribe(resp => expect(resp).toMatchObject({ body: elemDefault }));

                const req = httpMock.expectOne({ method: 'GET' });
                req.flush(JSON.stringify(returnedFromService));
            });

            it('should create a TeamSkill', async () => {
                const returnedFromService = Object.assign(
                    {
                        id: 0,
                        completedAt: currentDate.format(DATE_TIME_FORMAT),
                        verifiedAt: currentDate.format(DATE_TIME_FORMAT)
                    },
                    elemDefault
                );
                const expected = Object.assign(
                    {
                        completedAt: currentDate,
                        verifiedAt: currentDate
                    },
                    returnedFromService
                );
                service
                    .create(new TeamSkill(null))
                    .pipe(take(1))
                    .subscribe(resp => expect(resp).toMatchObject({ body: expected }));
                const req = httpMock.expectOne({ method: 'POST' });
                req.flush(JSON.stringify(returnedFromService));
            });

            it('should update a TeamSkill', async () => {
                const returnedFromService = Object.assign(
                    {
                        completedAt: currentDate.format(DATE_TIME_FORMAT),
                        verifiedAt: currentDate.format(DATE_TIME_FORMAT),
                        irrelevant: true,
                        note: 'BBBBBB',
                        vote: 1,
                        voters: 'BBBBBB'
                    },
                    elemDefault
                );

                const expected = Object.assign(
                    {
                        completedAt: currentDate,
                        verifiedAt: currentDate
                    },
                    returnedFromService
                );
                service
                    .update(expected)
                    .pipe(take(1))
                    .subscribe(resp => expect(resp).toMatchObject({ body: expected }));
                const req = httpMock.expectOne({ method: 'PUT' });
                req.flush(JSON.stringify(returnedFromService));
            });

            it('should return a list of TeamSkill', async () => {
                const returnedFromService = Object.assign(
                    {
                        completedAt: currentDate.format(DATE_TIME_FORMAT),
                        verifiedAt: currentDate.format(DATE_TIME_FORMAT),
                        irrelevant: true,
                        note: 'BBBBBB',
                        vote: 1,
                        voters: 'BBBBBB'
                    },
                    elemDefault
                );
                const expected = Object.assign(
                    {
                        completedAt: currentDate,
                        verifiedAt: currentDate
                    },
                    returnedFromService
                );
                service
                    .query(expected)
                    .pipe(
                        take(1),
                        map(resp => resp.body)
                    )
                    .subscribe(body => expect(body).toContainEqual(expected));
                const req = httpMock.expectOne({ method: 'GET' });
                req.flush(JSON.stringify([returnedFromService]));
                httpMock.verify();
            });

            it('should delete a TeamSkill', async () => {
                const rxPromise = service.delete(123).subscribe(resp => expect(resp.ok));

                const req = httpMock.expectOne({ method: 'DELETE' });
                req.flush({ status: 200 });
            });
        });

        afterEach(() => {
            httpMock.verify();
        });
    });
});
