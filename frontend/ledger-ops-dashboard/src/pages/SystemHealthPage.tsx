import { Clock, Database, GitBranch, HeartPulse, Server, Wifi } from "lucide-react";
import { EmptyState } from "../components/EmptyState";
import { StatCard } from "../components/StatCard";
import { TableSkeleton } from "../components/Skeleton";
import { getHealthStatus } from "../services/health";
import { formatDateTime } from "../utils/format";
import { useAsync } from "../hooks/useAsync";

export function SystemHealthPage() {
  const { data, loading, error } = useAsync(getHealthStatus, []);

  if (loading) {
    return <TableSkeleton />;
  }

  if (error || !data) {
    return (
      <EmptyState
        title="Health endpoint unavailable"
        detail="/operations/health is needed for infrastructure status."
      />
    );
  }

  return (
    <div className="grid gap-4 sm:grid-cols-2 xl:grid-cols-3">
      <StatCard label="Backend Status" value={data.backendStatus} icon={Server} tone="success" />
      <StatCard label="Database Status" value={data.databaseStatus} icon={Database} tone="success" />
      <StatCard label="Redis Status" value={data.redisStatus} icon={Wifi} tone="success" />
      <StatCard label="Current Time" value={formatDateTime(data.currentTime)} icon={Clock} />
      <StatCard label="Application Version" value={data.applicationVersion} icon={HeartPulse} />
      <StatCard label="Flyway Version" value={data.flywayMigrationVersion} icon={GitBranch} />
    </div>
  );
}
