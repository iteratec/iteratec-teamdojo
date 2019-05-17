import { NgModule, CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { RouterModule } from '@angular/router';
import { JhiLanguageService } from 'ng-jhipster';
import { MarkdownModule } from 'ngx-markdown';

import { JhiLanguageHelper } from 'app/core';
import { TeamdojoSharedModule } from 'app/shared';
import {
    SkillComponent,
    SkillDetailComponent,
    SkillUpdateComponent,
    SkillDeletePopupComponent,
    SkillDeleteDialogComponent,
    skillRoute,
    skillPopupRoute
} from './';

const ENTITY_STATES = [...skillRoute, ...skillPopupRoute];

@NgModule({
    imports: [TeamdojoSharedModule, RouterModule.forChild(ENTITY_STATES), MarkdownModule.forChild()],
    declarations: [SkillComponent, SkillDetailComponent, SkillUpdateComponent, SkillDeleteDialogComponent, SkillDeletePopupComponent],
    entryComponents: [SkillComponent, SkillUpdateComponent, SkillDeleteDialogComponent, SkillDeletePopupComponent],
    providers: [{ provide: JhiLanguageService, useClass: JhiLanguageService }],
    schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class TeamdojoSkillModule {
    constructor(private languageService: JhiLanguageService, private languageHelper: JhiLanguageHelper) {
        this.languageHelper.language.subscribe((languageKey: string) => {
            if (languageKey !== undefined) {
                this.languageService.changeLanguage(languageKey);
            }
        });
    }
}
