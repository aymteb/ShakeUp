import { Component, OnInit, signal } from '@angular/core';
import { AuthService } from '../core/auth.service';
import { Eleve, ElevesApi } from '../eleves-api';

@Component({
  selector: 'app-coach-home',
  templateUrl: './coach-home.html',
  styleUrl: './coach-home.css',
})
export class CoachHome implements OnInit {
  readonly eleves = signal<Eleve[]>([]);
  readonly error = signal('');
  readonly loading = signal(true);

  constructor(
    readonly auth: AuthService,
    private readonly elevesApi: ElevesApi,
  ) {}

  ngOnInit() {
    this.elevesApi.list().subscribe({
      next: (list) => {
        this.eleves.set(list);
        this.loading.set(false);
      },
      error: () => {
        this.error.set('Impossible de charger les élèves');
        this.loading.set(false);
      },
    });
  }
}
