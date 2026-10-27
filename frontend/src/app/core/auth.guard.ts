import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { catchError, map, of } from 'rxjs';
import { AuthService, UserRole } from './auth.service';

export function authGuard(...roles: UserRole[]): CanActivateFn {
  return () => {
    const auth = inject(AuthService);
    const router = inject(Router);
    const session = auth.session();
    if (!session) {
      return router.createUrlTree(['/login']);
    }
    if (roles.length > 0 && !roles.includes(session.role)) {
      return router.createUrlTree(['/login']);
    }
    return true;
  };
}

export const eleveAppGuard: CanActivateFn = () => {
  const auth = inject(AuthService);
  const router = inject(Router);
  const session = auth.session();
  if (!session || session.role !== 'ELEVE') {
    return router.createUrlTree(['/login']);
  }
  if (session.mustChangePassword) {
    return router.createUrlTree(['/password']);
  }
  return auth.fetchAccessStatus().pipe(
    map((status) =>
      status.coachingActive ? true : router.createUrlTree(['/eleve/bloque']),
    ),
    catchError(() => {
      auth.clearSession();
      return of(router.createUrlTree(['/login']));
    }),
  );
};

export const coachingBlockedGuard: CanActivateFn = () => {
  const auth = inject(AuthService);
  const router = inject(Router);
  const session = auth.session();
  if (!session || session.role !== 'ELEVE') {
    return router.createUrlTree(['/login']);
  }
  if (session.mustChangePassword) {
    return router.createUrlTree(['/password']);
  }
  return auth.fetchAccessStatus().pipe(
    map((status) =>
      status.coachingActive ? router.createUrlTree(['/eleve']) : true,
    ),
    catchError(() => {
      auth.clearSession();
      return of(router.createUrlTree(['/login']));
    }),
  );
};
