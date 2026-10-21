export type ExerciseKind = 'LOAD' | 'HOLD';

export interface Exercise {
  id: number;
  name: string;
  muscleGroup: string;
  demoUrl: string | null;
  kind: ExerciseKind;
  hidden: boolean;
}

export interface Food {
  id: number;
  name: string;
  kcalPer100g: number;
  proteinPer100g: number;
  carbsPer100g: number;
  fatPer100g: number;
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

export interface DiaryEntry {
  id: number;
  eatenOn: string;
  mealSlot: 'BREAKFAST' | 'LUNCH' | 'DINNER' | 'SNACK';
  foodId: number;
  foodName: string;
  grams: number;
  kcal: number;
  proteinG: number;
  carbsG: number;
  fatG: number;
}

export interface DiaryDay {
  date: string;
  entries: DiaryEntry[];
  totalKcal: number;
  totalProteinG: number;
  totalCarbsG: number;
  totalFatG: number;
}

export interface FeedbackMessage {
  id: number;
  channel: 'MUSCU' | 'NUTRITION';
  body: string;
  authorName: string;
  authorRole: string;
  createdAt: string;
}

export interface WorkoutLog {
  id: number;
  performedOn: string;
  done: boolean;
  comment: string | null;
  planId: number | null;
}
