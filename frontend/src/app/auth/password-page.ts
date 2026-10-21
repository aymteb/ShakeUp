import { Component, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../core/auth.service';

@Component({
  selector: 'app-password-page',
  imports: [ReactiveFormsModule],
  templateUrl: './password-page.html',
  styleUrl: './password-page.css',
})
export class PasswordPage {
  readonly error = signal('');
  readonly ok = signal(false);
  readonly form;

  constructor(
    fb: FormBuilder,
    private readonly auth: AuthService,
    private readonly router: Router,
  ) {
    this.form = fb.nonNullable.group({
      currentPassword: ['', Validators.required],
      newPassword: ['', [Validators.required, Validators.minLength(6)]],
    });
  }

  submit() {
    this.form.markAllAsTouched();
    if (this.form.invalid) {
      this.error.set('Mot de passe actuel + nouveau (6 caractères min.)');
      return;
    }
    this.error.set('');
    const { currentPassword, newPassword } = this.form.getRawValue();
    this.auth.changePassword(currentPassword, newPassword).subscribe({
      next: () => {
        this.ok.set(true);
        void this.router.navigateByUrl('/eleve');
      },
      error: () => this.error.set('Impossible de changer le mot de passe'),
    });
  }
}
