import type { LucideIcon } from "lucide-react";

interface StatCardProps {
  label: string;
  value: string | number;
  icon: LucideIcon;
  tone?: "default" | "success" | "warning" | "danger";
}

const tones = {
  default: "border-slate-700/70 bg-surface-850 text-slate-300",
  success: "border-emerald-500/20 bg-emerald-500/10 text-emerald-300",
  warning: "border-yellow-500/20 bg-yellow-500/10 text-yellow-300",
  danger: "border-red-500/20 bg-red-500/10 text-red-300"
};

export function StatCard({ label, value, icon: Icon, tone = "default" }: StatCardProps) {
  return (
    <div className="rounded-lg border border-slate-800/90 bg-surface-900 p-5 shadow-subtle transition hover:border-slate-700">
      <div className="flex items-center justify-between gap-4">
        <div>
          <p className="text-xs font-medium uppercase tracking-wide text-slate-500">{label}</p>
          <p className="mt-2 text-2xl font-semibold tabular-nums text-slate-50">{value}</p>
        </div>
        <div className={`rounded-md border p-2.5 shadow-subtle ${tones[tone]}`}>
          <Icon className="h-5 w-5" />
        </div>
      </div>
    </div>
  );
}
