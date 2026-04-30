import { CommonModule } from '@angular/common';
import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { finalize } from 'rxjs';
import { AuthService } from '../../services/auth.service';


@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './login.html',
  styleUrls: ['./login.css'],
})
export class Login {
  private readonly authService = inject(AuthService);
  private readonly router = inject(Router);

  email = '';
  senha = '';

  carregando = false;
  erro = '';

  entrar(): void {
    this.erro = '';

    if (!this.email || !this.senha) {
      this.erro = 'Informe email e senha.';
      return;
    }

    this.carregando = true;

    this.authService
      .login(this.email, this.senha)
      .pipe(finalize(() => (this.carregando = false)))
      .subscribe({
        next: () => {
          this.router.navigate(['/boas-vindas']);
        },
        error: (err) => {
          console.error(err);
          this.erro = 'Email ou senha inválidos.';
        },
      });
  }
}
