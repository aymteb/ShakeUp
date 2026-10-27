import {
  Component,
  ElementRef,
  HostListener,
  OnDestroy,
  OnInit,
  computed,
  signal,
  viewChild,
} from '@angular/core';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { Subject, debounceTime, distinctUntilChanged, switchMap } from 'rxjs';
import { EleveApi } from './eleve-api';
import { DiaryDay, DiaryEntry, Food, NutritionTarget } from '../shared/api-types';

type MealSlot = DiaryEntry['mealSlot'];
type QtyMode = 'g' | 'unit';

@Component({
  selector: 'app-eleve-nutrition-page',
  imports: [FormsModule, RouterLink],
  templateUrl: './eleve-nutrition-page.html',
  styleUrl: './eleve-nutrition-page.css',
})
export class EleveNutritionPage implements OnInit, OnDestroy {
  readonly target = signal<NutritionTarget | null>(null);
  readonly diary = signal<DiaryDay | null>(null);
  readonly foods = signal<Food[]>([]);
  readonly searching = signal(false);
  readonly query = signal('');
  readonly mealSlot = signal<MealSlot>('LUNCH');
  readonly gramsDraft = signal('');
  readonly qtyMode = signal<QtyMode>('g');
  readonly selectedFood = signal<Food | null>(null);
  readonly adding = signal(false);
  readonly error = signal('');
  readonly fieldError = signal('');
  readonly toast = signal('');
  readonly ok = signal('');

  readonly scanning = signal(false);
  readonly scanSupported = signal(false);
  readonly barcodeLoading = signal(false);

  readonly swipeId = signal<number | null>(null);
  readonly swipeX = signal(0);
  readonly swiping = signal(false);

  readonly videoRef = viewChild<ElementRef<HTMLVideoElement>>('scanVideo');

  readonly padKeys = ['1', '2', '3', '4', '5', '6', '7', '8', '9', 'C', '0', '⌫'] as const;
  readonly gramPresets = [30, 50, 100, 150];
  readonly unitPresets = [1, 2, 3];

  private readonly search$ = new Subject<string>();
  private mediaStream: MediaStream | null = null;
  private scanLoop: number | null = null;
  private detector: BarcodeDetector | null = null;
  private lastScan = '';
  private toastTimer: ReturnType<typeof setTimeout> | null = null;
  private swipeStartX = 0;
  private swipeActive = false;
  private swipeEntryId: number | null = null;

  readonly kcalLeft = computed(() => this.left(this.target()?.caloriesKcal, this.diary()?.totalKcal));
  readonly proteinLeft = computed(() => this.left(this.target()?.proteinG, this.diary()?.totalProteinG));
  readonly carbsLeft = computed(() => this.left(this.target()?.carbsG, this.diary()?.totalCarbsG));
  readonly fatLeft = computed(() => this.left(this.target()?.fatG, this.diary()?.totalFatG));

  readonly kcalPct = computed(() => this.pct(this.diary()?.totalKcal, this.target()?.caloriesKcal));
  readonly proteinPct = computed(() =>
    this.pct(this.diary()?.totalProteinG, this.target()?.proteinG),
  );
  readonly carbsPct = computed(() => this.pct(this.diary()?.totalCarbsG, this.target()?.carbsG));
  readonly fatPct = computed(() => this.pct(this.diary()?.totalFatG, this.target()?.fatG));

  readonly qtyValue = computed(() => {
    const n = Number.parseInt(this.gramsDraft(), 10);
    return Number.isFinite(n) ? n : 0;
  });

  readonly servingGrams = computed(() => {
    const s = this.selectedFood()?.servingGrams;
    return s != null && s > 0 ? s : null;
  });

  readonly canUseUnit = computed(() => this.servingGrams() != null);

  readonly grams = computed(() => {
    const qty = this.qtyValue();
    if (qty < 1) {
      return 0;
    }
    if (this.qtyMode() === 'unit') {
      const serving = this.servingGrams();
      return serving == null ? 0 : Math.round(qty * serving);
    }
    return qty;
  });

  readonly previewKcal = computed(() => {
    const f = this.selectedFood();
    if (!f || this.grams() < 1) {
      return null;
    }
    return Math.round((f.kcalPer100g * this.grams()) / 100);
  });

  readonly meals: { id: MealSlot; label: string }[] = [
    { id: 'BREAKFAST', label: 'Matin' },
    { id: 'LUNCH', label: 'Midi' },
    { id: 'DINNER', label: 'Soir' },
    { id: 'SNACK', label: 'Collation' },
  ];

