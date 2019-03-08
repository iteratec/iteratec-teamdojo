import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, RouterStateSnapshot, Routes } from '@angular/router';
import { UserRouteAccessService } from 'app/core';
import { Observable, of } from 'rxjs';
import { filter, map } from 'rxjs/operators';
import { Badge } from 'app/shared/model/badge.model';
import { BadgeService } from './badge.service';
import { BadgeComponent } from './badge.component';
import { BadgeDetailComponent } from './badge-detail.component';
import { BadgeUpdateComponent } from './badge-update.component';
import { BadgeDeletePopupComponent } from './badge-delete-dialog.component';
import { IBadge } from 'app/shared/model/badge.model';

@Injectable({ providedIn: 'root' })
export class BadgeResolve implements Resolve<IBadge> {
    constructor(private service: BadgeService) {}

    resolve(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<IBadge> {
        const id = route.params['id'] ? route.params['id'] : null;
        if (id) {
            return this.service.find(id).pipe(
                filter((response: HttpResponse<Badge>) => response.ok),
                map((badge: HttpResponse<Badge>) => badge.body)
            );
        }
        return of(new Badge());
    }
}

export const badgeRoute: Routes = [
    {
        path: '',
        component: BadgeComponent,
        data: {
            authorities: ['ROLE_USER'],
            pageTitle: 'teamdojoApp.badge.home.title'
        },
        canActivate: [UserRouteAccessService]
    },
    {
        path: ':id/view',
        component: BadgeDetailComponent,
        resolve: {
            badge: BadgeResolve
        },
        data: {
            authorities: ['ROLE_USER'],
            pageTitle: 'teamdojoApp.badge.home.title'
        },
        canActivate: [UserRouteAccessService]
    },
    {
        path: 'new',
        component: BadgeUpdateComponent,
        resolve: {
            badge: BadgeResolve
        },
        data: {
            authorities: ['ROLE_USER'],
            pageTitle: 'teamdojoApp.badge.home.title'
        },
        canActivate: [UserRouteAccessService]
    },
    {
        path: ':id/edit',
        component: BadgeUpdateComponent,
        resolve: {
            badge: BadgeResolve
        },
        data: {
            authorities: ['ROLE_USER'],
            pageTitle: 'teamdojoApp.badge.home.title'
        },
        canActivate: [UserRouteAccessService]
    }
];

export const badgePopupRoute: Routes = [
    {
        path: ':id/delete',
        component: BadgeDeletePopupComponent,
        resolve: {
            badge: BadgeResolve
        },
        data: {
            authorities: ['ROLE_USER'],
            pageTitle: 'teamdojoApp.badge.home.title'
        },
        canActivate: [UserRouteAccessService],
        outlet: 'popup'
    }
];
