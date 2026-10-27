import { Injectable, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { Observable, tap } from 'rxjs';

export type UserRole = 'COACH' | 'ELEVE';

export interface LoginResponse {
  token: string;
  role: UserRole;
  userId: number;
  displayName: string;
  mustChangePassword: boolean;
  coachingActive: boolean;
  coachingStartsOn: string | null;
  coachingEndsOn: string | null;
  coachEmail?: string | null;
  coachDisplayName?: string | null;
  serverDate?: string | null;
  isoYear?: number | null;
  isoWeek?: number | null;
  coachingWeekKind?: CoachingWeekKind | null;
}

export type CoachingWeekKind = 'FULL' | 'COUPURE';

export interface EleveAccessStatus {
  coachingActive: boolean;
  coachingStartsOn: string | null;
  coachingEndsOn: string | null;
  displayName: string;
  coachEmail: string | null;
  coachDisplayName: string | null;
  serverDate: string;
  isoYear: number;
  isoWeek: number;
  coachingWeekKind: CoachingWeekKind;
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
      .pipe(tap((res) => this.persist(normalizeLogin(res))));
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

  fetchAccessStatus(): Observable<EleveAccessStatus> {
    return this.http.get<EleveAccessStatus>(`${this.api}/eleve/me`).pipe(
      tap((status) => {
        const current = this.session();
        if (!current) {
          return;
        }
        this.persist({
          ...current,
          coachingActive: status.coachingActive,
          coachingStartsOn: status.coachingStartsOn,
          coachingEndsOn: status.coachingEndsOn,
          displayName: status.displayName || current.displayName,
          coachEmail: status.coachEmail,
          coachDisplayName: status.coachDisplayName,
          serverDate: status.serverDate,
          isoYear: status.isoYear,
          isoWeek: status.isoWeek,
          coachingWeekKind: status.coachingWeekKind,
        });
      }),
    );
  }

  markCoachingEnded() {
    const current = this.session();
    if (!current) {
      return;
    }
    this.persist({ ...current, coachingActive: false });
  }

  postLoginUrl(res: LoginResponse): string {
    if (res.mustChangePassword) {
      return '/password';
    }
    return res.coachingActive ? '/eleve' : '/eleve/bloque';
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
      return normalizeLogin(JSON.parse(raw) as LoginResponse);
    } catch {
      localStorage.removeItem(STORAGE_KEY);
      return null;
    }
  }
}

function normalizeLogin(res: LoginResponse): LoginResponse {
  return {
    ...res,
    coachingActive: res.coachingActive !== false,
    coachingStartsOn: res.coachingStartsOn ?? null,
    coachingEndsOn: res.coachingEndsOn ?? null,
  };
}
