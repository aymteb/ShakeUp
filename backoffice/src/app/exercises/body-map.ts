import { Component, computed, input, output, signal } from '@angular/core';
import { MuscleGroup } from './muscle-groups';
import {
  BODY_BACK,
  BODY_FRONT,
  BODY_OUTLINE,
  BODY_VIEWBOX,
  BodyPartPaths,
} from './body-paths';

const SLUG_TO_MUSCLE: Record<string, MuscleGroup | null> = {
  chest: 'Pectoraux',
  abs: 'Core',
  obliques: 'Core',
  deltoids: 'Épaules',
  biceps: 'Bras',
  triceps: 'Bras',
  forearm: 'Bras',
  quadriceps: 'Jambes',
  adductors: 'Jambes',
  tibialis: 'Jambes',
  knees: 'Jambes',
  hamstring: 'Jambes',
  gluteal: 'Jambes',
  calves: 'Mollets',
  'upper-back': 'Dos',
  'lower-back': 'Dos',
  trapezius: 'Dos',
  neck: null,
  head: null,
  hair: null,
  hands: null,
  feet: null,
  ankles: null,
};

type Side = 'front' | 'back';

@Component({
  selector: 'app-body-map',
  templateUrl: './body-map.html',
  styleUrl: './body-map.css',
})
export class BodyMap {
  readonly selected = input<MuscleGroup | null>(null);
  readonly muscleSelect = output<MuscleGroup | null>();

  readonly side = signal<Side>('front');

  readonly viewBox = computed(() => BODY_VIEWBOX[this.side()]);
  readonly outline = computed(() => BODY_OUTLINE[this.side()]);
  readonly parts = computed(() => (this.side() === 'front' ? BODY_FRONT : BODY_BACK));

  setSide(side: Side) {
    this.side.set(side);
  }

  muscleFor(slug: string): MuscleGroup | null {
    return SLUG_TO_MUSCLE[slug] ?? null;
  }

  isInteractive(part: BodyPartPaths): boolean {
    return this.muscleFor(part.slug) != null;
  }

  isOn(part: BodyPartPaths): boolean {
    const muscle = this.muscleFor(part.slug);
    return muscle != null && this.selected() === muscle;
  }

  pickPart(part: BodyPartPaths) {
    const muscle = this.muscleFor(part.slug);
    if (!muscle) {
      return;
    }
    this.muscleSelect.emit(this.selected() === muscle ? null : muscle);
  }

  pickCardio() {
    this.muscleSelect.emit(this.selected() === 'Cardio' ? null : 'Cardio');
  }

  clear() {
    if (this.selected()) {
      this.muscleSelect.emit(null);
    }
  }
}
