import { CommonModule } from "@angular/common";
import { Component, inject } from "@angular/core";
import { ActivatedRoute } from "@angular/router";
import { map, switchMap, startWith, catchError, of, shareReplay } from "rxjs";
import { PluggyTransactionDTO } from "../../models/conta";
import { TransacoesService } from "../../services/transacoes.service";

type TransacoesVm = {
  loading: boolean;
  errorMessage: string;
  transactions: PluggyTransactionDTO[];
  totalElements: number;
  accountId: number | null;
};

@Component({
  selector: 'app-transacoes',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './transacoes.html',
  styleUrl: './transacoes.css',
})
export class Transacoes {
  private readonly route = inject(ActivatedRoute);
  private readonly transacoesService = inject(TransacoesService);

  readonly vm$ = this.route.paramMap.pipe(
    map((params) => {
      const rawAccountId = params.get('accountId');
      const accountId = rawAccountId ? Number(rawAccountId) : NaN;

      if (!rawAccountId || Number.isNaN(accountId)) {
        throw new Error('accountId inválido ou não informado na rota.');
      }

      return accountId;
    }),
    switchMap((accountId) =>
      this.transacoesService.findMyTransactionsByAccount(accountId, {
        page: 0,
        size: 20,
        sort: 'date,desc',
      }).pipe(
        map((response) => ({
          loading: false,
          errorMessage: '',
          transactions: response.content ?? [],
          totalElements: response.totalElements ?? 0,
          accountId,
        }) satisfies TransacoesVm),
        startWith({
          loading: true,
          errorMessage: '',
          transactions: [],
          totalElements: 0,
          accountId,
        } satisfies TransacoesVm)
      )
    ),
    catchError((error) => {
      console.error('[TransacoesComponent] erro ao carregar transações', error);

      return of({
        loading: false,
        errorMessage: 'Não foi possível carregar as transações.',
        transactions: [],
        totalElements: 0,
        accountId: null,
      } satisfies TransacoesVm);
    }),
    shareReplay({ bufferSize: 1, refCount: true })
  );
}
