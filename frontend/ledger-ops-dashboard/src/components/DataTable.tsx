import type { ReactNode } from "react";
import { EmptyState } from "./EmptyState";

interface Column<T> {
  key: string;
  header: string;
  render: (row: T) => ReactNode;
}

interface DataTableProps<T> {
  rows: T[];
  columns: Column<T>[];
  emptyTitle?: string;
  onRowClick?: (row: T) => void;
}

export function DataTable<T>({ rows, columns, emptyTitle = "No records", onRowClick }: DataTableProps<T>) {
  if (rows.length === 0) {
    return <EmptyState title={emptyTitle} />;
  }

  return (
    <div className="overflow-hidden rounded-lg border border-slate-800/90 bg-surface-900 shadow-subtle">
      <div className="overflow-x-auto">
        <table className="min-w-full divide-y divide-slate-800/80">
          <thead className="bg-surface-850">
            <tr>
              {columns.map((column) => (
                <th
                  key={column.key}
                  className="px-4 py-3 text-left text-[11px] font-semibold uppercase tracking-wide text-slate-500"
                >
                  {column.header}
                </th>
              ))}
            </tr>
          </thead>
          <tbody className="divide-y divide-slate-800/80">
            {rows.map((row, index) => (
              <tr
                key={index}
                onClick={() => onRowClick?.(row)}
                className={`transition ${onRowClick ? "cursor-pointer hover:bg-surface-850/80" : ""}`}
              >
                {columns.map((column) => (
                  <td key={column.key} className="whitespace-nowrap px-4 py-3.5 text-sm text-slate-300">
                    {column.render(row)}
                  </td>
                ))}
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  );
}
