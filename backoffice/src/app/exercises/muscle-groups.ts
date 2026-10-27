export const MUSCLE_GROUPS = [
  'Pectoraux',
  'Dos',
  'Épaules',
  'Bras',
  'Jambes',
  'Mollets',
  'Core',
  'Cardio',
] as const;

export type MuscleGroup = (typeof MUSCLE_GROUPS)[number];

export const MUSCLE_LABELS: Record<MuscleGroup, string> = {
  Pectoraux: 'Pectoraux',
  Dos: 'Dos',
  Épaules: 'Épaules',
  Bras: 'Bras',
  Jambes: 'Jambes',
  Mollets: 'Mollets',
  Core: 'Core',
  Cardio: 'Cardio',
};
