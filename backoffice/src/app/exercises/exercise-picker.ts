import {
  Component,
  ElementRef,
  HostListener,
  effect,
  input,
  output,
  signal,
  viewChild,
  computed,
  untracked,
} from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Exercise } from '../shared/api-types';
import { BodyMap } from './body-map';
import { MuscleGroup } from './muscle-groups';

@Component({
  selector: 'app-exercise-picker',
  imports: [FormsModule, BodyMap],
  templateUrl: './exercise-picker.html',
  styleUrl: './exercise-picker.css',
})
export class ExercisePicker {
  readonly open = input(false);
  readonly exercises = input<Exercise[]>([]);
  readonly closed = output<void>();
  readonly picked = output<Exercise>();

  readonly query = signal('');
  readonly muscle = signal<MuscleGroup | null>(null);
  readonly activeId = signal<number | null>(null);
  readonly preview = signal<Exercise | null>(null);

  readonly searchRef = viewChild<ElementRef<HTMLInputElement>>('searchInput');

  readonly filtered = computed(() => {
    const q = this.query().trim().toLowerCase();
    const muscle = this.muscle();
    return this.exercises().filter((e) => {
      if (muscle && e.muscleGroup !== muscle) {
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

  readonly resultCount = computed(() => this.filtered().length);

  constructor() {
    effect(() => {
      const isOpen = this.open();
      if (!isOpen) {
        return;
      }
      untracked(() => {
        this.query.set('');
        this.muscle.set(null);
        this.preview.set(null);
        this.activeId.set(this.exercises()[0]?.id ?? null);
      });
      queueMicrotask(() => this.searchRef()?.nativeElement.focus());
    });
  }

  @HostListener('document:keydown', ['$event'])
  onKey(event: KeyboardEvent) {
    if (!this.open()) {
      return;
    }
    if (event.key === 'Escape') {
      event.preventDefault();
      this.closed.emit();
      return;
    }
    const list = this.filtered();
    if (!list.length) {
      return;
    }
    const idx = list.findIndex((e) => e.id === this.activeId());
    if (event.key === 'ArrowDown') {
      event.preventDefault();
      const next = list[Math.min(Math.max(idx, -1) + 1, list.length - 1)] ?? list[0];
      this.activate(next);
    } else if (event.key === 'ArrowUp') {
      event.preventDefault();
      const prev = list[Math.max(idx - 1, 0)] ?? list[0];
      this.activate(prev);
    } else if (event.key === 'Enter') {
      event.preventDefault();
      const ex = list.find((e) => e.id === this.activeId()) ?? list[0];
      if (ex) {
        this.select(ex);
      }
    }
  }

  setMuscle(group: MuscleGroup | null) {
    this.muscle.set(group);
    queueMicrotask(() => this.activate(this.filtered()[0] ?? null));
  }

  onQuery(value: string) {
    this.query.set(value);
    queueMicrotask(() => this.activate(this.filtered()[0] ?? null));
  }

  clearMuscle() {
    this.setMuscle(null);
  }

  activate(ex: Exercise | null) {
    this.activeId.set(ex?.id ?? null);
    this.preview.set(ex?.demoUrl ? ex : null);
  }

  select(ex: Exercise) {
    this.picked.emit(ex);
    this.closed.emit();
  }

  backdropClose() {
    this.closed.emit();
  }
}
