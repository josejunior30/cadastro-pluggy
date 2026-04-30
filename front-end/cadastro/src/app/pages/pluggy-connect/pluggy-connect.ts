// file: src/app/pages/pluggy-connect/pluggy-connect.ts
import { CommonModule } from '@angular/common';
import { Component, inject, signal } from '@angular/core';
import { Router } from '@angular/router';
import { PluggyConnect } from 'pluggy-connect-sdk';

import { MenuComponent } from '../../components/menu/menu';
import { PluggyService } from '../../services/pluggy.service';

@Component({
  selector: 'app-pluggy-connect',
  standalone: true,
  templateUrl: './pluggy-connect.html',
  imports: [CommonModule, MenuComponent],
})
export class PluggyConnectComponent {
  private readonly pluggyService = inject(PluggyService);
  private readonly router = inject(Router);

  carregando = signal(false);
  erro = signal<string | null>(null);
  sucesso = signal<string | null>(null);
  itemId = signal<string | null>(null);

  conectarConta(): void {
    this.carregando.set(true);
    this.erro.set(null);
    this.sucesso.set(null);
    this.itemId.set(null);

    this.pluggyService.createConnectToken().subscribe({
      next: (response) => {
        console.log('Connect token recebido:', response);

        if (!response.accessToken) {
          this.carregando.set(false);
          this.erro.set('O backend não retornou o accessToken da Pluggy.');
          return;
        }

        this.abrirWidgetPluggy(response.accessToken);
      },
      error: (error) => {
        console.error('Erro ao gerar connect token:', error);

        this.carregando.set(false);
        this.erro.set(
          error?.error?.message ||
            error?.error?.error ||
            'Não foi possível gerar o token de conexão da Pluggy.'
        );
      },
    });
  }

  private abrirWidgetPluggy(connectToken: string): void {
    const pluggyConnect = new PluggyConnect({
      connectToken,
      includeSandbox: true,
      language: 'pt',

      onSuccess: (itemData: any) => {
        console.log('Pluggy onSuccess:', itemData);
        const itemId = itemData?.item?.id ?? itemData?.id;

        if (!itemId) {
          this.carregando.set(false);
          this.erro.set('A Pluggy não retornou um itemId válido.');
          return;
        }

        this.itemId.set(itemId);
        this.sincronizarItem(itemId);
      },

      onError: (error: any) => {
        console.error('Pluggy onError:', error);
        this.carregando.set(false);
        this.erro.set('Ocorreu um erro ao conectar a conta na Pluggy.');
      },

      onClose: () => {
        this.carregando.set(false);
      },
    });

    pluggyConnect.init();
  }

  private sincronizarItem(itemId: string): void {
    this.pluggyService.syncItem(itemId).subscribe({
      next: () => {
        this.carregando.set(false);
        this.sucesso.set('Conta conectada e dados importados com sucesso.');
        void this.router.navigate(['/conta']);
      },
      error: (error) => {
        console.error('Erro ao sincronizar item:', error);

        this.carregando.set(false);
        this.erro.set(
          error?.error?.message ||
            error?.error?.error ||
            'Conta conectada, mas ocorreu erro ao importar os dados no backend.'
        );
      },
    });
  }
}

export { PluggyConnect };
