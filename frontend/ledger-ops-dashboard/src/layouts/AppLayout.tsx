import {
  Activity,
  Banknote,
  Gauge,
  HeartPulse,
  Home,
  Landmark,
  RefreshCw,
  Route,
  ShieldCheck
} from "lucide-react";
import { NavLink, Outlet, useLocation } from "react-router-dom";

const navigation = [
  { label: "Dashboard", href: "/", icon: Home },
  { label: "Create Transaction", href: "/transactions/new", icon: Banknote },
  { label: "Accounts", href: "/accounts", icon: Landmark },
  { label: "Transactions", href: "/transactions", icon: Activity },
  { label: "Saga Monitor", href: "/sagas", icon: Route },
  { label: "Recovery Monitor", href: "/recovery", icon: ShieldCheck },
  { label: "Reconciliation", href: "/reconciliation", icon: RefreshCw },
  { label: "System Health", href: "/health", icon: HeartPulse }
];

const titles: Record<string, string> = {
  "/": "Operations Dashboard",
  "/transactions/new": "Create Transaction",
  "/accounts": "Accounts",
  "/transactions": "Transactions",
  "/sagas": "Saga Monitor",
  "/recovery": "Recovery Monitor",
  "/reconciliation": "Reconciliation",
  "/health": "System Health"
};

export function AppLayout() {
  const location = useLocation();
  const title = titles[location.pathname] ?? "LedgerCore Ops";

  return (
    <div className="min-h-screen bg-surface-950 text-slate-100">
      <aside className="fixed inset-y-0 left-0 z-30 hidden w-68 border-r border-slate-800/90 bg-surface-900 lg:block">
        <div className="flex h-16 items-center gap-3 border-b border-slate-800 px-5">
          <div className="rounded-md border border-slate-700 bg-surface-850 p-2 text-slate-200">
            <Gauge className="h-5 w-5" />
          </div>
          <div>
            <p className="text-sm font-semibold text-slate-50">LedgerCore</p>
            <p className="text-xs text-slate-500">Operations Console</p>
          </div>
        </div>
        <nav className="space-y-1 px-3 py-4">
          {navigation.map((item) => (
            <NavLink
              key={item.href}
              to={item.href}
              end={item.href === "/"}
              className={({ isActive }) =>
                `flex items-center gap-3 rounded-md px-3 py-2.5 text-sm transition ${
                  isActive
                    ? "bg-surface-800 text-slate-50 shadow-subtle"
                    : "text-slate-400 hover:bg-surface-850 hover:text-slate-100"
                }`
              }
            >
              <item.icon className="h-4 w-4" />
              {item.label}
            </NavLink>
          ))}
        </nav>
      </aside>

      <div className="lg:pl-68">
        <header className="sticky top-0 z-20 border-b border-slate-800/90 bg-surface-950/90 backdrop-blur">
          <div className="flex min-h-16 items-center justify-between gap-4 px-4 sm:px-6 lg:px-8">
            <div>
              <h1 className="text-lg font-semibold tracking-tight text-slate-50">{title}</h1>
              <p className="text-xs text-slate-500">Production-inspired payment operations</p>
            </div>
            <div className="hidden items-center gap-2 rounded-md border border-slate-800 bg-surface-900 px-3 py-2 text-xs text-slate-400 sm:flex">
              <span className="h-2 w-2 rounded-full bg-emerald-400" />
              Dark mode
            </div>
          </div>
          <div className="flex gap-2 overflow-x-auto border-t border-slate-900 px-4 py-2 lg:hidden">
            {navigation.map((item) => (
              <NavLink
                key={item.href}
                to={item.href}
                end={item.href === "/"}
                className={({ isActive }) =>
                  `flex shrink-0 items-center gap-2 rounded-md px-3 py-2 text-xs ${
                    isActive ? "bg-surface-800 text-slate-50" : "text-slate-400"
                  }`
                }
              >
                <item.icon className="h-4 w-4" />
                {item.label}
              </NavLink>
            ))}
          </div>
        </header>
        <main className="px-4 py-6 sm:px-6 lg:px-8">
          <Outlet />
        </main>
      </div>
    </div>
  );
}
