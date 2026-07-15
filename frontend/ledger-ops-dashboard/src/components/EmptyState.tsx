import { Inbox } from "lucide-react";

export function EmptyState({ title, detail }: { title: string; detail?: string }) {
  return (
    <div className="flex min-h-44 items-center justify-center rounded-lg border border-dashed border-slate-800 bg-surface-900/70 p-8 text-center shadow-subtle">
      <div>
        <div className="mx-auto flex h-11 w-11 items-center justify-center rounded-md border border-slate-800 bg-surface-850 text-slate-500">
          <Inbox className="h-5 w-5" />
        </div>
        <p className="mt-4 text-sm font-medium text-slate-300">{title}</p>
        {detail && <p className="mt-1 text-sm text-slate-500">{detail}</p>}
      </div>
    </div>
  );
}
