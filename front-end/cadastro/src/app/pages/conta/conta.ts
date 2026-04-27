import { CommonModule } from '@angular/common';
import { Component, OnInit, inject } from '@angular/core';
import { ContaService } from '../../services/conta.service';
import { PluggyAccountDTO } from '../../models/conta';
import { map, startWith, catchError, of, shareReplay } from 'rxjs';
import { RouterModule } from '@angular/router';

type ContaVm = {
  loading: boolean;
  errorMessage: string;
  accounts: PluggyAccountDTO[];
};

@Component({
  selector: 'app-conta',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './conta.html',
  styleUrl: './conta.css',
})
export class Conta {
  private readonly contaService = inject(ContaService);

  readonly vm$ = this.contaService.findMyAccounts().pipe(
    map((accounts) => ({
      loading: false,
      errorMessage: '',
      accounts: Array.isArray(accounts) ? accounts : [],
    }) satisfies ContaVm),
    startWith({
      loading: true,
      errorMessage: '',
      accounts: [],
    } satisfies ContaVm),
    catchError((error) => {
      console.error('[ContaComponent] erro ao carregar contas', error);

      return of({
        loading: false,
        errorMessage: 'Não foi possível carregar as contas.',
        accounts: [],
      } satisfies ContaVm);
    }),
    shareReplay({ bufferSize: 1, refCount: true })
  );
}
