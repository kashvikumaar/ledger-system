import { CircleCheck, CircleDot } from "lucide-react";
import { StatusBadge } from "./StatusBadge";

export interface TimelineItem {
  title: string;
  status: string;
  timestamp?: string;
  detail?: string;
}

export function Timeline({ items }: { items: TimelineItem[] }) {
  return (
    <div className="space-y-4">
      {items.map((item, index) => {
        const completed = item.status === "COMPLETED" || item.status === "COMPENSATED";
        const Icon = completed ? CircleCheck : CircleDot;

        return (
          <div key={`${item.title}-${index}`} className="flex gap-3">
            <div className="flex flex-col items-center">
              <Icon className={`h-5 w-5 ${completed ? "text-emerald-300" : "text-slate-500"}`} />
              {index < items.length - 1 && <div className="mt-2 h-full min-h-8 w-px bg-slate-800" />}
            </div>
            <div className="pb-3">
              <div className="flex flex-wrap items-center gap-2">
                <p className="text-sm font-medium text-slate-100">{item.title}</p>
                <StatusBadge status={item.status} />
              </div>
              {item.detail && <p className="mt-1 text-sm text-slate-500">{item.detail}</p>}
            </div>
          </div>
        );
      })}
    </div>
  );
}
