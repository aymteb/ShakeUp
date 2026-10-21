import { Component, OnInit, computed, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { EleveApi } from './eleve-api';
import { DiaryDay, FeedbackMessage, Food, NutritionTarget } from '../shared/api-types';

@Component({
  selector: 'app-eleve-nutrition-page',
  imports: [ReactiveFormsModule, RouterLink],
  templateUrl: './eleve-nutrition-page.html',
  styleUrl: './eleve-nutrition-page.css',
})
export class EleveNutritionPage implements OnInit {
  readonly target = signal<NutritionTarget | null>(null);
  readonly diary = signal<DiaryDay | null>(null);
  readonly foods = signal<Food[]>([]);
  readonly feedback = signal<FeedbackMessage[]>([]);
  readonly error = signal('');

  readonly remainingKcal = computed(() => {
    const t = this.target();
    const d = this.diary();
    if (!t || !d) {
      return null;
    }
    return Math.round(t.caloriesKcal - d.totalKcal);
  });

  readonly form;
  readonly feedbackForm;

  constructor(
    fb: FormBuilder,
    private readonly api: EleveApi,
  ) {
    this.form = fb.nonNullable.group({
      mealSlot: ['LUNCH' as string, Validators.required],
      foodId: [0, Validators.required],
      grams: [100, [Validators.required, Validators.min(1)]],
    });
    this.feedbackForm = fb.nonNullable.group({
      body: [''],
    });
  }

  ngOnInit() {
    this.api.foods().subscribe({
      next: (list) => {
        this.foods.set(list);
        if (list[0]) {
          this.form.patchValue({ foodId: list[0].id });
        }
      },
    });
    this.reload();
  }

  reload() {
    this.api.target().subscribe({ next: (t) => this.target.set(t) });
    this.api.diary(this.api.today()).subscribe({ next: (d) => this.diary.set(d) });
    this.api.feedback('NUTRITION').subscribe({ next: (m) => this.feedback.set(m) });
  }

  add() {
    if (this.form.invalid) {
      this.error.set('Choisis un aliment et une quantité');
      return;
    }
    const v = this.form.getRawValue();
    this.api
      .addDiary({
        eatenOn: this.api.today(),
        mealSlot: v.mealSlot,
        foodId: Number(v.foodId),
        grams: v.grams,
      })
      .subscribe({
        next: () => {
          this.error.set('');
          this.reload();
        },
        error: () => this.error.set('Ajout impossible'),
      });
  }

  remove(id: number) {
    this.api.deleteDiary(id).subscribe({
      next: () => this.reload(),
      error: () => this.error.set('Suppression impossible'),
    });
  }

  sendFeedback() {
    const body = this.feedbackForm.getRawValue().body.trim();
    if (!body) {
      return;
    }
    this.api.postFeedback('NUTRITION', body).subscribe({
      next: () => {
        this.feedbackForm.reset({ body: '' });
        this.reload();
      },
      error: () => this.error.set('Envoi impossible'),
    });
  }
}
