import { Component, OnInit, computed, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { CataloguesApi } from './catalogues-api';
import { CatalogSource, Exercise } from '../shared/api-types';

@Component({
  selector: 'app-catalogues-page',
  imports: [FormsModule],
  templateUrl: './catalogues-page.html',
  styleUrl: './catalogues-page.css',
})
export class CataloguesPage implements OnInit {
  readonly exercises = signal<Exercise[]>([]);
  readonly loading = signal(true);
  readonly syncing = signal(false);
  readonly error = signal('');
  readonly syncMessage = signal('');
  readonly query = signal('');
  readonly sourceFilter = signal<'ALL' | CatalogSource>('ALL');
  readonly showHidden = signal(true);

  readonly filtered = computed(() => {
    const q = this.query().trim().toLowerCase();
    const source = this.sourceFilter();
    const showHidden = this.showHidden();
    return this.exercises().filter((e) => {
      if (!showHidden && e.hidden) {
        return false;
      }
      if (source !== 'ALL' && e.source !== source) {
        return false;
      }
      if (!q) {
        return true;
      }
      return (
        e.name.toLowerCase().includes(q) ||
        e.muscleGroup.toLowerCase().includes(q)
      );
    });
  });

  readonly stats = computed(() => {
    const all = this.exercises();
    return {
      total: all.length,
      wger: all.filter((e) => e.source === 'WGER').length,
      custom: all.filter((e) => e.source === 'CUSTOM').length,
      hidden: all.filter((e) => e.hidden).length,
    };
  });

  constructor(private readonly api: CataloguesApi) {}

  ngOnInit() {
    this.reload();
  }

  reload() {
    this.loading.set(true);
    this.error.set('');
    this.api.exercises().subscribe({
      next: (list) => {
        this.exercises.set(list);
        this.loading.set(false);
      },
      error: () => {
        this.error.set('Impossible de charger le catalogue.');
        this.loading.set(false);
      },
    });
  }

  sync() {
    this.syncing.set(true);
    this.error.set('');
    this.syncMessage.set('');
    this.api.syncWger().subscribe({
      next: (r) => {
        this.syncMessage.set(
          `Sync Wger : ${r.totalFetched} lus · ${r.created} créés · ${r.updated} maj · ${r.skipped} inchangés`,
        );
        this.syncing.set(false);
        this.reload();
      },
      error: () => {
        this.error.set('Sync Wger impossible (réseau ou API).');
        this.syncing.set(false);
      },
    });
  }

  toggleHidden(ex: Exercise) {
    this.api.setHidden(ex.id, !ex.hidden).subscribe({
      next: (updated) => {
        this.exercises.update((list) =>
          list.map((e) => (e.id === updated.id ? updated : e)),
        );
      },
      error: () => this.error.set('Mise à jour impossible'),
    });
  }

  sourceLabel(source: CatalogSource): string {
    switch (source) {
      case 'WGER':
        return 'Wger';
      case 'CUSTOM':
        return 'Perso';
      case 'SEED':
        return 'Seed';
    }
  }
}
