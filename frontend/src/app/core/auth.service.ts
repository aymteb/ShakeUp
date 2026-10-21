import { Injectable, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { tap } from 'rxjs/operators';

export type UserRole = 'COACH' | 'ELEVE';

export interface LoginResponse {
  token: string;
  role: UserRole;
  userId: number;
  displayName: string;
  mustChangePassword: boolean;
}

const STORAGE_KEY = 'shakeup.auth.eleve';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly api = 'http://localhost:8080/api/v1';
  readonly session = signal<LoginResponse | null>(this.readStorage());

  constructor(
    private readonly http: HttpClient,
    private readonly router: Router,
  ) {}

  login(email: string, password: string) {
    return this.http
      .post<LoginResponse>(`${this.api}/auth/login`, { email, password })
      .pipe(tap((res) => this.persist(res)));
  }

  changePassword(currentPassword: string, newPassword: string) {
    return this.http
      .post(
        `${this.api}/auth/change-password`,
        { currentPassword, newPassword },
        { responseType: 'text' },
      )
      .pipe(
        tap(() => {
          const current = this.session();
          if (current) {
            this.persist({ ...current, mustChangePassword: false });
          }
        }),
      );
  }

  logout() {
    localStorage.removeItem(STORAGE_KEY);
    this.session.set(null);
    void this.router.navigateByUrl('/login');
  }

  clearSession() {
    localStorage.removeItem(STORAGE_KEY);
    this.session.set(null);
  }

  token(): string | null {
    return this.session()?.token ?? null;
  }

  private persist(res: LoginResponse) {
    localStorage.setItem(STORAGE_KEY, JSON.stringify(res));
    this.session.set(res);
  }

  private readStorage(): LoginResponse | null {
    const raw = localStorage.getItem(STORAGE_KEY);
    if (!raw) {
      return null;
    }
    try {
      return JSON.parse(raw) as LoginResponse;
    } catch {
      localStorage.removeItem(STORAGE_KEY);
      return null;
    }
  }
}
