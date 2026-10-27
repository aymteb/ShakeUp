import { Component, computed } from '@angular/core';
import { AuthService } from '../core/auth.service';

@Component({
  selector: 'app-eleve-bloque-page',
  templateUrl: './eleve-bloque-page.html',
  styleUrl: './eleve-bloque-page.css',
})
export class EleveBloquePage {
  readonly endsOn = computed(() => this.auth.session()?.coachingEndsOn ?? null);
  readonly displayName = computed(() => this.auth.session()?.displayName ?? '');
  readonly coachEmail = computed(() => this.auth.session()?.coachEmail ?? null);
  readonly coachDisplayName = computed(
    () => this.auth.session()?.coachDisplayName ?? 'ton coach',
  );

  readonly mailtoHref = computed(() => {
    const email = this.coachEmail();
    if (!email) {
      return null;
    }
    const name = this.displayName() || 'élève';
    const subject = encodeURIComponent(`Reprise de coaching — ${name}`);
    const body = encodeURIComponent(
      `Bonjour,\n\nMa période de coaching est terminée et je souhaiterais discuter de la suite.\n\nMerci,\n${name}`,
    );
    return `mailto:${email}?subject=${subject}&body=${body}`;
  });

  constructor(readonly auth: AuthService) {}

  formatDate(iso: string | null): string {
    if (!iso) {
      return '';
    }
    const d = new Date(iso + 'T12:00:00');
    if (Number.isNaN(d.getTime())) {
      return iso;
    }
    return d.toLocaleDateString('fr-FR', {
      day: 'numeric',
      month: 'long',
      year: 'numeric',
    });
  }
}
