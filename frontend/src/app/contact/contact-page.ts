import { Component, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { HttpClient } from '@angular/common/http';
import { RouterLink } from '@angular/router';

@Component({
  selector: 'app-contact-page',
  imports: [ReactiveFormsModule, RouterLink],
  templateUrl: './contact-page.html',
  styleUrl: './contact-page.css',
})
export class ContactPage {
  readonly error = signal('');
  readonly sent = signal(false);
  readonly form;

  constructor(
    fb: FormBuilder,
    private readonly http: HttpClient,
  ) {
    this.form = fb.nonNullable.group({
      name: ['', [Validators.required, Validators.maxLength(120)]],
      email: ['', [Validators.required, Validators.email]],
      message: ['', [Validators.required, Validators.maxLength(2000)]],
    });
  }

  submit() {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      this.error.set('Nom, email et message sont requis.');
      return;
    }
    this.error.set('');
    this.http.post('http://localhost:8080/api/v1/contact', this.form.getRawValue()).subscribe({
      next: () => {
        this.sent.set(true);
        this.form.reset();
      },
      error: () => this.error.set('Envoi impossible pour le moment. Réessayez plus tard.'),
    });
  }
}
