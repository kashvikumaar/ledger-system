import { apiClient } from "./client";
import type { AccountSummary, LedgerEntry } from "../types";

export async function searchAccounts(search?: string) {
  const response = await apiClient.get<AccountSummary[]>("/operations/accounts", {
    params: { search }
  });
  return response.data;
}

export async function getAccountBalance(accountId: number) {
  const response = await apiClient.get<number>(`/transactions/accounts/${accountId}/balance`);
  return response.data;
}

export async function getAccountEntries(accountId: number) {
  const response = await apiClient.get<LedgerEntry[]>(
    `/operations/accounts/${accountId}/entries`
  );
  return response.data;
}
