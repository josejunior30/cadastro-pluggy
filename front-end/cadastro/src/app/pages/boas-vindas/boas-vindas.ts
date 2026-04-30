import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { RouterModule } from '@angular/router';

type Atalho = {
  title: string;
  description: string;
  route: string;
  buttonLabel: string;
};

@Component({
  selector: 'app-boas-vindas',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './boas-vindas.html',
  styleUrl: './boas-vindas.css',
})
export class BoasVindas {
  readonly atalhos: Atalho[] = [
    {
      title: 'Conectar conta bancária',
      description:
        'Conecte sua conta via Pluggy para visualizar saldo, contas e transações no sistema. É necessário ter uma conta na Pluggy para realizar a integração.',
      route: '/pluggy',
      buttonLabel: 'Conectar agora',
    },
    {
      title: 'Visualizar contas',
      description:
        'Acesse as contas já sincronizadas e acompanhe saldos, tipos de conta e informações atualizadas.',
      route: '/conta',
      buttonLabel: 'Ver contas',
    },
  ];
}
