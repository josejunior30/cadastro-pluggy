import { Routes } from '@angular/router';
import { authGuard } from './guard/guard';

export const routes: Routes = [
  {
    path: '',
    loadComponent: () => import('./pages/login/login').then((m) => m.Login),
  },

    {
    path: 'pluggy',
     canActivate: [authGuard],
    loadComponent: () => import('./pages/pluggy-connect/pluggy-connect').then((m) => m.PluggyConnectComponent),
  },
   {
    path: 'conta',
     canActivate: [authGuard],
    loadComponent: () => import('./pages/conta/conta').then((m) => m.Conta),
  },

    {
    path: 'transacoes/:accountId',
    canActivate: [authGuard],
    loadComponent: () =>
      import('./pages/transacoes/transacoes').then((m) => m.Transacoes),
  },
];
