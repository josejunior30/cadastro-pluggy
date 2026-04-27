// file: front-end/cadastro/src/app/services/conta.service.ts

import { HttpClient, HttpErrorResponse, HttpParams } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { catchError, Observable, tap, throwError } from 'rxjs';

import { environment } from '../../environments/environment';
import { PluggyAccountDTO, ContaTransactionQueryParams, PageResponse, PluggyTransactionDTO } from '../models/conta';

@Injectable({
  providedIn: 'root',
})
export class ContaService {
  private readonly http = inject(HttpClient);
  private readonly apiUrl = `${environment.apiUrl}/pluggy`;

  findMyAccounts(): Observable<PluggyAccountDTO[]> {
    const url = `${this.apiUrl}/accounts`;

    console.log('[ContaService] GET', url);

    return this.http.get<PluggyAccountDTO[]>(url).pipe(
      tap((response: any[]) => {
        console.log('[ContaService] Resposta bruta /accounts:', response);
        console.log('[ContaService] Quantidade de contas recebidas:', response?.length ?? 0);

        if (Array.isArray(response)) {
          console.table(
            response.map((account) => ({
              id: account.id,
              name: account.name,
              type: account.type,
              subtype: account.subtype,
              balance: account.balance,
              currencyCode: account.currencyCode,
            }))
          );
        }
      }),
      catchError((error: HttpErrorResponse) => {
        console.error('[ContaService] Erro ao buscar /accounts', {
          status: error.status,
          statusText: error.statusText,
          message: error.message,
          url: error.url,
          error: error.error,
        });

        return throwError(() => error);
      })
    );
  }

 
}
