import { Component, Input, inject } from '@angular/core';
import { NgIf } from '@angular/common';
import { Router, RouterLink, RouterLinkActive } from '@angular/router';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-menu',
  standalone: true,
  imports: [NgIf, RouterLink, RouterLinkActive],
  templateUrl: './menu.html',
})
export class MenuComponent {
  @Input() userName = 'Usuário';
  @Input() userEmail = '';
  @Input() avatarUrl = '';

  private readonly authService = inject(AuthService);
  private readonly router = inject(Router);

  get userInitials(): string {
    return this.userName
      .trim()
      .split(' ')
      .slice(0, 2)
      .map((name) => name.charAt(0).toUpperCase())
      .join('');
  }

  logout(): void {
    this.authService.logout();
    this.router.navigate(['/']);
  }
}
