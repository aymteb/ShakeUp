import { HttpErrorResponse, HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { Router } from '@angular/router';
import { catchError, throwError } from 'rxjs';
import { AuthService } from './auth.service';

export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const auth = inject(AuthService);
  const router = inject(Router);
  const token = auth.token();
  const authed = token
    ? req.clone({ setHeaders: { Authorization: `Bearer ${token}` } })
    : req;

  return next(authed).pipe(
    catchError((err: unknown) => {
      if (
        err instanceof HttpErrorResponse &&
        err.status === 403 &&
        isCoachingEnded(err)
      ) {
        auth.markCoachingEnded();
        void router.navigateByUrl('/eleve/bloque');
      }
      return throwError(() => err);
    }),
  );
};

function isCoachingEnded(err: HttpErrorResponse): boolean {
  const body = err.error;
  if (typeof body === 'string') {
    return body.includes('COACHING_ENDED');
  }
  if (body && typeof body === 'object' && 'code' in body) {
    return (body as { code?: string }).code === 'COACHING_ENDED';
  }
  return false;
}
