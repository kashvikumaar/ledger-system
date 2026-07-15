import { apiClient } from "./client";
import type { ReconciliationReport } from "../types";

export async function uploadReconciliation(file: File) {
  const formData = new FormData();
  formData.append("file", file);

  const response = await apiClient.post<ReconciliationReport>(
    "/reconciliation/upload",
    formData,
    {
      headers: {
        "Content-Type": "multipart/form-data"
      }
    }
  );

  return response.data;
}

export async function getReconciliationReport(id: number) {
  const response = await apiClient.get<ReconciliationReport>(`/reconciliation/${id}`);
  return response.data;
}
