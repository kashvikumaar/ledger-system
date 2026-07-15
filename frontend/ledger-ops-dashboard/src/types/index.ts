export type Status =
  | "SUCCESS"
  | "FAILED"
  | "PENDING"
  | "IN_PROGRESS"
  | "STARTED"
  | "COMPLETED"
  | "COMPENSATING"
  | "COMPENSATED"
  | "COMPENSATION_FAILED"
  | "MATCHED"
  | "MISSING_IN_LEDGER"
  | "MISSING_EXTERNALLY"
  | "AMOUNT_MISMATCH"
  | "DUPLICATE_TRANSACTION";

export interface EntryRequest {
  accountId: number;
  type: "DEBIT" | "CREDIT";
  amount: number;
}

export interface TransactionRequest {
  externalTransactionReference: string;
  entries: EntryRequest[];
}

export interface TransactionSummary {
  id: number;
  externalTransactionReference: string;
  status: Status;
  amount: number;
  currency?: string;
  createdAt: string;
}

export interface LedgerEntry {
  id: number;
  accountId: number;
  accountName?: string;
  type: "DEBIT" | "CREDIT";
  amount: number;
  createdAt: string;
}

export interface TransactionDetail extends TransactionSummary {
  entries: LedgerEntry[];
  saga?: SagaSummary;
}

export interface AccountSummary {
  id: number;
  name: string;
  type: string;
  currency: string;
  balance?: number;
}

export interface SagaSummary {
  id: number;
  status: Status;
  currentStep?: string;
  transactionReference?: string;
  createdAt: string;
  updatedAt?: string;
}

export interface SagaStep {
  id: number;
  stepType: string;
  status: Status;
  failureReason?: string;
  createdAt: string;
  updatedAt?: string;
}

export interface SagaDetail extends SagaSummary {
  steps: SagaStep[];
}

export interface ReconciliationRecord {
  id: number;
  transactionReference: string;
  externalAmount?: number;
  internalAmount?: number;
  currency?: string;
  externalTimestamp?: string;
  status: Status;
  details?: string;
}

export interface ReconciliationReport {
  id: number;
  totalRecords: number;
  matched: number;
  unmatched: number;
  duplicates: number;
  amountMismatches: number;
  reconciliationTimestamp: string;
  records: ReconciliationRecord[];
}

export interface DashboardSummary {
  totalAccounts: number;
  totalTransactions: number;
  successfulTransactions: number;
  failedTransactions: number;
  activeSagas: number;
  pendingRecoveries: number;
  recentTransactions: TransactionSummary[];
  recentSagas: SagaSummary[];
  recentReconciliationReports: ReconciliationReport[];
}

export interface HealthStatus {
  backendStatus: string;
  databaseStatus: string;
  redisStatus: string;
  currentTime: string;
  applicationVersion: string;
  flywayMigrationVersion: string;
}
