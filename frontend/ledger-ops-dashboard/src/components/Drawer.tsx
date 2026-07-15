import type { ReactNode } from "react";
import { X } from "lucide-react";

interface DrawerProps {
  open: boolean;
  title: string;
  children: ReactNode;
  onClose: () => void;
}

export function Drawer({ open, title, children, onClose }: DrawerProps) {
  if (!open) {
    return null;
  }

  return (
    <div className="fixed inset-0 z-40">
      <button className="absolute inset-0 bg-black/60" onClick={onClose} aria-label="Close drawer" />
      <aside className="absolute right-0 top-0 h-full w-full max-w-xl overflow-y-auto border-l border-slate-800 bg-surface-900 p-6 shadow-panel">
        <div className="flex items-center justify-between gap-4">
          <h2 className="text-lg font-semibold text-slate-50">{title}</h2>
          <button
            onClick={onClose}
            className="rounded-md border border-slate-700 p-2 text-slate-400 hover:bg-surface-800 hover:text-slate-100"
            aria-label="Close"
          >
            <X className="h-4 w-4" />
          </button>
        </div>
        <div className="mt-6">{children}</div>
      </aside>
    </div>
  );
}
