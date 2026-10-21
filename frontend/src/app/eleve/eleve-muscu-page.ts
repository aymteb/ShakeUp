import { Component, OnInit, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { EleveApi } from './eleve-api';
import { FeedbackMessage, Plan, PlanItem } from '../shared/api-types';

@Component({
  selector: 'app-eleve-muscu-page',
  imports: [ReactiveFormsModule, RouterLink],
  templateUrl: './eleve-muscu-page.html',
  styleUrl: './eleve-muscu-page.css',
})
export class EleveMuscuPage implements OnInit {
  readonly plan = signal<Plan | null>(null);
  readonly feedback = signal<FeedbackMessage[]>([]);
  readonly error = signal('');
  readonly ok = signal('');
  readonly form;
  readonly feedbackForm;

  constructor(
    fb: FormBuilder,
    private readonly api: EleveApi,
  ) {
    this.form = fb.nonNullable.group({
      done: [true],
      comment: [''],
    });
    this.feedbackForm = fb.nonNullable.group({
      body: [''],
    });
  }

  ngOnInit() {
    this.reload();
  }

  reload() {
    this.api.plan().subscribe({ next: (p) => this.plan.set(p) });
    this.api.feedback('MUSCU').subscribe({ next: (m) => this.feedback.set(m) });
  }

  saveLog() {
    const { done, comment } = this.form.getRawValue();
    this.api.saveLog(this.api.today(), done, comment).subscribe({
      next: () => {
        this.ok.set('Séance enregistrée');
        this.error.set('');
      },
      error: () => this.error.set('Enregistrement impossible'),
    });
  }

  sendFeedback() {
    const body = this.feedbackForm.getRawValue().body.trim();
    if (!body) {
      return;
    }
    this.api.postFeedback('MUSCU', body).subscribe({
      next: () => {
        this.feedbackForm.reset({ body: '' });
        this.reload();
      },
      error: () => this.error.set('Envoi impossible'),
    });
  }

  formatItem(item: PlanItem): string {
    if (item.exerciseKind === 'HOLD') {
      return `${item.setsTarget} séries × ${item.durationSeconds}s — repos ${item.restSeconds}s`;
    }
    const weight =
      item.weightKg == null || item.weightKg === 0
        ? 'poids du corps'
        : `${item.weightKg} kg`;
    return `${item.setsTarget} séries × ${item.repsTarget} reps @ ${weight} — repos ${item.restSeconds}s`;
  }
}
