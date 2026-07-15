import { statusClassName } from "../utils/status";

export function StatusBadge({ status }: { status: string }) {
  return (
    <span
      className={`inline-flex items-center gap-1.5 rounded-full border px-2.5 py-1 text-[11px] font-medium uppercase tracking-wide ${statusClassName(
        status
      )}`}
    >
      <span className="h-1.5 w-1.5 rounded-full bg-current opacity-80" />
      {status.replaceAll("_", " ")}
    </span>
  );
}
