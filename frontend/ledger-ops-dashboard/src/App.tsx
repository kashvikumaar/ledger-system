import { Navigate, Route, Routes } from "react-router-dom";
import { AppLayout } from "./layouts/AppLayout";
import { AccountsPage } from "./pages/AccountsPage";
import { CreateTransactionPage } from "./pages/CreateTransactionPage";
import { DashboardPage } from "./pages/DashboardPage";
import { ReconciliationPage } from "./pages/ReconciliationPage";
import { RecoveryMonitorPage } from "./pages/RecoveryMonitorPage";
import { SagaMonitorPage } from "./pages/SagaMonitorPage";
import { SystemHealthPage } from "./pages/SystemHealthPage";
import { TransactionsPage } from "./pages/TransactionsPage";

export default function App() {
  return (
    <Routes>
      <Route element={<AppLayout />}>
        <Route index element={<DashboardPage />} />
        <Route path="transactions/new" element={<CreateTransactionPage />} />
        <Route path="accounts" element={<AccountsPage />} />
        <Route path="transactions" element={<TransactionsPage />} />
        <Route path="sagas" element={<SagaMonitorPage />} />
        <Route path="recovery" element={<RecoveryMonitorPage />} />
        <Route path="reconciliation" element={<ReconciliationPage />} />
        <Route path="health" element={<SystemHealthPage />} />
      </Route>
      <Route path="*" element={<Navigate to="/" replace />} />
    </Routes>
  );
}
