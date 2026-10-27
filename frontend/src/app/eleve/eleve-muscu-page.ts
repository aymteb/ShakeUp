import { Component, OnDestroy, OnInit, computed, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { EleveApi } from './eleve-api';
import { Plan, PlanItem } from '../shared/api-types';

type SetState = {
  done: boolean;
  reps: number | null;
  weightKg: number | null;
  durationSeconds: number | null;
};

@Component({
  selector: 'app-eleve-muscu-page',
  imports: [FormsModule, RouterLink],
  templateUrl: './eleve-muscu-page.html',
  styleUrl: './eleve-muscu-page.css',
})
export class EleveMuscuPage implements OnInit, OnDestroy {
  readonly plan = signal<Plan | null>(null);
  readonly live = signal(false);
  readonly exerciseIndex = signal(0);
  readonly setIndex = signal(0);
  readonly sets = signal<SetState[][]>([]);
  readonly restLeft = signal(0);
  readonly error = signal('');
  readonly ok = signal('');

  private restTimer: ReturnType<typeof setInterval> | null = null;

  readonly currentItem = computed(() => {
    const p = this.plan();
    if (!p) {
      return null;
    }
    return p.items[this.exerciseIndex()] ?? null;
  });

  readonly progressLabel = computed(() => {
    const p = this.plan();
    if (!p?.items.length) {
      return '';
    }
    return `Exo ${this.exerciseIndex() + 1}/${p.items.length} · Série ${this.setIndex() + 1}`;
  });

  readonly doneCount = computed(() =>
    this.sets()
      .flat()
      .filter((s) => s.done).length,
  );

  readonly totalSets = computed(() => this.sets().flat().length);

  constructor(private readonly api: EleveApi) {}

  ngOnInit() {
    this.api.plan().subscribe({
      next: (p) => {
        this.plan.set(p);
        if (p) {
          this.sets.set(p.items.map((item) => this.buildSets(item)));
        }
      },
      error: () => this.error.set('Impossible de charger le programme'),
    });
  }

  ngOnDestroy() {
    this.clearRest();
  }

  startLive() {
    if (!this.plan()?.items.length) {
      return;
    }
    this.live.set(true);
    this.exerciseIndex.set(0);
    this.setIndex.set(0);
    this.ok.set('');
  }

  exitLive() {
    this.clearRest();
    this.live.set(false);
  }

  patchSet(field: keyof SetState, value: number | null) {
    const ei = this.exerciseIndex();
    const si = this.setIndex();
    this.sets.update((all) =>
      all.map((group, i) =>
        i !== ei
          ? group
          : group.map((s, j) => (j !== si ? s : { ...s, [field]: value })),
      ),
    );
  }

  completeSet() {
    const item = this.currentItem();
    if (!item) {
      return;
    }
    const ei = this.exerciseIndex();
    const si = this.setIndex();
    this.sets.update((all) =>
      all.map((group, i) =>
        i !== ei ? group : group.map((s, j) => (j !== si ? s : { ...s, done: true })),
      ),
    );

    const group = this.sets()[ei];
    const nextSet = si + 1;
    if (nextSet < group.length) {
      this.setIndex.set(nextSet);
      this.startRest(item.restSeconds);
      return;
    }

    const nextEx = ei + 1;
    const p = this.plan();
    if (p && nextEx < p.items.length) {
      this.exerciseIndex.set(nextEx);
      this.setIndex.set(0);
      this.startRest(item.restSeconds);
      return;
    }

    this.clearRest();
    this.finishSession();
  }

  skipRest() {
    this.clearRest();
  }

  finishSession() {
    const summary = this.buildSummary();
    this.api.saveLog(this.api.today(), true, summary).subscribe({
      next: () => {
        this.ok.set('Séance enregistrée');
        this.live.set(false);
      },
      error: () => this.error.set('Enregistrement impossible'),
    });
  }

  formatItem(item: PlanItem): string {
    if (item.exerciseKind === 'HOLD') {
      return `${item.setsTarget} × ${item.durationSeconds}s — repos ${item.restSeconds}s`;
    }
    const weight =
      item.weightKg == null || item.weightKg === 0 ? 'PDC' : `${item.weightKg} kg`;
    return `${item.setsTarget} × ${item.repsTarget} @ ${weight} — repos ${item.restSeconds}s`;
  }

  num(value: string | number): number | null {
    if (value === '' || value == null) {
      return null;
    }
    const n = typeof value === 'number' ? value : Number(value);
    return Number.isFinite(n) ? n : null;
  }

  private buildSets(item: PlanItem): SetState[] {
    return Array.from({ length: item.setsTarget }, () => ({
      done: false,
      reps: item.repsTarget ?? null,
      weightKg: item.weightKg ?? null,
      durationSeconds: item.durationSeconds ?? null,
    }));
  }

  private startRest(seconds: number) {
    this.clearRest();
    if (seconds <= 0) {
      return;
    }
    this.restLeft.set(seconds);
    this.restTimer = setInterval(() => {
      const next = this.restLeft() - 1;
      if (next <= 0) {
        this.clearRest();
      } else {
        this.restLeft.set(next);
      }
    }, 1000);
  }

  private clearRest() {
    if (this.restTimer) {
      clearInterval(this.restTimer);
      this.restTimer = null;
    }
    this.restLeft.set(0);
  }

  private buildSummary(): string {
    const p = this.plan();
    if (!p) {
      return 'Séance live';
    }
    const lines = p.items.map((item, i) => {
      const done = this.sets()[i]?.filter((s) => s.done).length ?? 0;
      return `${item.exerciseName}: ${done}/${item.setsTarget}`;
    });
    return `Live · ${lines.join(' · ')}`;
  }
}
