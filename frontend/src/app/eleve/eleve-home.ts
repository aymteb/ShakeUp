import { Component, OnInit, computed, signal } from '@angular/core';
import { RouterLink } from '@angular/router';
import { AuthService } from '../core/auth.service';
import { EleveApi } from './eleve-api';
import { NutritionTarget, Plan, PlanItem } from '../shared/api-types';

@Component({
  selector: 'app-eleve-home',
  imports: [RouterLink],
  templateUrl: './eleve-home.html',
  styleUrl: './eleve-home.css',
})
export class EleveHome implements OnInit {
  readonly plan = signal<Plan | null>(null);
  readonly target = signal<NutritionTarget | null>(null);
  readonly error = signal('');
  readonly isCoupure = computed(
    () => this.auth.session()?.coachingWeekKind === 'COUPURE',
  );

  constructor(
    readonly auth: AuthService,
    private readonly api: EleveApi,
  ) {}

  ngOnInit() {
    this.api.plan().subscribe({
      next: (p) => this.plan.set(p),
      error: () => this.error.set('Impossible de charger le programme'),
    });
    this.api.target().subscribe({
      next: (t) => this.target.set(t),
      error: () => this.error.set('Impossible de charger les consignes'),
    });
  }

  formatItem(item: PlanItem): string {
    if (item.exerciseKind === 'HOLD') {
      return `${item.setsTarget} × ${item.durationSeconds}s`;
    }
    const weight =
      item.weightKg == null || item.weightKg === 0 ? 'PDC' : `${item.weightKg} kg`;
    return `${item.setsTarget} × ${item.repsTarget} @ ${weight}`;
  }
}
