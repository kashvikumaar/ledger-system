import { apiClient } from "./client";
import type { HealthStatus } from "../types";

export async function getHealthStatus() {
  const response = await apiClient.get<HealthStatus>("/operations/health");
  return response.data;
}
