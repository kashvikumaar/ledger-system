import type { Status } from "../types";

export function statusClassName(status: Status | string) {
  switch (status) {
    case "SUCCESS":
    case "COMPLETED":
    case "MATCHED":
      return "border-emerald-500/30 bg-emerald-500/10 text-emerald-300";
    case "FAILED":
    case "COMPENSATION_FAILED":
    case "MISSING_IN_LEDGER":
    case "MISSING_EXTERNALLY":
      return "border-red-500/30 bg-red-500/10 text-red-300";
    case "COMPENSATING":
    case "AMOUNT_MISMATCH":
      return "border-orange-500/30 bg-orange-500/10 text-orange-300";
    case "COMPENSATED":
      return "border-sky-500/30 bg-sky-500/10 text-sky-300";
    case "PENDING":
    case "DUPLICATE_TRANSACTION":
      return "border-yellow-500/30 bg-yellow-500/10 text-yellow-300";
    case "IN_PROGRESS":
    case "STARTED":
      return "border-violet-500/30 bg-violet-500/10 text-violet-300";
    default:
      return "border-slate-600 bg-slate-800 text-slate-300";
  }
}
