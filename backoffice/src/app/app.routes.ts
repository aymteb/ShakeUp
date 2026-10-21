import { Routes } from '@angular/router';
import { coachGuard } from './core/auth.guard';
import { LoginPage } from './auth/login-page';
import { CoachHome } from './home/coach-home';

export const routes: Routes = [
  { path: 'login', component: LoginPage },
  { path: '', component: CoachHome, canActivate: [coachGuard] },
  { path: '**', redirectTo: '' },
];
