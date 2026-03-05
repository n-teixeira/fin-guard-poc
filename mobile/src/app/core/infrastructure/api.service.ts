import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { map, catchError } from 'rxjs/operators';
import { environment } from '../../../environments/environment';
import { Transaction } from '../domain/models/transaction.model';
import { CashFlow } from '../domain/models/cash-flow.model';

export interface NotificationPayload {
  content: string;
  packageName: string;
}

@Injectable({ providedIn: 'root' })
export class ApiService {
  private readonly baseUrl = environment.apiUrl;
  private readonly userId = 'default-user';

  constructor(private readonly http: HttpClient) {}

  private getHeaders(): HttpHeaders {
    return new HttpHeaders({
      'Content-Type': 'application/json',
      'X-User-Id': this.userId,
    });
  }

  processNotification(content: string, packageName: string): Observable<Transaction> {
    return this.http
      .post<Transaction>(`${this.baseUrl}/api/v1/notifications/process`, { content, packageName }, {
        headers: this.getHeaders(),
      })
      .pipe(
        map((res: any) => ({ ...res, amount: res.amount ?? 0 })),
        catchError((err) => throwError(() => err))
      );
  }

  getCashFlow(): Observable<CashFlow> {
    return this.http
      .get<CashFlow>(`${this.baseUrl}/api/v1/cash-flow`, {
        headers: this.getHeaders(),
      })
      .pipe(
        map((res: any) => ({
          ...res,
          balance: res.balance ?? 0,
          totalCredits: res.totalCredits ?? 0,
          totalDebits: res.totalDebits ?? 0,
        })),
        catchError((err) => throwError(() => err))
      );
  }

  getTransactions(): Observable<Transaction[]> {
    return this.http
      .get<Transaction[]>(`${this.baseUrl}/api/v1/transactions`, {
        headers: this.getHeaders(),
      })
      .pipe(
        map((list: any) => (Array.isArray(list) ? list : [])),
        catchError((err) => throwError(() => err))
      );
  }
}
