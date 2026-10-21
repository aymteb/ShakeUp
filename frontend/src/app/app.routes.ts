import { Routes } from '@angular/router';
import { authGuard } from './core/auth.guard';
import { LandingPage } from './landing/landing-page';
import { LoginPage } from './auth/login-page';
import { PasswordPage } from './auth/password-page';
import { EleveHome } from './eleve/eleve-home';
import { ContactPage } from './contact/contact-page';

export const routes: Routes = [
  { path: '', pathMatch: 'full', component: LandingPage },
  { path: 'login', component: LoginPage },
  { path: 'contact', component: ContactPage },
  {
    path: 'features',
    loadChildren: () =>
      import('./features/features.routes').then((m) => m.featuresRoutes),
  },
  { path: 'password', component: PasswordPage, canActivate: [authGuard('ELEVE')] },
  { path: 'eleve', component: EleveHome, canActivate: [authGuard('ELEVE')] },
  { path: '**', redirectTo: '' },
];

