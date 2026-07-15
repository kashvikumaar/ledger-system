import {
  Activity,
  AlertTriangle,
  CheckCircle2,
  Landmark,
  RefreshCw,
  Route
} from "lucide-react";
import { DataTable } from "../components/DataTable";
import { EmptyState } from "../components/EmptyState";
import { StatCard } from "../components/StatCard";
import { StatusBadge } from "../components/StatusBadge";
import { TableSkeleton } from "../components/Skeleton";
import type { ReactNode } from "react";
import { getDashboardSummary } from "../services/dashboard";
import { formatDateTime, formatMoney } from "../utils/format";
import { useAsync } from "../hooks/useAsync";

export function DashboardPage() {
  const { data, loading, error } = useAsync(getDashboardSummary, []);

  if (loading) {
    return <TableSkeleton />;
  }

  if (error || !data) {
    return (
      <EmptyState
        title="Dashboard endpoint unavailable"
        detail="/operations/dashboard is needed for aggregate operations metrics."
      />
    );
  }

  return (
    <div className="space-y-6">
      <div className="grid gap-4 sm:grid-cols-2 xl:grid-cols-6">
        <StatCard label="Total Accounts" value={data.totalAccounts} icon={Landmark} />
        <StatCard label="Total Transactions" value={data.totalTransactions} icon={Activity} />
        <StatCard label="Successful" value={data.successfulTransactions} icon={CheckCircle2} tone="success" />
        <StatCard label="Failed" value={data.failedTransactions} icon={AlertTriangle} tone="danger" />
        <StatCard label="Active Sagas" value={data.activeSagas} icon={Route} tone="warning" />
        <StatCard label="Pending Recoveries" value={data.pendingRecoveries} icon={RefreshCw} />
      </div>

      <section className="grid gap-6 xl:grid-cols-2">
        <Panel title="Recent Transactions">
          <DataTable
            rows={data.recentTransactions}
            columns={[
              { key: "reference", header: "Reference", render: (row) => row.externalTransactionReference },
              { key: "status", header: "Status", render: (row) => <StatusBadge status={row.status} /> },
              { key: "amount", header: "Amount", render: (row) => formatMoney(row.amount, row.currency) },
              { key: "created", header: "Created", render: (row) => formatDateTime(row.createdAt) }
            ]}
            emptyTitle="No recent transactions"
          />
        </Panel>

        <Panel title="Recent Saga Activity">
          <DataTable
            rows={data.recentSagas}
            columns={[
              { key: "id", header: "Saga", render: (row) => `#${row.id}` },
              { key: "status", header: "Status", render: (row) => <StatusBadge status={row.status} /> },
              { key: "step", header: "Step", render: (row) => row.currentStep ?? "—" },
              { key: "created", header: "Created", render: (row) => formatDateTime(row.createdAt) }
            ]}
            emptyTitle="No recent saga activity"
          />
        </Panel>
      </section>

      <Panel title="Recent Reconciliation Reports">
        <DataTable
          rows={data.recentReconciliationReports}
          columns={[
            { key: "id", header: "Report", render: (row) => `#${row.id}` },
            { key: "matched", header: "Matched", render: (row) => row.matched },
            { key: "unmatched", header: "Unmatched", render: (row) => row.unmatched },
            { key: "time", header: "Timestamp", render: (row) => formatDateTime(row.reconciliationTimestamp) }
          ]}
          emptyTitle="No reconciliation reports"
        />
      </Panel>
    </div>
  );
}

function Panel({ title, children }: { title: string; children: ReactNode }) {
  return (
    <section className="space-y-3">
      <h2 className="text-sm font-semibold text-slate-200">{title}</h2>
      {children}
    </section>
  );
}
