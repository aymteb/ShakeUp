import { Component, OnInit, signal } from '@angular/core';
import { AuthService } from '../core/auth.service';
import { EleveApi } from './eleve-api';
import { NutritionTarget, Plan, PlanItem } from '../shared/api-types';

@Component({
  selector: 'app-eleve-home',
  templateUrl: './eleve-home.html',
  styleUrl: './eleve-home.css',
})
export class EleveHome implements OnInit {
  readonly plan = signal<Plan | null>(null);
  readonly target = signal<NutritionTarget | null>(null);

  constructor(
    readonly auth: AuthService,
    private readonly api: EleveApi,
  ) {}

  ngOnInit() {
    this.api.plan().subscribe((p) => this.plan.set(p));
    this.api.target().subscribe((t) => this.target.set(t));
  }

  formatItem(item: PlanItem): string {
    if (item.exerciseKind === 'HOLD') {
      return `${item.setsTarget} × ${item.durationSeconds}s — repos ${item.restSeconds}s`;
    }
    const weight =
      item.weightKg == null || item.weightKg === 0 ? 'poids du corps' : `${item.weightKg} kg`;
    return `${item.setsTarget} × ${item.repsTarget} @ ${weight} — repos ${item.restSeconds}s`;
  }
}
