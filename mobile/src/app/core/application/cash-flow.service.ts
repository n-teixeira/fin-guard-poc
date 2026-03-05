import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable, interval, switchMap, tap, startWith } from 'rxjs';
import { ApiService } from '../infrastructure/api.service';
import { CashFlow } from '../domain/models/cash-flow.model';

@Injectable({ providedIn: 'root' })
export class CashFlowService {
  private readonly _cashFlow$ = new BehaviorSubject<CashFlow | null>(null);

  readonly cashFlow$: Observable<CashFlow | null> = this._cashFlow$ as Observable<CashFlow | null>;

  constructor(private readonly api: ApiService) {}

  loadCashFlow(): Observable<CashFlow> {
    return this.api.getCashFlow().pipe(
      tap((cf) => this._cashFlow$.next(cf))
    );
  }

  startPolling(intervalMs = 30_000): Observable<CashFlow | null> {
    return interval(intervalMs).pipe(
      startWith(0),
      switchMap(() => this.api.getCashFlow()),
      tap((cf) => this._cashFlow$.next(cf))
    );
  }
}
