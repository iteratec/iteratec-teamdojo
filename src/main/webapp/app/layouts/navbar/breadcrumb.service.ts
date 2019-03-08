import { EventEmitter, Injectable, Output } from '@angular/core';
import { ITeam } from 'app/shared/model/team.model';
import { IDimension } from 'app/shared/model/dimension.model';
import { IBadge } from 'app/shared/model/badge.model';
import { ISkill } from 'app/shared/model/skill.model';
import { ILevel } from 'app/shared/model/level.model';
import { Breadcrumb } from 'app/shared/model/breadcrumb.model';
import { ActivatedRoute, Router } from '@angular/router';
import { Params } from '@angular/router/src/shared';

@Injectable()
export class BreadcrumbService {
    @Output() breadcrumbChanged = new EventEmitter<any>(true);

    private team: ITeam;
    private dimension: IDimension;
    private badge: IBadge;
    private skill: ISkill;
    private level: ILevel;
    private params: Params;

    constructor(private route: ActivatedRoute, private router: Router) {
        this.route.queryParams.subscribe(queryParams => {
            this.params = queryParams;
        });
    }

    setBreadcrumb(team: ITeam = null, dimension: IDimension = null, level: ILevel = null, badge: IBadge = null, skill: ISkill = null) {
        this.team = team;

        if (badge) {
            this.badge = badge;
            this.dimension = null;
            this.level = null;
        } else if (this.badge) {
            this.badge = null;
        }
        if (level || dimension) {
            this.dimension = dimension;
            this.level = level;
            this.badge = null;
        } else if (this.level || this.dimension) {
            this.dimension = null;
            this.level = null;
        }

        this.skill = skill;
        this.breadcrumbChanged.emit('Breadcrumb changed');
    }

    getCurrentBreadcrumb() {
        const breadcrumbs = [];

        const path = [];

        if (this.team !== null && typeof this.team !== 'undefined') {
            path.push('teams', this.team.shortName);
            const url = this.router.createUrlTree(path).toString();
            breadcrumbs.push(new Breadcrumb(this.team.shortName, url, false));
        } else {
            path.push('');
        }
        if (this.dimension !== null && typeof this.dimension !== 'undefined') {
            const url = this.router.createUrlTree(path, { queryParams: this.params }).toString();
            breadcrumbs.push(new Breadcrumb(this.dimension.name, url, false));
        }
        if (this.level !== null && typeof this.level !== 'undefined') {
            const url = this.router.createUrlTree(path, { queryParams: { level: this.level.id } }).toString();
            breadcrumbs.push(new Breadcrumb(this.level.name, url, false));
        }
        if (this.badge !== null && typeof this.badge !== 'undefined') {
            const url = this.router.createUrlTree(path, { queryParams: { badge: this.badge.id } }).toString();
            breadcrumbs.push(new Breadcrumb(this.badge.name, url, false));
        }
        if (this.skill !== null && typeof this.skill !== 'undefined') {
            path.push('skills', this.skill.id);
            const url = this.router.createUrlTree(path, { queryParams: this.params }).toString();
            breadcrumbs.push(new Breadcrumb(this.skill.title, url, false));
        }
        if (breadcrumbs.length > 0) {
            breadcrumbs[breadcrumbs.length - 1].active = true;
        }
        return breadcrumbs;
    }
}
