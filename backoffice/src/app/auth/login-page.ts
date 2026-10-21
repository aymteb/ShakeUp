import { Component, OnInit, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { AuthService } from '../core/auth.service';

const DEMO_EMAIL = 'coach@shakeup.local';
const DEMO_PASSWORD = 'coach123';
const FRONT_URL = 'http://localhost:4200';

@Component({
  selector: 'app-login-page',
  imports: [ReactiveFormsModule],
  templateUrl: './login-page.html',
  styleUrl: './login-page.css',
})
export class LoginPage implements OnInit {
  readonly error = signal('');
  readonly demoMode = signal(false);
  readonly frontUrl = FRONT_URL;
  readonly form;

  constructor(
    fb: FormBuilder,
    private readonly auth: AuthService,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
  ) {
    this.form = fb.nonNullable.group({
      email: ['', [Validators.required, Validators.email]],
      password: ['', Validators.required],
    });
  }

  ngOnInit() {
    if (this.route.snapshot.queryParamMap.get('demo') === '1') {
      this.demoMode.set(true);
      this.form.patchValue({ email: DEMO_EMAIL, password: DEMO_PASSWORD });
    }
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
        if (res.role !== 'COACH') {
          this.auth.clearSession();
          this.error.set('Ce compte est un espace élève — utilisez l’app publique.');
          return;
        }
        void this.router.navigateByUrl('/');
      },
      error: () => this.error.set('Email ou mot de passe incorrect'),
    });
  }
}
