import { Component, OnInit, OnDestroy } from '@angular/core';
import { HttpErrorResponse, HttpHeaders, HttpResponse } from '@angular/common/http';
import { Subscription } from 'rxjs';
import { JhiEventManager, JhiParseLinks, JhiAlertService } from 'ng-jhipster';

import { ISkill } from 'app/shared/model/skill.model';
import { AccountService } from 'app/core';

import { ITEMS_PER_PAGE } from 'app/shared';
import { SkillService } from './skill.service';
import { FilterQuery } from 'app/shared/table-filter/table-filter.component';

@Component({
    selector: 'jhi-skill',
    templateUrl: './skill.component.html'
})
export class SkillComponent implements OnInit, OnDestroy {
    skills: ISkill[];
    currentAccount: any;
    eventSubscriber: Subscription;
    itemsPerPage: number;
    links: any;
    page: any;
    predicate: any;
    reverse: any;
    totalItems: number;

    private filters: FilterQuery[] = [];

    constructor(
        protected skillService: SkillService,
        protected jhiAlertService: JhiAlertService,
        protected eventManager: JhiEventManager,
        protected parseLinks: JhiParseLinks,
        protected accountService: AccountService
    ) {
        this.skills = [];
        this.itemsPerPage = ITEMS_PER_PAGE;
        this.page = 0;
        this.links = {
            last: 0
        };
        this.predicate = 'id';
        this.reverse = true;
    }

    loadAll() {
        const query = {};
        this.filters.forEach(filter => (query[`${filter.fieldName}.${filter.operator}`] = filter.query));

        this.skillService
            .query({
                ...query,
                page: this.page,
                size: this.itemsPerPage,
                sort: this.sort()
            })
            .subscribe(
                (res: HttpResponse<ISkill[]>) => this.paginateSkills(res.body, res.headers),
                (res: HttpErrorResponse) => this.onError(res.message)
            );
    }

    applyFilter(query: FilterQuery[]) {
        this.filters = query;
        this.reset();
    }

    reset() {
        this.page = 0;
        this.skills = [];
        this.loadAll();
    }

    loadPage(page) {
        this.page = page;
        this.loadAll();
    }

    ngOnInit() {
        this.loadAll();
        this.accountService.identity().then(account => {
            this.currentAccount = account;
        });
        this.registerChangeInSkills();
    }

    ngOnDestroy() {
        this.eventManager.destroy(this.eventSubscriber);
    }

    trackId(index: number, item: ISkill) {
        return item.id;
    }

    registerChangeInSkills() {
        this.eventSubscriber = this.eventManager.subscribe('skillListModification', response => this.reset());
    }

    sort() {
        const result = [this.predicate + ',' + (this.reverse ? 'asc' : 'desc')];
        if (this.predicate !== 'id') {
            result.push('id');
        }
        return result;
    }

    protected paginateSkills(data: ISkill[], headers: HttpHeaders) {
        this.links = this.parseLinks.parse(headers.get('link'));
        this.totalItems = parseInt(headers.get('X-Total-Count'), 10);
        for (let i = 0; i < data.length; i++) {
            this.skills.push(data[i]);
        }
    }

    private onError(errorMessage: string) {
        console.error(errorMessage);
    }
}
