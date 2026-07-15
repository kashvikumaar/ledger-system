import { apiClient } from "./client";
import type { DashboardSummary } from "../types";

export async function getDashboardSummary() {
  const response = await apiClient.get<DashboardSummary>("/operations/dashboard");
  return response.data;
}
