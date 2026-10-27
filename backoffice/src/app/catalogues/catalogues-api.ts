import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Exercise, WgerSyncResult } from '../shared/api-types';

@Injectable({ providedIn: 'root' })
export class CataloguesApi {
  private readonly base = 'http://localhost:8080/api/v1/coach';

  constructor(private readonly http: HttpClient) {}

  exercises(): Observable<Exercise[]> {
    return this.http.get<Exercise[]>(`${this.base}/exercises`);
  }

  syncWger(): Observable<WgerSyncResult> {
    return this.http.post<WgerSyncResult>(`${this.base}/exercises/sync-wger`, {});
  }

  setHidden(id: number, hidden: boolean): Observable<Exercise> {
    return this.http.patch<Exercise>(`${this.base}/exercises/${id}/hidden`, { hidden });
  }
}
