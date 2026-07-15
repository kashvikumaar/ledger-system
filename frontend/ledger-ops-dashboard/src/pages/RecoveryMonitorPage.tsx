import { RefreshCw } from "lucide-react";
import { DataTable } from "../components/DataTable";
import { EmptyState } from "../components/EmptyState";
import { StatusBadge } from "../components/StatusBadge";
import { TableSkeleton } from "../components/Skeleton";
import { listSagas } from "../services/sagas";
import { formatDateTime } from "../utils/format";
import { useAsync } from "../hooks/useAsync";

export function RecoveryMonitorPage() {
  const { data, loading, error, refresh } = useAsync(() => listSagas({ recoverable: true }), []);

  return (
    <div className="space-y-5">
      <div className="flex justify-end">
        <button onClick={() => void refresh()} className="inline-flex items-center gap-2 rounded-md border border-slate-700 px-4 py-2 text-sm text-slate-200 hover:bg-surface-800">
          <RefreshCw className="h-4 w-4" />
          Refresh
        </button>
      </div>
      {loading && <TableSkeleton />}
      {error && <EmptyState title="Recovery endpoint unavailable" detail="/operations/sagas?recoverable=true is needed for recovery monitoring." />}
      {!loading && !error && (
        <DataTable
          rows={data ?? []}
          columns={[
            { key: "id", header: "Saga", render: (row) => `#${row.id}` },
            { key: "status", header: "Recovery Status", render: (row) => <StatusBadge status={row.status} /> },
            { key: "updated", header: "Last Updated", render: (row) => formatDateTime(row.updatedAt) },
            { key: "compensation", header: "Compensation State", render: (row) => row.status.includes("COMPENS") ? <StatusBadge status={row.status} /> : "—" }
          ]}
          emptyTitle="No recoverable sagas"
        />
      )}
    </div>
  );
}
