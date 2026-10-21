import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
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
