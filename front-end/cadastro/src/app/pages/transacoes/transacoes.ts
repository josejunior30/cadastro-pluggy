import { CommonModule } from '@angular/common';
import { Component, inject } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import {
  BehaviorSubject,
  combineLatest,
  map,
  switchMap,
  startWith,
  catchError,
  of,
  shareReplay,
  distinctUntilChanged,
} from 'rxjs';

import { PluggyTransactionDTO } from '../../models/conta';
import { TransacoesService } from '../../services/transacoes.service';
import { PaginationComponent } from '../../components/pagination/pagination';
import { BackButtonComponent } from '../../components/back-button/back-button';
import { MenuComponent } from '../../components/menu/menu';

type TransacoesVm = {
  loading: boolean;
  errorMessage: string;
  transactions: PluggyTransactionDTO[];
  totalElements: number;
  accountId: number | null;
  page: number;
  pageSize: number;
};

@Component({
  selector: 'app-transacoes',
  standalone: true,
  imports: [CommonModule, PaginationComponent,BackButtonComponent,MenuComponent],
  templateUrl: './transacoes.html',
  styleUrl: './transacoes.css',
})
export class Transacoes {
  private readonly route = inject(ActivatedRoute);
  private readonly transacoesService = inject(TransacoesService);

  private readonly pageSize = 20;
  private readonly sort = 'date,desc';

  private readonly pageSubject = new BehaviorSubject<number>(0);

  private readonly accountId$ = this.route.paramMap.pipe(
    map((params) => {
      const rawAccountId = params.get('accountId');
      const accountId = rawAccountId ? Number(rawAccountId) : NaN;

      if (!rawAccountId || Number.isNaN(accountId)) {
        throw new Error('accountId inválido ou não informado na rota.');
      }

      return accountId;
    }),
    distinctUntilChanged()
  );

  readonly vm$ = combineLatest([
    this.accountId$,
    this.pageSubject,
  ]).pipe(
    switchMap(([accountId, page]) =>
      this.loadTransactions(accountId, page)
    ),
    catchError((error) => {
      console.error('[TransacoesComponent] erro na rota', error);

      return of(this.createErrorVm('Conta inválida ou não informada na rota.'));
    }),
    shareReplay({ bufferSize: 1, refCount: true })
  );

  goToPage(page: number): void {
    if (page < 0) {
      return;
    }

    this.pageSubject.next(page);
  }

  private loadTransactions(accountId: number, page: number) {
    return this.transacoesService.findMyTransactionsByAccount(accountId, {
      page,
      size: this.pageSize,
      sort: this.sort,
    }).pipe(
      map((response) => {
        const transactions = response.content ?? [];
        const totalElements = response.totalElements ?? 0;

        return {
          loading: false,
          errorMessage: '',
          transactions,
          totalElements,
          accountId,
          page,
          pageSize: this.pageSize,
        } satisfies TransacoesVm;
      }),
      startWith(this.createLoadingVm(accountId, page)),
      catchError((error) => {
        console.error('[TransacoesComponent] erro ao carregar transações', error);

        return of(
          this.createErrorVm(
            'Não foi possível carregar as transações.',
            accountId,
            page
          )
        );
      })
    );
  }

  private createLoadingVm(accountId: number, page: number): TransacoesVm {
    return {
      loading: true,
      errorMessage: '',
      transactions: [],
      totalElements: 0,
      accountId,
      page,
      pageSize: this.pageSize,
    };
  }

  private createErrorVm(
    errorMessage: string,
    accountId: number | null = null,
    page = 0
  ): TransacoesVm {
    return {
      loading: false,
      errorMessage,
      transactions: [],
      totalElements: 0,
      accountId,
      page,
      pageSize: this.pageSize,
    };
  }

  translateStatus(status: string | null | undefined): string {
  const statusMap: Record<string, string> = {
    POSTED: 'lançada',
    PENDING: 'pendente',
  };

  if (!status) {
    return '-';
  }

  return statusMap[status.toUpperCase()] ?? status.toLowerCase();
}

translateType(type: string | null | undefined): string {
  const typeMap: Record<string, string> = {
    DEBIT: 'débito',
    CREDIT: 'crédito',
  };

  if (!type) {
    return '-';
  }

  return typeMap[type.toUpperCase()] ?? type.toLowerCase();
}
}
