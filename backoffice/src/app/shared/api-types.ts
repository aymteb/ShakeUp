export type ExerciseKind = 'LOAD' | 'HOLD';
export type CatalogSource = 'SEED' | 'WGER' | 'CUSTOM';

export type CoachingWeekKind = 'FULL' | 'COUPURE';

export interface Eleve {
  id: number;
  email: string;
  displayName: string;
  mustChangePassword: boolean;
  createdAt: string;
  coachingStartsOn: string | null;
  coachingEndsOn: string | null;
  coachingActive: boolean;
}

export interface CoachingWeek {
  isoYear: number;
  isoWeek: number;
  kind: CoachingWeekKind;
  note: string | null;
}

export interface Exercise {
  id: number;
  name: string;
  muscleGroup: string;
  demoUrl: string | null;
  kind: ExerciseKind;
  source: CatalogSource;
  externalId: number | null;
  hidden: boolean;
}

export interface WgerSyncResult {
  created: number;
  updated: number;
  skipped: number;
  totalFetched: number;
}

export interface PlanItem {
  id?: number;
  exerciseId: number;
  exerciseName?: string;
  muscleGroup?: string;
  exerciseKind?: ExerciseKind;
  setsTarget: number;
  repsTarget?: number | null;
  weightKg?: number | null;
  durationSeconds?: number | null;
  restSeconds: number;
  sortOrder?: number;
}

export interface Plan {
  id: number;
  title: string;
  notes: string | null;
  items: PlanItem[];
}

export interface NutritionTarget {
  id: number;
  caloriesKcal: number;
  proteinG: number | null;
  carbsG: number | null;
  fatG: number | null;
  coachNote: string | null;
}
