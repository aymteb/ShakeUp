import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AuthService } from './auth.service';

export const coachGuard: CanActivateFn = () => {
  const auth = inject(AuthService);
  const router = inject(Router);
  const session = auth.session();
  if (!session) {
    return router.createUrlTree(['/login']);
  }
  if (session.role !== 'COACH') {
    auth.logout();
    return router.createUrlTree(['/login']);
  }
  return true;
};
