import { Search } from "lucide-react";
import { useState } from "react";
import { DataTable } from "../components/DataTable";
import { Drawer } from "../components/Drawer";
import { EmptyState } from "../components/EmptyState";
import { StatusBadge } from "../components/StatusBadge";
import { TableSkeleton } from "../components/Skeleton";
import { getTransaction, listTransactions } from "../services/transactions";
import type { TransactionDetail, TransactionSummary } from "../types";
import { formatDateTime, formatMoney } from "../utils/format";
import { useAsync } from "../hooks/useAsync";

export function TransactionsPage() {
  const [status, setStatus] = useState("");
  const [search, setSearch] = useState("");
  const [selected, setSelected] = useState<TransactionDetail | null>(null);
  const { data, loading, error, refresh } = useAsync(
    () => listTransactions({ status: status || undefined, search: search || undefined }),
    [status, search]
  );

  async function openTransaction(row: TransactionSummary) {
    try {
      setSelected(await getTransaction(row.id));
    } catch {
      setSelected({ ...row, entries: [] });
    }
  }

  return (
    <div className="space-y-5">
      <div className="flex flex-col gap-3 rounded-lg border border-slate-800 bg-surface-900 p-4 md:flex-row">
        <div className="relative flex-1">
          <Search className="absolute left-3 top-2.5 h-4 w-4 text-slate-500" />
          <input value={search} onChange={(event) => setSearch(event.target.value)} className="input pl-9" placeholder="Search reference" />
        </div>
        <select value={status} onChange={(event) => setStatus(event.target.value)} className="input md:w-52">
          <option value="">All statuses</option>
          <option value="SUCCESS">Success</option>
          <option value="FAILED">Failed</option>
          <option value="PENDING">Pending</option>
        </select>
        <button onClick={() => void refresh()} className="rounded-md border border-slate-700 px-4 py-2 text-sm text-slate-200 hover:bg-surface-800">
          Refresh
        </button>
      </div>

      {loading && <TableSkeleton />}
      {error && <EmptyState title="Transactions endpoint unavailable" detail="/operations/transactions is needed for transaction search." />}
      {!loading && !error && (
        <DataTable
          rows={data ?? []}
          columns={[
            { key: "reference", header: "External Reference", render: (row) => row.externalTransactionReference },
            { key: "status", header: "Status", render: (row) => <StatusBadge status={row.status} /> },
            { key: "amount", header: "Amount", render: (row) => formatMoney(row.amount, row.currency) },
            { key: "created", header: "Created Time", render: (row) => formatDateTime(row.createdAt) }
          ]}
          onRowClick={openTransaction}
          emptyTitle="No transactions found"
        />
      )}

      <Drawer open={!!selected} title="Transaction Detail" onClose={() => setSelected(null)}>
        {selected && (
          <div className="space-y-6">
            <div className="rounded-lg border border-slate-800 bg-surface-850 p-4">
              <p className="text-sm text-slate-500">External Reference</p>
              <p className="mt-1 font-medium text-slate-100">{selected.externalTransactionReference}</p>
              <div className="mt-4 flex items-center gap-3">
                <StatusBadge status={selected.status} />
                <span className="text-sm text-slate-500">{formatDateTime(selected.createdAt)}</span>
              </div>
            </div>
            <DataTable
              rows={selected.entries}
              columns={[
                { key: "account", header: "Account", render: (row) => row.accountName ?? row.accountId },
                { key: "debit", header: "Debit", render: (row) => (row.type === "DEBIT" ? formatMoney(row.amount, selected.currency) : "—") },
                { key: "credit", header: "Credit", render: (row) => (row.type === "CREDIT" ? formatMoney(row.amount, selected.currency) : "—") }
              ]}
              emptyTitle="Entries unavailable"
            />
            {selected.saga && (
              <div className="rounded-lg border border-slate-800 bg-surface-850 p-4">
                <p className="text-sm text-slate-500">Saga</p>
                <div className="mt-2 flex items-center gap-3">
                  <span className="text-sm text-slate-200">#{selected.saga.id}</span>
                  <StatusBadge status={selected.saga.status} />
                </div>
              </div>
            )}
          </div>
        )}
      </Drawer>
    </div>
  );
}
