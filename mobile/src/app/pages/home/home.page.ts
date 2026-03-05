import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import {
  IonHeader,
  IonToolbar,
  IonTitle,
  IonContent,
  IonCard,
  IonCardHeader,
  IonCardTitle,
  IonCardContent,
  IonButton,
  IonIcon,
} from '@ionic/angular/standalone';
import { addIcons } from 'ionicons';
import { walletOutline, listOutline } from 'ionicons/icons';
import { Subscription } from 'rxjs';
import { CashFlowService } from '../../core/application/cash-flow.service';
import { CashFlow } from '../../core/domain/models/cash-flow.model';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [
    CommonModule,
    RouterLink,
    IonHeader,
    IonToolbar,
    IonTitle,
    IonContent,
    IonCard,
    IonCardHeader,
    IonCardTitle,
    IonCardContent,
    IonButton,
    IonIcon,
  ],
  template: `
    <ion-header>
      <ion-toolbar color="primary">
        <ion-title>FinGuard</ion-title>
      </ion-toolbar>
    </ion-header>

    <ion-content class="ion-padding">
      <ion-card>
        <ion-card-header>
          <ion-card-title>
            <ion-icon name="wallet-outline"></ion-icon>
            Fluxo de Caixa
          </ion-card-title>
        </ion-card-header>
        <ion-card-content>
          <p class="balance" [class.positive]="(cashFlow?.balance ?? 0) >= 0">
            {{ formatCurrency(cashFlow?.balance ?? 0) }}
          </p>
          <p class="sub">Receitas: {{ formatCurrency(cashFlow?.totalCredits ?? 0) }}</p>
          <p class="sub">Despesas: {{ formatCurrency(cashFlow?.totalDebits ?? 0) }}</p>
        </ion-card-content>
      </ion-card>

      <ion-button expand="block" routerLink="/transactions">
        <ion-icon name="list-outline"></ion-icon>
        Ver Transações
      </ion-button>

      <p class="hint ion-padding-top">
        As notificações bancárias são interceptadas automaticamente via Android Notification Listener.
        O fluxo de caixa é atualizado em tempo real.
      </p>
    </ion-content>
  `,
  styles: [`
    .balance {
      font-size: 2rem;
      font-weight: bold;
    }
    .balance.positive { color: var(--ion-color-success); }
    .balance:not(.positive) { color: var(--ion-color-danger); }
    .sub { color: var(--ion-color-medium); font-size: 0.9rem; }
    .hint { font-size: 0.85rem; color: var(--ion-color-medium); }
  `],
})
export class HomePage implements OnInit, OnDestroy {
  cashFlow: CashFlow | null = null;
  private sub?: Subscription;

  constructor(private readonly cashFlowService: CashFlowService) {
    addIcons({ walletOutline, listOutline });
  }

  ngOnInit() {
    this.sub = this.cashFlowService.cashFlow$.subscribe((cf) => (this.cashFlow = cf));
    this.cashFlowService.loadCashFlow().subscribe({
      error: (err) => console.warn('Erro ao carregar fluxo de caixa:', err),
    });
  }

  ngOnDestroy() {
    this.sub?.unsubscribe();
  }

  formatCurrency(value: number): string {
    return new Intl.NumberFormat('pt-BR', {
      style: 'currency',
      currency: 'BRL',
    }).format(value);
  }
}
