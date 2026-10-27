import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, of, catchError, map } from 'rxjs';
import {
  CoachingWeek,
  CoachingWeekKind,
  Eleve,
  Exercise,
  NutritionTarget,
  Plan,
  PlanItem,
} from '../shared/api-types';

@Injectable({ providedIn: 'root' })
export class ElevesApi {
  private readonly base = 'http://localhost:8080/api/v1/coach';

  constructor(private readonly http: HttpClient) {}

  listEleves(): Observable<Eleve[]> {
    return this.http.get<Eleve[]>(`${this.base}/eleves`);
  }

  getEleve(id: number): Observable<Eleve> {
    return this.http.get<Eleve>(`${this.base}/eleves/${id}`);
  }

  createEleve(body: {
    email: string;
    displayName: string;
    temporaryPassword: string;
  }): Observable<Eleve> {
    return this.http.post<Eleve>(`${this.base}/eleves`, body);
  }

  updateEleve(
    id: number,
    body: {
      displayName: string;
      temporaryPassword?: string;
      coachingStartsOn?: string | null;
      coachingEndsOn?: string | null;
    },
  ): Observable<Eleve> {
    return this.http.patch<Eleve>(`${this.base}/eleves/${id}`, body);
  }

  deleteEleve(id: number): Observable<void> {
    return this.http.delete<void>(`${this.base}/eleves/${id}`);
  }

  exercises(): Observable<Exercise[]> {
    return this.http.get<Exercise[]>(`${this.base}/exercises`);
  }

  getPlan(eleveId: number): Observable<Plan | null> {
    return this.http
      .get<Plan>(`${this.base}/eleves/${eleveId}/plan`, { observe: 'response' })
      .pipe(
        map((res) => (res.status === 204 ? null : res.body)),
        catchError(() => of(null)),
      );
  }

  savePlan(
    eleveId: number,
    title: string,
    notes: string,
    items: PlanItem[],
  ): Observable<Plan> {
    return this.http.put<Plan>(`${this.base}/eleves/${eleveId}/plan`, {
      title,
      notes,
      items: items.map((i) => ({
        exerciseId: i.exerciseId,
        setsTarget: i.setsTarget,
        repsTarget: i.repsTarget ?? null,
        weightKg: i.weightKg ?? null,
        durationSeconds: i.durationSeconds ?? null,
        restSeconds: i.restSeconds,
      })),
    });
  }

  getTarget(eleveId: number): Observable<NutritionTarget | null> {
    return this.http
      .get<NutritionTarget>(`${this.base}/eleves/${eleveId}/nutrition-target`, {
        observe: 'response',
      })
      .pipe(
        map((res) => (res.status === 204 ? null : res.body)),
        catchError(() => of(null)),
      );
  }

  saveTarget(
    eleveId: number,
    body: {
      caloriesKcal: number;
      proteinG: number | null;
      carbsG: number | null;
      fatG: number | null;
      coachNote: string | null;
    },
  ): Observable<NutritionTarget> {
    return this.http.put<NutritionTarget>(
      `${this.base}/eleves/${eleveId}/nutrition-target`,
      body,
    );
  }

  currentCoachingWeek(eleveId: number): Observable<CoachingWeek> {
    return this.http.get<CoachingWeek>(
      `${this.base}/eleves/${eleveId}/coaching-weeks/current`,
    );
  }

  saveCoachingWeek(
    eleveId: number,
    body: {
      isoYear: number;
      isoWeek: number;
      kind: CoachingWeekKind;
      note?: string | null;
    },
  ): Observable<CoachingWeek> {
    return this.http.put<CoachingWeek>(
      `${this.base}/eleves/${eleveId}/coaching-weeks`,
      body,
    );
  }
}
