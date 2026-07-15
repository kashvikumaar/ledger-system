export function Skeleton({ className = "" }: { className?: string }) {
  return <div className={`animate-pulse rounded-md bg-slate-800/80 ${className}`} />;
}

export function TableSkeleton() {
  return (
    <div className="rounded-lg border border-slate-800 bg-surface-900 p-5 shadow-subtle">
      <Skeleton className="h-8 w-48" />
      <div className="mt-5 space-y-3">
        {Array.from({ length: 6 }).map((_, index) => (
          <Skeleton key={index} className="h-10 w-full" />
        ))}
      </div>
    </div>
  );
}
