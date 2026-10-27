import { Component } from '@angular/core';
import { RouterLink, RouterLinkActive, RouterOutlet } from '@angular/router';
import { AuthService } from '../core/auth.service';

@Component({
  selector: 'app-coach-shell',
  imports: [RouterOutlet, RouterLink, RouterLinkActive],
  templateUrl: './coach-shell.html',
  styleUrl: './coach-shell.css',
})
export class CoachShell {
  constructor(readonly auth: AuthService) {}
}
