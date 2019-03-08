/* tslint:disable max-line-length */
import { TestBed, getTestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { of } from 'rxjs';
import { take, map } from 'rxjs/operators';
import * as moment from 'moment';
import { DATE_TIME_FORMAT } from 'app/shared/constants/input.constants';
import { TrainingService } from 'app/entities/training/training.service';
import { ITraining, Training } from 'app/shared/model/training.model';

describe('Service Tests', () => {
    describe('Training Service', () => {
        let injector: TestBed;
        let service: TrainingService;
        let httpMock: HttpTestingController;
        let elemDefault: ITraining;
        let currentDate: moment.Moment;
        beforeEach(() => {
            TestBed.configureTestingModule({
                imports: [HttpClientTestingModule]
            });
            injector = getTestBed();
            service = injector.get(TrainingService);
            httpMock = injector.get(HttpTestingController);
            currentDate = moment();

            elemDefault = new Training(0, 'AAAAAAA', 'AAAAAAA', 'AAAAAAA', 'AAAAAAA', currentDate, false, 'AAAAAAA');
        });

        describe('Service methods', async () => {
            it('should find an element', async () => {
                const returnedFromService = Object.assign(
                    {
                        validUntil: currentDate.format(DATE_TIME_FORMAT)
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

            it('should create a Training', async () => {
                const returnedFromService = Object.assign(
                    {
                        id: 0,
                        validUntil: currentDate.format(DATE_TIME_FORMAT)
                    },
                    elemDefault
                );
                const expected = Object.assign(
                    {
                        validUntil: currentDate
                    },
                    returnedFromService
                );
                service
                    .create(new Training(null))
                    .pipe(take(1))
                    .subscribe(resp => expect(resp).toMatchObject({ body: expected }));
                const req = httpMock.expectOne({ method: 'POST' });
                req.flush(JSON.stringify(returnedFromService));
            });

            it('should update a Training', async () => {
                const returnedFromService = Object.assign(
                    {
                        title: 'BBBBBB',
                        description: 'BBBBBB',
                        contactPerson: 'BBBBBB',
                        link: 'BBBBBB',
                        validUntil: currentDate.format(DATE_TIME_FORMAT),
                        isOfficial: true,
                        suggestedBy: 'BBBBBB'
                    },
                    elemDefault
                );

                const expected = Object.assign(
                    {
                        validUntil: currentDate
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

            it('should return a list of Training', async () => {
                const returnedFromService = Object.assign(
                    {
                        title: 'BBBBBB',
                        description: 'BBBBBB',
                        contactPerson: 'BBBBBB',
                        link: 'BBBBBB',
                        validUntil: currentDate.format(DATE_TIME_FORMAT),
                        isOfficial: true,
                        suggestedBy: 'BBBBBB'
                    },
                    elemDefault
                );
                const expected = Object.assign(
                    {
                        validUntil: currentDate
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

            it('should delete a Training', async () => {
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
