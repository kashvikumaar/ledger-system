import { Download, UploadCloud } from "lucide-react";
import { DragEvent, useState } from "react";
import { DataTable } from "../components/DataTable";
import { EmptyState } from "../components/EmptyState";
import { StatCard } from "../components/StatCard";
import { StatusBadge } from "../components/StatusBadge";
import { useToast } from "../components/Toast";
import { uploadReconciliation } from "../services/reconciliation";
import type { ReconciliationReport } from "../types";
import { downloadText, formatDateTime, formatMoney } from "../utils/format";

export function ReconciliationPage() {
  const toast = useToast();
  const [report, setReport] = useState<ReconciliationReport | null>(null);
  const [loading, setLoading] = useState(false);

  async function upload(file: File) {
    setLoading(true);
    try {
      const result = await uploadReconciliation(file);
      setReport(result);
      toast.success("Reconciliation completed");
    } catch {
      toast.error("Reconciliation failed");
    } finally {
      setLoading(false);
    }
  }

  function handleDrop(event: DragEvent<HTMLDivElement>) {
    event.preventDefault();
    const file = event.dataTransfer.files.item(0);
    if (file) {
      void upload(file);
    }
  }

  function exportReport() {
    if (!report) {
      return;
    }

    const rows = [
      "transaction_reference,external_amount,internal_amount,currency,status,details",
      ...report.records.map((record) =>
        [
          record.transactionReference,
          record.externalAmount ?? "",
          record.internalAmount ?? "",
          record.currency ?? "",
          record.status,
          record.details ?? ""
        ].join(",")
      )
    ];

    downloadText(`reconciliation-${report.id}.csv`, rows.join("\n"));
  }

  return (
    <div className="space-y-6">
      <div
        onDragOver={(event) => event.preventDefault()}
        onDrop={handleDrop}
        className="rounded-lg border border-dashed border-slate-700 bg-surface-900 p-8 text-center"
      >
        <UploadCloud className="mx-auto h-10 w-10 text-slate-500" />
        <p className="mt-4 text-sm font-medium text-slate-200">Upload reconciliation CSV</p>
        <label className="mt-4 inline-flex cursor-pointer rounded-md bg-slate-100 px-4 py-2 text-sm font-medium text-slate-950 hover:bg-white">
          {loading ? "Uploading" : "Choose file"}
          <input
            type="file"
            accept=".csv,text/csv"
            className="sr-only"
            onChange={(event) => {
              const file = event.target.files?.item(0);
              if (file) {
                void upload(file);
              }
            }}
          />
        </label>
      </div>

      {report ? (
        <>
          <div className="grid gap-4 sm:grid-cols-2 xl:grid-cols-4">
            <StatCard label="Matched" value={report.matched} icon={UploadCloud} tone="success" />
            <StatCard label="Missing" value={report.unmatched} icon={UploadCloud} tone="danger" />
            <StatCard label="Duplicate" value={report.duplicates} icon={UploadCloud} tone="warning" />
            <StatCard label="Amount Mismatch" value={report.amountMismatches} icon={UploadCloud} tone="warning" />
          </div>

          <div className="flex items-center justify-between">
            <p className="text-sm text-slate-500">Report #{report.id} · {formatDateTime(report.reconciliationTimestamp)}</p>
            <button onClick={exportReport} className="inline-flex items-center gap-2 rounded-md border border-slate-700 px-4 py-2 text-sm text-slate-200 hover:bg-surface-800">
              <Download className="h-4 w-4" />
              Download CSV
            </button>
          </div>

          <DataTable
            rows={report.records}
            columns={[
              { key: "reference", header: "Reference", render: (row) => row.transactionReference },
              { key: "external", header: "External", render: (row) => formatMoney(row.externalAmount, row.currency) },
              { key: "internal", header: "Internal", render: (row) => formatMoney(row.internalAmount, row.currency) },
              { key: "status", header: "Status", render: (row) => <StatusBadge status={row.status} /> },
              { key: "details", header: "Details", render: (row) => row.details ?? "—" }
            ]}
            emptyTitle="No reconciliation records"
          />
        </>
      ) : (
        <EmptyState title="No reconciliation report loaded" />
      )}
    </div>
  );
}
