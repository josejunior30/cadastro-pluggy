
import { CommonModule } from '@angular/common';
import { Component, inject } from '@angular/core';
import { RouterModule } from '@angular/router';
import { catchError, map, of, shareReplay, startWith } from 'rxjs';

import { ContaService } from '../../services/conta.service';
import { PluggyAccountDTO } from '../../models/conta';
import { MenuComponent } from '../../components/menu/menu';

type ContaVm = {
  loading: boolean;
  errorMessage: string;
  accounts: PluggyAccountDTO[];
};

@Component({
  selector: 'app-conta',
  standalone: true,
  imports: [CommonModule, RouterModule,[MenuComponent]],
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

  formatSubtype(subtype: string | null | undefined): string {
    if (!subtype) {
      return 'Não informado';
    }

    const normalizedSubtype = subtype.trim().toUpperCase();

    switch (normalizedSubtype) {
      case 'CHECKING_ACCOUNT':
        return 'Conta corrente';
      case 'CREDIT_CARD':
        return 'Cartão de Crédito';
      default:
        return subtype;
    }
  }
}
