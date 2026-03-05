import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import {
  IonHeader,
  IonToolbar,
  IonTitle,
  IonContent,
  IonBackButton,
  IonButtons,
  IonList,
  IonItem,
  IonLabel,
} from '@ionic/angular/standalone';
import { ApiService } from '../../core/infrastructure/api.service';
import { Transaction } from '../../core/domain/models/transaction.model';

@Component({
  selector: 'app-transactions',
  standalone: true,
  imports: [
    CommonModule,
    RouterLink,
    IonHeader,
    IonToolbar,
    IonTitle,
    IonContent,
    IonBackButton,
    IonButtons,
    IonList,
    IonItem,
    IonLabel,
  ],
  template: `
    <ion-header>
      <ion-toolbar>
        <ion-buttons slot="start">
          <ion-back-button defaultHref="/home"></ion-back-button>
        </ion-buttons>
        <ion-title>Transações</ion-title>
      </ion-toolbar>
    </ion-header>

    <ion-content>
      @if (loading) {
        <p class="ion-padding">Carregando...</p>
      } @else if (transactions.length === 0) {
        <p class="ion-padding">Nenhuma transação registrada.</p>
      } @else {
        <ion-list>
          @for (t of transactions; track t.id) {
            <ion-item>
              <ion-label>
                <h2>{{ t.origin }}</h2>
                <p>{{ t.type }} · {{ formatDate(t.occurredAt) }}</p>
              </ion-label>
              <span [class.credit]="isCredit(t)" [class.debit]="!isCredit(t)" slot="end">
                {{ formatCurrency(t.amount) }}
              </span>
            </ion-item>
          }
        </ion-list>
      }
    </ion-content>
  `,
  styles: [`
    .credit { color: var(--ion-color-success); font-weight: bold; }
    .debit { color: var(--ion-color-danger); font-weight: bold; }
  `],
})
export class TransactionsPage implements OnInit {
  transactions: Transaction[] = [];
  loading = true;

  constructor(private readonly api: ApiService) {}

  ngOnInit() {
    this.api.getTransactions().subscribe({
      next: (list) => {
        this.transactions = list;
        this.loading = false;
      },
      error: () => (this.loading = false),
    });
  }

  isCredit(t: Transaction): boolean {
    return ['CREDIT', 'PIX_IN', 'TRANSFER_IN'].includes(t.type);
  }

  formatCurrency(value: number): string {
    const v = value ?? 0;
    const prefix = v >= 0 ? '' : '-';
    return prefix + new Intl.NumberFormat('pt-BR', { style: 'currency', currency: 'BRL' }).format(Math.abs(v));
  }

  formatDate(iso: string): string {
    if (!iso) return '';
    return new Date(iso).toLocaleString('pt-BR');
  }
}
