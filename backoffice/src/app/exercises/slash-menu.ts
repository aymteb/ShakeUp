import {
  Component,
  HostListener,
  computed,
  effect,
  input,
  output,
  signal,
  ElementRef,
  viewChild,
} from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Exercise } from '../shared/api-types';

@Component({
  selector: 'app-slash-menu',
  imports: [FormsModule],
  templateUrl: './slash-menu.html',
  styleUrl: './slash-menu.css',
})
export class SlashMenu {
  readonly open = input(false);
  readonly exercises = input<Exercise[]>([]);
  readonly closed = output<void>();
  readonly picked = output<Exercise>();

  readonly query = signal('');
  readonly activeIndex = signal(0);
  readonly preview = signal<Exercise | null>(null);
  readonly inputRef = viewChild<ElementRef<HTMLInputElement>>('slashInput');

  readonly matches = computed(() => {
    const q = this.query().trim().toLowerCase();
    const all = this.exercises();
    if (!q) {
      return all.slice(0, 12);
    }
    return all
      .filter(
        (e) =>
          e.name.toLowerCase().includes(q) ||
          e.muscleGroup.toLowerCase().includes(q),
      )
      .slice(0, 12);
  });

  constructor() {
    effect(() => {
      if (!this.open()) {
        return;
      }
      this.query.set('');
      this.activeIndex.set(0);
      this.preview.set(null);
      queueMicrotask(() => this.inputRef()?.nativeElement.focus());
    });
  }

  @HostListener('document:keydown', ['$event'])
  onKey(event: KeyboardEvent) {
    if (!this.open()) {
      return;
    }
    const list = this.matches();
    if (event.key === 'Escape') {
      event.preventDefault();
      this.closed.emit();
      return;
    }
    if (event.key === 'ArrowDown') {
      event.preventDefault();
      this.activeIndex.update((i) => Math.min(i + 1, Math.max(list.length - 1, 0)));
      this.syncPreview();
    } else if (event.key === 'ArrowUp') {
      event.preventDefault();
      this.activeIndex.update((i) => Math.max(i - 1, 0));
      this.syncPreview();
    } else if (event.key === 'Enter') {
      event.preventDefault();
      const ex = list[this.activeIndex()];
      if (ex) {
        this.select(ex);
      }
    }
  }

  setQuery(value: string) {
    this.query.set(value.startsWith('/') ? value.slice(1) : value);
    this.activeIndex.set(0);
    queueMicrotask(() => this.syncPreview());
  }

  highlight(index: number) {
    this.activeIndex.set(index);
    this.syncPreview();
  }

  select(ex: Exercise) {
    this.picked.emit(ex);
    this.closed.emit();
  }

  private syncPreview() {
    const ex = this.matches()[this.activeIndex()];
    this.preview.set(ex?.demoUrl ? ex : null);
  }
}
