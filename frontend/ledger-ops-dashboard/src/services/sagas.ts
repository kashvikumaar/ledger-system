import { apiClient } from "./client";
import type { SagaDetail, SagaSummary } from "../types";

export async function listSagas(params?: { status?: string; recoverable?: boolean }) {
  const response = await apiClient.get<SagaSummary[]>("/operations/sagas", {
    params
  });
  return response.data;
}

export async function getSaga(id: number) {
  const response = await apiClient.get<SagaDetail>(`/operations/sagas/${id}`);
  return response.data;
}
