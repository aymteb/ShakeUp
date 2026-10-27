import { Component, OnInit, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { ElevesApi } from './eleves-api';
import { Eleve } from '../shared/api-types';

@Component({
  selector: 'app-eleves-list-page',
  imports: [ReactiveFormsModule, RouterLink],
  templateUrl: './eleves-list-page.html',
  styleUrl: './eleves-list-page.css',
})
export class ElevesListPage implements OnInit {
  readonly eleves = signal<Eleve[]>([]);
  readonly error = signal('');
  readonly loading = signal(true);
  readonly showCreate = signal(false);
  readonly form;

  constructor(
    fb: FormBuilder,
    private readonly api: ElevesApi,
  ) {
    this.form = fb.nonNullable.group({
      displayName: ['', Validators.required],
      email: ['', [Validators.required, Validators.email]],
      temporaryPassword: ['', [Validators.required, Validators.minLength(6)]],
    });
  }

  ngOnInit() {
    this.reload();
  }

  reload() {
    this.loading.set(true);
    this.api.listEleves().subscribe({
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

  openCreate() {
    this.error.set('');
    this.form.reset({ displayName: '', email: '', temporaryPassword: '' });
    this.showCreate.set(true);
  }

  closeCreate() {
    this.showCreate.set(false);
  }

  create() {
    this.form.markAllAsTouched();
    if (this.form.invalid) {
      this.error.set('Nom, email valide et mot de passe (6 car. min).');
      return;
    }
    this.error.set('');
    this.api.createEleve(this.form.getRawValue()).subscribe({
      next: () => {
        this.showCreate.set(false);
        this.reload();
      },
      error: (err) => {
        this.error.set(err.status === 409 ? 'Email déjà utilisé' : 'Création impossible');
      },
    });
  }

  remove(eleve: Eleve, event: Event) {
    event.preventDefault();
    event.stopPropagation();
    if (!confirm(`Supprimer ${eleve.displayName} ?`)) {
      return;
    }
    this.api.deleteEleve(eleve.id).subscribe({
      next: () => this.reload(),
      error: () => this.error.set('Suppression impossible'),
    });
  }
}
