import { apiClient } from "./client";
import type { TransactionDetail, TransactionRequest, TransactionSummary } from "../types";

export async function createTransaction(
  request: TransactionRequest,
  idempotencyKey: string
) {
  const response = await apiClient.post<string>("/transactions", request, {
    headers: {
      "Idempotency-Key": idempotencyKey
    }
  });

  return response.data;
}

export async function listTransactions(params?: {
  status?: string;
  search?: string;
  page?: number;
  size?: number;
}) {
  const response = await apiClient.get<TransactionSummary[]>("/operations/transactions", {
    params
  });
  return response.data;
}

export async function getTransaction(id: number) {
  const response = await apiClient.get<TransactionDetail>(`/operations/transactions/${id}`);
  return response.data;
}
