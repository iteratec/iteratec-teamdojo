import { ActivatedRouteSnapshot, Resolve, Route, Router, RouterStateSnapshot } from '@angular/router';
import { OverviewComponent } from './';
import { Injectable } from '@angular/core';
import { LevelSkillService } from 'app/entities/level-skill';
import { TeamService } from 'app/entities/team';
import { LevelService } from 'app/entities/level';
import { BadgeService } from 'app/entities/badge';
import { TeamSkillService } from 'app/entities/team-skill';
import { BadgeSkillService } from 'app/entities/badge-skill';
import { OverviewSkillDetailsComponent } from 'app/overview/skills/skill-details/overview-skill-details.component';
import { Skill } from 'app/shared/model/skill.model';
import { SkillService } from 'app/entities/skill';

@Injectable()
export class AllTeamsResolve implements Resolve<any> {
    constructor(private teamService: TeamService) {}

    resolve(route: ActivatedRouteSnapshot, state: RouterStateSnapshot) {
        return this.teamService.query();
    }
}

@Injectable()
export class AllLevelsResolve implements Resolve<any> {
    constructor(private levelService: LevelService) {}

    resolve(route: ActivatedRouteSnapshot, state: RouterStateSnapshot) {
        return this.levelService.query();
    }
}

@Injectable()
export class AllBadgesResolve implements Resolve<any> {
    constructor(private badgeService: BadgeService) {}

    resolve(route: ActivatedRouteSnapshot, state: RouterStateSnapshot) {
        return this.badgeService.query();
    }
}

@Injectable()
export class AllTeamSkillsResolve implements Resolve<any> {
    constructor(private teamSkillService: TeamSkillService) {}

    resolve(route: ActivatedRouteSnapshot, state: RouterStateSnapshot) {
        return this.teamSkillService.query();
    }
}

@Injectable()
export class AllLevelSkillsResolve implements Resolve<any> {
    constructor(private levelSkillService: LevelSkillService) {}

    resolve(route: ActivatedRouteSnapshot, state: RouterStateSnapshot) {
        return this.levelSkillService.query();
    }
}

@Injectable()
export class AllBadgeSkillsResolve implements Resolve<any> {
    constructor(private badgeSkillService: BadgeSkillService) {}

    resolve(route: ActivatedRouteSnapshot, state: RouterStateSnapshot) {
        return this.badgeSkillService.query();
    }
}

@Injectable()
export class SkillResolve implements Resolve<any> {
    constructor(private skillService: SkillService, private router: Router) {}

    resolve(route: ActivatedRouteSnapshot, state: RouterStateSnapshot) {
        const skillId = route.params['skillId'] ? route.params['skillId'] : null;
        if (skillId) {
            return this.skillService.find(skillId);
        }
        return new Skill();
    }
}

export const OVERVIEW_ROUTE: Route[] = [
    {
        path: '',
        component: OverviewComponent,
        data: {
            authorities: [],
            pageTitle: 'teamdojoApp.teams.home.title'
        },
        resolve: {
            teams: AllTeamsResolve,
            levels: AllLevelsResolve,
            badges: AllBadgesResolve,
            teamSkills: AllTeamSkillsResolve,
            levelSkills: AllLevelSkillsResolve,
            badgeSkills: AllBadgeSkillsResolve
        }
    },
    {
        path: 'overview/skills/:skillId',
        component: OverviewSkillDetailsComponent,
        resolve: {
            teams: AllTeamsResolve,
            levels: AllLevelsResolve,
            badges: AllBadgesResolve,
            teamSkills: AllTeamSkillsResolve,
            levelSkills: AllLevelSkillsResolve,
            badgeSkills: AllBadgeSkillsResolve,
            skill: SkillResolve
        },
        data: {
            authorities: [],
            pageTitle: 'teamdojoApp.teams.skills.title'
        }
    }
];