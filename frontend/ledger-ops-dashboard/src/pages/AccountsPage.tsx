import { Search } from "lucide-react";
import { useState } from "react";
import { DataTable } from "../components/DataTable";
import { EmptyState } from "../components/EmptyState";
import { TableSkeleton } from "../components/Skeleton";
import { getAccountBalance, getAccountEntries, searchAccounts } from "../services/accounts";
import type { AccountSummary, LedgerEntry } from "../types";
import { formatDateTime, formatMoney } from "../utils/format";

export function AccountsPage() {
  const [search, setSearch] = useState("");
  const [accounts, setAccounts] = useState<AccountSummary[]>([]);
  const [entries, setEntries] = useState<LedgerEntry[]>([]);
  const [selected, setSelected] = useState<AccountSummary | null>(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  async function runSearch() {
    setLoading(true);
    setError(null);
    try {
      const results = await searchAccounts(search);
      setAccounts(results);
    } catch {
      setError("/operations/accounts is needed for account search.");
    } finally {
      setLoading(false);
    }
  }

  async function selectAccount(account: AccountSummary) {
    const balance = await getAccountBalance(account.id);
    setSelected({ ...account, balance });
    try {
      setEntries(await getAccountEntries(account.id));
    } catch {
      setEntries([]);
    }
  }

  return (
    <div className="space-y-6">
      <div className="flex flex-col gap-3 rounded-lg border border-slate-800 bg-surface-900 p-4 sm:flex-row">
        <div className="relative flex-1">
          <Search className="absolute left-3 top-2.5 h-4 w-4 text-slate-500" />
          <input
            value={search}
            onChange={(event) => setSearch(event.target.value)}
            className="input pl-9"
            placeholder="Search account"
          />
        </div>
        <button onClick={runSearch} className="rounded-md bg-slate-100 px-4 py-2 text-sm font-medium text-slate-950">
          Search
        </button>
      </div>

      {loading && <TableSkeleton />}
      {error && <EmptyState title="Account search unavailable" detail={error} />}

      {!loading && !error && (
        <DataTable
          rows={accounts}
          columns={[
            { key: "id", header: "ID", render: (row) => row.id },
            { key: "name", header: "Name", render: (row) => row.name },
            { key: "type", header: "Type", render: (row) => row.type },
            { key: "currency", header: "Currency", render: (row) => row.currency }
          ]}
          onRowClick={selectAccount}
          emptyTitle="Search for an account"
        />
      )}

      {selected && (
        <section className="grid gap-6 xl:grid-cols-[22rem_minmax(0,1fr)]">
          <div className="rounded-lg border border-slate-800 bg-surface-900 p-5">
            <p className="text-sm text-slate-500">Current Balance</p>
            <p className="mt-2 text-3xl font-semibold text-slate-50">
              {formatMoney(selected.balance, selected.currency)}
            </p>
            <p className="mt-2 text-sm text-slate-400">{selected.name}</p>
          </div>
          <DataTable
            rows={entries}
            columns={[
              { key: "type", header: "Type", render: (row) => row.type },
              { key: "amount", header: "Amount", render: (row) => formatMoney(row.amount, selected.currency) },
              { key: "created", header: "Created", render: (row) => formatDateTime(row.createdAt) }
            ]}
            emptyTitle="No recent entries"
          />
        </section>
      )}
    </div>
  );
}