  constructor(private readonly api: EleveApi) {
    this.search$
      .pipe(
        debounceTime(320),
        distinctUntilChanged(),
        switchMap((q) => {
          this.searching.set(true);
          if (this.isEan(q) || q.trim().length < 2) {
            return this.api.foods();
          }
          return this.api.foods(q);
        }),
      )
      .subscribe({
        next: (list) => {
          this.foods.set(list);
          this.searching.set(false);
        },
        error: () => {
          this.searching.set(false);
          this.error.set('Recherche aliments impossible');
        },
      });
  }

  ngOnInit() {
    this.scanSupported.set(typeof window !== 'undefined' && !!window.BarcodeDetector);
    this.reload();
    this.search$.next('');
  }

  ngOnDestroy() {
    void this.stopScanner();
    this.unlockScroll();
    if (this.toastTimer) {
      clearTimeout(this.toastTimer);
    }
  }

  @HostListener('document:keydown.escape')
  onEscape() {
    if (this.selectedFood()) {
      this.closeSheet();
    }
  }

  onQuery(value: string) {
    this.query.set(value);
    this.fieldError.set('');
    if (!this.isEan(value)) {
      this.search$.next(value);
    }
  }

  onSearchSubmit() {
    const q = this.query().trim();
    if (!q) {
      return;
    }
    if (this.isEan(q)) {
      this.resolveBarcode(q);
      return;
    }
    this.search$.next(q);
  }

  selectFood(food: Food) {
    this.selectedFood.set(food);
    this.error.set('');
    if (food.servingGrams != null && food.servingGrams > 0) {
      this.qtyMode.set('unit');
      this.gramsDraft.set('1');
    } else {
      this.qtyMode.set('g');
      this.gramsDraft.set('');
    }
    this.lockScroll();
  }

  closeSheet() {
    this.selectedFood.set(null);
    this.gramsDraft.set('');
    this.qtyMode.set('g');
    this.adding.set(false);
    this.unlockScroll();
  }

  setQtyMode(mode: QtyMode) {
    if (mode === 'unit' && !this.canUseUnit()) {
      this.showToast('Portion inconnue pour cet aliment — reste en grammes.');
      return;
    }
    if (mode === this.qtyMode()) {
      return;
    }
    this.qtyMode.set(mode);
    this.gramsDraft.set(mode === 'unit' ? '1' : '');
  }

  setPreset(value: number) {
    this.gramsDraft.set(String(value));
  }

  padTap(key: string) {
    const maxLen = this.qtyMode() === 'unit' ? 2 : 4;
    if (key === 'C') {
      this.gramsDraft.set('');
      return;
    }
    if (key === '⌫') {
      this.gramsDraft.update((d) => d.slice(0, -1));
      return;
    }
    this.gramsDraft.update((d) => {
      if (d.length >= maxLen) {
        return d;
      }
      if (d === '0') {
        return key;
      }
      return d + key;
    });
  }

  async toggleScanner() {
    if (this.scanning()) {
      await this.stopScanner();
      return;
    }
    await this.startScanner();
  }

  async startScanner() {
    this.fieldError.set('');
    if (!window.BarcodeDetector) {
      this.showToast('Scan non supporté — saisis le code EAN puis Entrée.');
      this.fieldError.set('Scan caméra non supporté — saisis le code EAN puis Entrée.');
      return;
    }
    try {
      this.detector = new window.BarcodeDetector({
        formats: ['ean_13', 'ean_8', 'upc_a', 'upc_e', 'code_128'],
      });
      this.mediaStream = await navigator.mediaDevices.getUserMedia({
        audio: false,
        video: { facingMode: { ideal: 'environment' } },
      });
      this.scanning.set(true);
      queueMicrotask(() => {
        const video = this.videoRef()?.nativeElement;
        if (!video || !this.mediaStream) {
          return;
        }
        video.srcObject = this.mediaStream;
        void video.play();
        this.tickScan();
      });
    } catch {
      this.showToast('Caméra inaccessible (permission ou appareil).');
      this.fieldError.set('Caméra inaccessible (permission ou appareil).');
      await this.stopScanner();
    }
  }

  async stopScanner() {
    if (this.scanLoop != null) {
      cancelAnimationFrame(this.scanLoop);
      this.scanLoop = null;
    }
    this.mediaStream?.getTracks().forEach((t) => t.stop());
    this.mediaStream = null;
    this.detector = null;
    this.scanning.set(false);
    this.lastScan = '';
  }

