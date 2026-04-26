import { HttpClient } from '@angular/common/http';

import { Observable } from 'rxjs';

import { environment } from '../../environments/environment';
import { inject, Injectable } from '@angular/core';

export interface PluggyConnectTokenResponse {
  accessToken: string;
}

export interface PluggySyncRequest {
  itemId: string;
}

@Injectable({
  providedIn: 'root',
})
export class PluggyService {
  private readonly http = inject(HttpClient);
  private readonly apiUrl = `${environment.apiUrl}/pluggy`;

  createConnectToken(): Observable<PluggyConnectTokenResponse> {
    return this.http.post<PluggyConnectTokenResponse>(`${this.apiUrl}/connect-token`, {});
  }

  syncItem(itemId: string): Observable<void> {
    const body: PluggySyncRequest = {
      itemId,
    };

    return this.http.post<void>(`${this.apiUrl}/items/sync`, body);
  }
}
