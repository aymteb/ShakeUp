import { Routes } from '@angular/router';
import { coachGuard } from './core/auth.guard';
import { LoginPage } from './auth/login-page';
import { CoachShell } from './layout/coach-shell';
import { ElevesListPage } from './eleves/eleves-list-page';
import { EleveDetailPage } from './eleves/eleve-detail-page';
import { CataloguesPage } from './catalogues/catalogues-page';

export const routes: Routes = [
  { path: 'login', component: LoginPage },
  {
    path: '',
    component: CoachShell,
    canActivate: [coachGuard],
    children: [
      { path: '', pathMatch: 'full', redirectTo: 'eleves' },
      { path: 'eleves', component: ElevesListPage },
      { path: 'eleves/:id', component: EleveDetailPage },
      { path: 'catalogues', component: CataloguesPage },
    ],
  },
  { path: '**', redirectTo: '' },
];