  add() {
    const food = this.selectedFood();
    if (!food) {
      return;
    }
    if (this.grams() < 1) {
      this.showToast(this.qtyMode() === 'unit' ? 'Indique le nombre d’unités.' : 'Indique les grammes.');
      return;
    }
    this.adding.set(true);
    this.api
      .addDiary({
        eatenOn: this.api.today(),
        mealSlot: this.mealSlot(),
        foodId: food.id,
        grams: this.grams(),
      })
      .subscribe({
        next: () => {
          this.adding.set(false);
          this.error.set('');
          this.ok.set(`${food.name} · ${this.grams()} g`);
          this.closeSheet();
          this.reload();
        },
        error: () => {
          this.adding.set(false);
          this.error.set('Ajout impossible');
        },
      });
  }

  remove(id: number) {
    this.resetSwipe();
    this.api.deleteDiary(id).subscribe({
      next: () => this.reload(),
      error: () => this.error.set('Suppression impossible'),
    });
  }

  onSwipeStart(event: PointerEvent, id: number) {
    if (event.pointerType === 'mouse' && event.button !== 0) {
      return;
    }
    this.swipeActive = true;
    this.swiping.set(true);
    this.swipeEntryId = id;
    this.swipeStartX = event.clientX;
    if (this.swipeId() !== id) {
      this.swipeId.set(id);
      this.swipeX.set(0);
    }
    (event.currentTarget as HTMLElement).setPointerCapture?.(event.pointerId);
  }

  onSwipeMove(event: PointerEvent) {
    if (!this.swipeActive || this.swipeEntryId == null) {
      return;
    }
    const dx = event.clientX - this.swipeStartX;
    this.swipeX.set(Math.max(-112, Math.min(0, dx)));
  }

  onSwipeEnd() {
    if (!this.swipeActive) {
      return;
    }
    this.swipeActive = false;
    this.swiping.set(false);
    if (this.swipeX() <= -72) {
      this.swipeX.set(-96);
    } else {
      this.resetSwipe();
    }
  }

  entryShift(id: number): number {
    return this.swipeId() === id ? this.swipeX() : 0;
  }

  entriesFor(slot: MealSlot): DiaryEntry[] {
    return this.diary()?.entries.filter((e) => e.mealSlot === slot) ?? [];
  }

  formatLeft(value: number | null): string {
    if (value == null) {
      return '—';
    }
    return String(value);
  }

  reload() {
    this.api.target().subscribe({ next: (t) => this.target.set(t) });
    this.api.diary(this.api.today()).subscribe({ next: (d) => this.diary.set(d) });
  }

  private resetSwipe() {
    this.swipeId.set(null);
    this.swipeX.set(0);
    this.swipeEntryId = null;
  }

  private tickScan() {
    this.scanLoop = requestAnimationFrame(async () => {
      if (!this.scanning() || !this.detector) {
        return;
      }
      const video = this.videoRef()?.nativeElement;
      if (video && video.readyState >= 2) {
        try {
          const codes = await this.detector.detect(video);
          const value = codes[0]?.rawValue;
          if (value && value !== this.lastScan) {
            this.lastScan = value;
            this.query.set(value);
            this.resolveBarcode(value);
            await this.stopScanner();
            return;
          }
        } catch {
          // keep scanning
        }
      }
      this.tickScan();
    });
  }

  private resolveBarcode(code: string) {
    this.barcodeLoading.set(true);
    this.query.set(code);
    this.fieldError.set('');
    this.error.set('');
    this.api.foodByBarcode(code).subscribe({
      next: (food) => {
        this.barcodeLoading.set(false);
        this.selectFood(food);
      },
      error: () => {
        this.barcodeLoading.set(false);
        this.fieldError.set(
          'Produit introuvable (OFF indisponible ou code inconnu). Réessaie ou cherche par nom.',
        );
      },
    });
  }

  private isEan(value: string): boolean {
    return /^\d{8,14}$/.test(value.trim());
  }

  private left(target: number | null | undefined, current: number | null | undefined): number | null {
    if (target == null) {
      return null;
    }
    return Math.round(target - (current ?? 0));
  }

  private pct(current: number | null | undefined, target: number | null | undefined): number {
    if (current == null || target == null || target <= 0) {
      return 0;
    }
    return Math.min(100, Math.round((current / target) * 100));
  }

  private lockScroll() {
    document.body.style.overflow = 'hidden';
  }

  private unlockScroll() {
    document.body.style.overflow = '';
  }

  private showToast(message: string) {
    this.toast.set(message);
    if (this.toastTimer) {
      clearTimeout(this.toastTimer);
    }
    this.toastTimer = setTimeout(() => {
      this.toast.set('');
      this.toastTimer = null;
    }, 3800);
  }
}
