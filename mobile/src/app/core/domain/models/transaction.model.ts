export type TransactionType =
  | 'CREDIT'
  | 'DEBIT'
  | 'PIX_IN'
  | 'PIX_OUT'
  | 'TRANSFER_IN'
  | 'TRANSFER_OUT'
  | 'PAYMENT'
  | 'UNKNOWN';

export interface Transaction {
  id: string;
  userId: string;
  amount: number;
  type: TransactionType;
  origin: string;
  occurredAt: string;
  bankIdentifier?: string;
}
