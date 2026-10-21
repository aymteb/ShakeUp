import { Routes } from '@angular/router';
import { FeaturesShell } from './features-shell';

export const featuresRoutes: Routes = [
  {
    path: '',
    component: FeaturesShell,
    children: [
      { path: '', pathMatch: 'full', redirectTo: 'overview' },
      {
        path: 'overview',
        loadComponent: () =>
          import('./pages/overview-page').then((m) => m.OverviewPage),
        title: 'ShakeUp — Découvrir',
      },
      {
        path: 'musculation',
        loadComponent: () =>
          import('./pages/musculation-page').then((m) => m.MusculationPage),
        title: 'ShakeUp — Musculation',
      },
      {
        path: 'nutrition',
        loadComponent: () =>
          import('./pages/nutrition-page').then((m) => m.NutritionPage),
        title: 'ShakeUp — Nutrition',
      },
      {
        path: 'pwa',
        loadComponent: () => import('./pages/pwa-page').then((m) => m.PwaPage),
        title: 'ShakeUp — App mobile',
      },
    ],
  },
];
