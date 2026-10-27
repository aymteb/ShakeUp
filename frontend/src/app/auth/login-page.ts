import { Component, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../core/auth.service';

export const BACKOFFICE_URL = 'http://localhost:4300';

@Component({
  selector: 'app-login-page',
  imports: [ReactiveFormsModule],
  templateUrl: './login-page.html',
  styleUrl: './login-page.css',
})
export class LoginPage {
  readonly error = signal('');
  readonly backofficeUrl = BACKOFFICE_URL;
  readonly form;

  constructor(
    fb: FormBuilder,
    private readonly auth: AuthService,
    private readonly router: Router,
  ) {
    this.form = fb.nonNullable.group({
      email: ['', [Validators.required, Validators.email]],
      password: ['', Validators.required],
    });
  }

  submit() {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      this.error.set('Email et mot de passe requis');
      return;
    }
    this.error.set('');
    const { email, password } = this.form.getRawValue();
    this.auth.login(email, password).subscribe({
      next: (res) => {
        if (res.role !== 'ELEVE') {
          this.auth.clearSession();
          this.error.set('Compte coach : utilisez le back-office.');
          return;
        }
        void this.router.navigateByUrl(this.auth.postLoginUrl(res));
      },
      error: () => this.error.set('Email ou mot de passe incorrect'),
    });
  }
}
