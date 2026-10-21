import { Component } from '@angular/core';
import { Router, RouterLink, RouterLinkActive, RouterOutlet } from '@angular/router';

@Component({
  selector: 'app-features-shell',
  imports: [RouterOutlet, RouterLink, RouterLinkActive],
  templateUrl: './features-shell.html',
  styleUrl: './features-shell.css',
})
export class FeaturesShell {
  readonly nav = [
    { path: 'overview', label: 'Vue d’ensemble' },
    { path: 'musculation', label: 'Musculation' },
    { path: 'nutrition', label: 'Nutrition' },
    { path: 'pwa', label: 'App mobile (PWA)' },
  ] as const;

  constructor(private readonly router: Router) {}

  navSelect(event: Event) {
    const path = (event.target as HTMLSelectElement).value;
    if (path) {
      void this.router.navigate(['/features', path]);
    }
  }
}
