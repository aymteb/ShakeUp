import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, of, catchError, map } from 'rxjs';
import {
  DiaryDay,
  FeedbackMessage,
  Food,
  NutritionTarget,
  Plan,
  WorkoutLog,
} from '../shared/api-types';

@Injectable({ providedIn: 'root' })
export class EleveApi {
  private readonly api = 'http://localhost:8080/api/v1/eleve';

  constructor(private readonly http: HttpClient) {}

  plan(): Observable<Plan | null> {
    return this.http.get<Plan>(`${this.api}/plan`, { observe: 'response' }).pipe(
      map((res) => (res.status === 204 ? null : res.body)),
      catchError(() => of(null)),
    );
  }

  saveLog(performedOn: string, done: boolean, comment: string) {
    return this.http.post<WorkoutLog>(`${this.api}/workout-logs`, {
      performedOn,
      done,
      comment,
    });
  }

  target(): Observable<NutritionTarget | null> {
    return this.http.get<NutritionTarget>(`${this.api}/nutrition-target`, { observe: 'response' }).pipe(
      map((res) => (res.status === 204 ? null : res.body)),
      catchError(() => of(null)),
    );
  }

  foods() {
    return this.http.get<Food[]>(`${this.api}/foods`);
  }

  diary(date: string) {
    return this.http.get<DiaryDay>(`${this.api}/diary`, { params: { date } });
  }

  addDiary(payload: {
    eatenOn: string;
    mealSlot: string;
    foodId: number;
    grams: number;
  }) {
    return this.http.post(`${this.api}/diary`, payload);
  }

  deleteDiary(id: number) {
    return this.http.delete(`${this.api}/diary/${id}`, { responseType: 'text' });
  }

  feedback(channel: 'MUSCU' | 'NUTRITION') {
    return this.http.get<FeedbackMessage[]>(`${this.api}/feedback`, { params: { channel } });
  }

  postFeedback(channel: 'MUSCU' | 'NUTRITION', body: string) {
    return this.http.post<FeedbackMessage>(`${this.api}/feedback`, { channel, body });
  }

  today(): string {
    return new Date().toISOString().slice(0, 10);
  }
}
