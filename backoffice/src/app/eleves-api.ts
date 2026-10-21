import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface Eleve {
  id: number;
  email: string;
  displayName: string;
  mustChangePassword: boolean;
  createdAt: string;
}

@Injectable({ providedIn: 'root' })
export class ElevesApi {
  private readonly base = 'http://localhost:8080/api/v1/coach/eleves';

  constructor(private readonly http: HttpClient) {}

  list(): Observable<Eleve[]> {
    return this.http.get<Eleve[]>(this.base);
  }
}
