import { RefreshCw } from "lucide-react";
import { useState } from "react";
import { DataTable } from "../components/DataTable";
import { Drawer } from "../components/Drawer";
import { EmptyState } from "../components/EmptyState";
import { StatusBadge } from "../components/StatusBadge";
import { TableSkeleton } from "../components/Skeleton";
import { Timeline } from "../components/Timeline";
import { getSaga, listSagas } from "../services/sagas";
import type { SagaDetail, SagaSummary } from "../types";
import { formatDateTime } from "../utils/format";
import { useAsync } from "../hooks/useAsync";

export function SagaMonitorPage() {
  const [status, setStatus] = useState("");
  const [selected, setSelected] = useState<SagaDetail | null>(null);
  const { data, loading, error, refresh } = useAsync(
    () => listSagas({ status: status || undefined }),
    [status]
  );

  async function openSaga(row: SagaSummary) {
    try {
      setSelected(await getSaga(row.id));
    } catch {
      setSelected({ ...row, steps: [] });
    }
  }

  return (
    <div className="space-y-5">
      <div className="flex flex-col gap-3 rounded-lg border border-slate-800 bg-surface-900 p-4 sm:flex-row">
        <select value={status} onChange={(event) => setStatus(event.target.value)} className="input sm:w-64">
          <option value="">All saga statuses</option>
          <option value="IN_PROGRESS">In progress</option>
          <option value="COMPENSATING">Compensating</option>
          <option value="COMPENSATED">Compensated</option>
          <option value="COMPENSATION_FAILED">Compensation failed</option>
        </select>
        <button onClick={() => void refresh()} className="inline-flex items-center gap-2 rounded-md border border-slate-700 px-4 py-2 text-sm text-slate-200 hover:bg-surface-800">
          <RefreshCw className="h-4 w-4" />
          Refresh
        </button>
      </div>

      {loading && <TableSkeleton />}
      {error && <EmptyState title="Saga endpoint unavailable" detail="/operations/sagas is needed for Saga monitoring." />}
      {!loading && !error && (
        <DataTable
          rows={data ?? []}
          columns={[
            { key: "id", header: "Saga ID", render: (row) => `#${row.id}` },
            { key: "status", header: "Status", render: (row) => <StatusBadge status={row.status} /> },
            { key: "step", header: "Current Step", render: (row) => row.currentStep ?? "—" },
            { key: "transaction", header: "Transaction", render: (row) => row.transactionReference ?? "—" },
            { key: "created", header: "Created Time", render: (row) => formatDateTime(row.createdAt) }
          ]}
          onRowClick={openSaga}
          emptyTitle="No sagas found"
        />
      )}

      <Drawer open={!!selected} title="Saga Timeline" onClose={() => setSelected(null)}>
        {selected && (
          <Timeline
            items={
              selected.steps.length > 0
                ? selected.steps.map((step) => ({
                    title: step.stepType.replaceAll("_", " "),
                    status: step.status,
                    detail: step.failureReason
                  }))
                : [{ title: "Saga", status: selected.status, detail: selected.currentStep }]
            }
          />
        )}
      </Drawer>
    </div>
  );
}
