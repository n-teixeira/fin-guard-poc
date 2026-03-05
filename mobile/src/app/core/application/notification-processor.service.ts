import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { tap } from 'rxjs/operators';
import { ApiService } from '../infrastructure/api.service';
import { Transaction } from '../domain/models/transaction.model';
import { CashFlowService } from './cash-flow.service';

/**
 * Application Service - processa notificações e atualiza fluxo de caixa em tempo real.
 */
@Injectable({ providedIn: 'root' })
export class NotificationProcessorService {
  constructor(
    private readonly api: ApiService,
    private readonly cashFlow: CashFlowService
  ) {}

  process(content: string, packageName: string): Observable<Transaction> {
    return this.api.processNotification(content, packageName).pipe(
      tap(() => this.cashFlow.loadCashFlow().subscribe())
    );
  }
}
