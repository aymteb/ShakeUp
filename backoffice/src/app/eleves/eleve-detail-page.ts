import { Component, HostListener, OnInit, computed, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { ElevesApi } from './eleves-api';
import {
  CoachingWeek,
  CoachingWeekKind,
  Eleve,
  Exercise,
  NutritionTarget,
  Plan,
  PlanItem,
} from '../shared/api-types';
import { ExercisePicker } from '../exercises/exercise-picker';
import { SlashMenu } from '../exercises/slash-menu';

@Component({
  selector: 'app-eleve-detail-page',
  imports: [ReactiveFormsModule, RouterLink, ExercisePicker, SlashMenu],
  templateUrl: './eleve-detail-page.html',
  styleUrl: './eleve-detail-page.css',
})
export class EleveDetailPage implements OnInit {
  eleveId = 0;
  readonly eleve = signal<Eleve | null>(null);
  readonly plan = signal<Plan | null>(null);
  readonly target = signal<NutritionTarget | null>(null);
  readonly currentWeek = signal<CoachingWeek | null>(null);
  readonly weekNote = signal('');
  readonly exercises = signal<Exercise[]>([]);
  readonly error = signal('');
  readonly ok = signal('');

  readonly editingPlan = signal(false);
  readonly editingTarget = signal(false);
  readonly editingProfile = signal(false);
  readonly pickerOpen = signal(false);
  readonly slashOpen = signal(false);

  readonly draftTitle = signal('Séance A');
  readonly draftNotes = signal('');
  readonly draftItems = signal<PlanItem[]>([]);

  readonly profileForm;
  readonly targetForm;
  readonly addItemForm;
  readonly selectedExerciseId = signal(0);
  readonly selectedKind = computed(() => {
    const ex = this.exercises().find((e) => e.id === this.selectedExerciseId());
    return ex?.kind ?? 'LOAD';
  });
  readonly selectedExercise = computed(() =>
    this.exercises().find((e) => e.id === this.selectedExerciseId()) ?? null,
  );

  constructor(
    fb: FormBuilder,
    private readonly route: ActivatedRoute,
    private readonly router: Router,
    private readonly api: ElevesApi,
  ) {
    this.profileForm = fb.nonNullable.group({
      displayName: ['', Validators.required],
      temporaryPassword: [''],
      coachingStartsOn: [''],
      coachingEndsOn: [''],
    });
    this.targetForm = fb.nonNullable.group({
      caloriesKcal: [2500, [Validators.required, Validators.min(1)]],
      proteinG: [160 as number | null],
      carbsG: [250 as number | null],
      fatG: [70 as number | null],
      coachNote: [''],
    });
    this.addItemForm = fb.nonNullable.group({
      exerciseId: [0, Validators.required],
      setsTarget: [3, [Validators.required, Validators.min(1)]],
      repsTarget: [10],
      weightKg: [20 as number | null],
      durationSeconds: [45],
      restSeconds: [90, [Validators.required, Validators.min(0)]],
    });
  }

  @HostListener('document:keydown', ['$event'])
  onGlobalKey(event: KeyboardEvent) {
    if (!this.editingPlan() || this.pickerOpen() || this.slashOpen()) {
      return;
    }
    if (event.key !== '/') {
      return;
    }
    const t = event.target as HTMLElement | null;
    const tag = t?.tagName;
    if (tag === 'INPUT' || tag === 'TEXTAREA' || tag === 'SELECT' || t?.isContentEditable) {
      return;
    }
    event.preventDefault();
    this.slashOpen.set(true);
  }

  ngOnInit() {
    this.eleveId = Number(this.route.snapshot.paramMap.get('id'));
    this.api.exercises().subscribe({
      next: (list) => {
        const visible = list.filter((e) => !e.hidden);
        this.exercises.set(visible);
        if (visible[0]) {
          this.addItemForm.patchValue({ exerciseId: visible[0].id });
          this.selectedExerciseId.set(visible[0].id);
        }
      },
    });
    this.reload();
  }

  reload() {
    this.api.getEleve(this.eleveId).subscribe({
      next: (e) => {
        this.eleve.set(e);
        this.profileForm.patchValue({
          displayName: e.displayName,
          temporaryPassword: '',
          coachingStartsOn: e.coachingStartsOn ?? '',
          coachingEndsOn: e.coachingEndsOn ?? '',
        });
      },
      error: () => this.error.set('Élève introuvable'),
    });
    this.api.getPlan(this.eleveId).subscribe({ next: (p) => this.plan.set(p) });
    this.api.getTarget(this.eleveId).subscribe({
      next: (t) => {
        this.target.set(t);
        if (t) {
          this.targetForm.patchValue({
            caloriesKcal: t.caloriesKcal,
            proteinG: t.proteinG,
            carbsG: t.carbsG,
            fatG: t.fatG,
            coachNote: t.coachNote ?? '',
          });
        }
      },
    });
    this.api.currentCoachingWeek(this.eleveId).subscribe({
      next: (w) => {
        this.currentWeek.set(w);
        this.weekNote.set(w.note ?? '');
      },
      error: () => this.currentWeek.set(null),
    });
  }

  setWeekKind(kind: CoachingWeekKind) {
    const week = this.currentWeek();
    if (!week) {
      return;
    }
    this.api
      .saveCoachingWeek(this.eleveId, {
        isoYear: week.isoYear,
        isoWeek: week.isoWeek,
        kind,
        note: this.weekNote().trim() || null,
      })
      .subscribe({
        next: (w) => {
          this.currentWeek.set(w);
          this.weekNote.set(w.note ?? '');
          this.ok.set(
            kind === 'COUPURE'
              ? 'Semaine marquée en coupure'
              : 'Semaine marquée pleine',
          );
        },
        error: () => this.error.set('Impossible de mettre à jour la semaine'),
      });
  }

  formatItem(item: PlanItem): string {
    if (item.exerciseKind === 'HOLD') {
      return `${item.setsTarget} séries × ${item.durationSeconds}s — repos ${item.restSeconds}s`;
    }
    const weight =
      item.weightKg == null || item.weightKg === 0 ? 'poids du corps' : `${item.weightKg} kg`;
    return `${item.setsTarget} séries × ${item.repsTarget} reps @ ${weight} — repos ${item.restSeconds}s`;
  }

  startEditPlan() {
    const p = this.plan();
    this.draftTitle.set(p?.title ?? 'Séance A');
    this.draftNotes.set(p?.notes ?? '');
    this.draftItems.set(p ? p.items.map((i) => ({ ...i })) : []);
    this.editingPlan.set(true);
    this.ok.set('');
  }

  cancelEditPlan() {
    this.editingPlan.set(false);
    this.pickerOpen.set(false);
    this.slashOpen.set(false);
  }

  openPicker() {
    this.pickerOpen.set(true);
  }

  onExercisePicked(ex: Exercise) {
    this.addItemForm.patchValue({ exerciseId: ex.id });
    this.selectedExerciseId.set(ex.id);
    this.appendExercise(ex);
  }

  addDraftItem() {
    const ex = this.selectedExercise();
    if (!ex) {
      this.error.set('Choisis un exercice');
      return;
    }
    this.appendExercise(ex);
  }

  private appendExercise(ex: Exercise) {
    const v = this.addItemForm.getRawValue();
    if (ex.kind === 'HOLD') {
      if (!v.durationSeconds || v.durationSeconds < 1) {
        this.error.set('Indique une durée (secondes)');
        return;
      }
    } else if (!v.repsTarget || v.repsTarget < 1) {
      this.error.set('Indique les répétitions');
      return;
    }
    this.error.set('');
    this.draftItems.update((items) => [
      ...items,
      {
        exerciseId: ex.id,
        exerciseName: ex.name,
        muscleGroup: ex.muscleGroup,
        exerciseKind: ex.kind,
        setsTarget: v.setsTarget,
        repsTarget: ex.kind === 'HOLD' ? null : v.repsTarget,
        weightKg: ex.kind === 'HOLD' ? null : v.weightKg,
        durationSeconds: ex.kind === 'HOLD' ? v.durationSeconds : null,
        restSeconds: v.restSeconds,
      },
    ]);
  }

  removeDraftItem(index: number) {
    this.draftItems.update((items) => items.filter((_, i) => i !== index));
  }

  updateDraftItem(
    index: number,
    patch: Partial<Pick<PlanItem, 'setsTarget' | 'repsTarget' | 'weightKg' | 'durationSeconds' | 'restSeconds'>>,
  ) {
    this.draftItems.update((items) =>
      items.map((item, i) => (i === index ? { ...item, ...patch } : item)),
    );
  }

  numOrNull(value: string): number | null {
    if (value === '' || value == null) {
      return null;
    }
    const n = Number(value);
    return Number.isFinite(n) ? n : null;
  }

  num(value: string, fallback = 0): number {
    const n = Number(value);
    return Number.isFinite(n) ? n : fallback;
  }

  savePlan() {
    if (!this.draftTitle().trim() || this.draftItems().length === 0) {
      this.error.set('Titre + au moins un exercice');
      return;
    }
    this.error.set('');
    this.api
      .savePlan(this.eleveId, this.draftTitle().trim(), this.draftNotes(), this.draftItems())
      .subscribe({
        next: (p) => {
          this.plan.set(p);
          this.editingPlan.set(false);
          this.ok.set('Programme enregistré');
        },
        error: () => this.error.set('Enregistrement du programme impossible'),
      });
  }

  startEditTarget() {
    this.editingTarget.set(true);
    this.ok.set('');
  }

  cancelEditTarget() {
    this.editingTarget.set(false);
    const t = this.target();
    if (t) {
      this.targetForm.patchValue({
        caloriesKcal: t.caloriesKcal,
        proteinG: t.proteinG,
        carbsG: t.carbsG,
        fatG: t.fatG,
        coachNote: t.coachNote ?? '',
      });
    }
  }

  saveTarget() {
    if (this.targetForm.invalid) {
      return;
    }
    const v = this.targetForm.getRawValue();
    this.api
      .saveTarget(this.eleveId, {
        caloriesKcal: v.caloriesKcal,
        proteinG: v.proteinG,
        carbsG: v.carbsG,
        fatG: v.fatG,
        coachNote: v.coachNote || null,
      })
      .subscribe({
        next: (t) => {
          this.target.set(t);
          this.editingTarget.set(false);
          this.ok.set('Consignes enregistrées');
        },
        error: () => this.error.set('Enregistrement nutrition impossible'),
      });
  }

  startEditProfile() {
    this.editingProfile.set(true);
  }

  cancelEditProfile() {
    this.editingProfile.set(false);
    const e = this.eleve();
    if (e) {
      this.profileForm.patchValue({
        displayName: e.displayName,
        temporaryPassword: '',
        coachingStartsOn: e.coachingStartsOn ?? '',
        coachingEndsOn: e.coachingEndsOn ?? '',
      });
    }
  }

  saveProfile() {
    if (this.profileForm.controls.displayName.invalid) {
      return;
    }
    const { displayName, temporaryPassword, coachingStartsOn, coachingEndsOn } =
      this.profileForm.getRawValue();
    const body: {
      displayName: string;
      temporaryPassword?: string;
      coachingStartsOn?: string | null;
      coachingEndsOn?: string | null;
    } = {
      displayName,
      coachingStartsOn: coachingStartsOn.trim() || null,
      coachingEndsOn: coachingEndsOn.trim() || null,
    };
    if (temporaryPassword.trim()) {
      body.temporaryPassword = temporaryPassword.trim();
    }
    this.api.updateEleve(this.eleveId, body).subscribe({
      next: (e) => {
        this.eleve.set(e);
        this.editingProfile.set(false);
        this.ok.set(
          e.coachingActive
            ? 'Profil mis à jour — accès élève actif'
            : 'Profil mis à jour — accès élève coupé',
        );
      },
      error: (err) =>
        this.error.set(
          err?.status === 400
            ? 'Dates de coaching invalides'
            : 'Mise à jour impossible',
        ),
    });
  }

  removeEleve() {
    const e = this.eleve();
    if (!e || !confirm(`Supprimer ${e.displayName} ?`)) {
      return;
    }
    this.api.deleteEleve(this.eleveId).subscribe({
      next: () => void this.router.navigateByUrl('/eleves'),
      error: () => this.error.set('Suppression impossible'),
    });
  }
}
