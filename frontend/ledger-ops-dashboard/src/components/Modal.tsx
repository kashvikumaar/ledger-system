import type { ReactNode } from "react";
import { X } from "lucide-react";

export function Modal({
  open,
  title,
  children,
  onClose
}: {
  open: boolean;
  title: string;
  children: ReactNode;
  onClose: () => void;
}) {
  if (!open) {
    return null;
  }

  return (
    <div className="fixed inset-0 z-40 flex items-center justify-center p-4">
      <button className="absolute inset-0 bg-black/65" onClick={onClose} aria-label="Close modal" />
      <section className="relative w-full max-w-lg rounded-lg border border-slate-800 bg-surface-900 p-6 shadow-panel">
        <div className="flex items-center justify-between">
          <h2 className="text-lg font-semibold text-slate-50">{title}</h2>
          <button onClick={onClose} className="rounded-md p-2 text-slate-400 hover:bg-surface-800">
            <X className="h-4 w-4" />
          </button>
        </div>
        <div className="mt-5">{children}</div>
      </section>
    </div>
  );
}
