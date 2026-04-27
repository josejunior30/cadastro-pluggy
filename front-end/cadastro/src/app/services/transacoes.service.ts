import { HttpClient, HttpErrorResponse, HttpParams } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { environment } from '../../environments/environment';
import { Observable, tap, catchError, throwError } from 'rxjs';
import { ContaTransactionQueryParams, PageResponse, PluggyTransactionDTO } from '../models/conta';

@Injectable({
  providedIn: 'root',
})
export class TransacoesService {

  private readonly http = inject(HttpClient);
  private readonly apiUrl = `${environment.apiUrl}/pluggy`;

   findMyTransactionsByAccount(
    accountId: number,
    queryParams: ContaTransactionQueryParams = {}
  ): Observable<PageResponse<PluggyTransactionDTO>> {
    let params = new HttpParams();

    if (queryParams.page !== undefined) {
      params = params.set('page', queryParams.page);
    }

    if (queryParams.size !== undefined) {
      params = params.set('size', queryParams.size);
    }

    if (queryParams.sort) {
      const sortValues = Array.isArray(queryParams.sort)
        ? queryParams.sort
        : [queryParams.sort];

      for (const sortValue of sortValues) {
        params = params.append('sort', sortValue);
      }
    }

    const url = `${this.apiUrl}/accounts/${accountId}/transactions`;

    console.log('[ContaService] GET', url, 'params:', params.toString());

    return this.http.get<PageResponse<PluggyTransactionDTO>>(url, { params }).pipe(
      tap((response) => {
        console.log('[ContaService] Resposta bruta /transactions:', response);
      }),
      catchError((error: HttpErrorResponse) => {
        console.error('[ContaService] Erro ao buscar /transactions', {
          accountId,
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
