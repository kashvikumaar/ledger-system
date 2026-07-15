import { Copy, RefreshCw, Send } from "lucide-react";
import { FormEvent, useMemo, useState, type ReactNode } from "react";
import { StatusBadge } from "../components/StatusBadge";
import { useToast } from "../components/Toast";
import { createTransaction } from "../services/transactions";
import { createIdempotencyKey, formatMoney } from "../utils/format";

export function CreateTransactionPage() {
  const toast = useToast();
  const [externalReference, setExternalReference] = useState("pay_7AF93KD");
  const [sourceAccount, setSourceAccount] = useState("1");
  const [destinationAccount, setDestinationAccount] = useState("2");
  const [amount, setAmount] = useState("500");
  const [currency, setCurrency] = useState("INR");
  const [idempotencyKey, setIdempotencyKey] = useState<string>(
    createIdempotencyKey()
  );
  const [loading, setLoading] = useState(false);
  const [result, setResult] = useState<string | null>(null);

  const numericAmount = useMemo(() => Number(amount || 0), [amount]);

  async function handleSubmit(event: FormEvent) {
    event.preventDefault();
    setLoading(true);
    setResult(null);

    try {
      const response = await createTransaction(
        {
          externalTransactionReference: externalReference,
          entries: [
            { accountId: Number(sourceAccount), type: "DEBIT", amount: numericAmount },
            { accountId: Number(destinationAccount), type: "CREDIT", amount: numericAmount }
          ]
        },
        idempotencyKey
      );
      setResult(response);
      toast.success("Transaction submitted");
    } catch (error) {
      toast.error(error instanceof Error ? error.message : "Transaction failed");
    } finally {
      setLoading(false);
    }
  }

  async function copy(value: string, label: string) {
    await navigator.clipboard.writeText(value);
    toast.success(`${label} copied`);
  }

  return (
    <div className="grid gap-6 xl:grid-cols-[minmax(0,1fr)_22rem]">
      <form onSubmit={handleSubmit} className="rounded-lg border border-slate-800 bg-surface-900 p-6 shadow-panel">
        <div className="grid gap-5 md:grid-cols-2">
          <Field label="External Transaction Reference">
            <div className="flex gap-2">
              <input value={externalReference} onChange={(event) => setExternalReference(event.target.value)} className="input" />
              <IconButton type="button" onClick={() => copy(externalReference, "Reference")}>
                <Copy className="h-4 w-4" />
              </IconButton>
            </div>
          </Field>
          <Field label="Currency">
            <input value={currency} onChange={(event) => setCurrency(event.target.value)} className="input" />
          </Field>
          <Field label="Source Account">
            <input value={sourceAccount} onChange={(event) => setSourceAccount(event.target.value)} className="input" inputMode="numeric" />
          </Field>
          <Field label="Destination Account">
            <input value={destinationAccount} onChange={(event) => setDestinationAccount(event.target.value)} className="input" inputMode="numeric" />
          </Field>
          <Field label="Amount">
            <input value={amount} onChange={(event) => setAmount(event.target.value)} className="input" inputMode="numeric" />
          </Field>
          <Field label="Idempotency Key">
            <div className="flex gap-2">
              <input value={idempotencyKey} onChange={(event) => setIdempotencyKey(event.target.value)} className="input" />
              <IconButton type="button" onClick={() => setIdempotencyKey(createIdempotencyKey())}>
                <RefreshCw className="h-4 w-4" />
              </IconButton>
              <IconButton type="button" onClick={() => copy(idempotencyKey, "Idempotency key")}>
                <Copy className="h-4 w-4" />
              </IconButton>
            </div>
          </Field>
        </div>
        <button
          type="submit"
          disabled={loading}
          className="mt-6 inline-flex items-center gap-2 rounded-md bg-slate-100 px-4 py-2 text-sm font-medium text-slate-950 hover:bg-white disabled:opacity-60"
        >
          <Send className="h-4 w-4" />
          {loading ? "Submitting" : "Submit"}
        </button>
      </form>

      <aside className="rounded-lg border border-slate-800 bg-surface-900 p-6 shadow-panel">
        <p className="text-sm font-semibold text-slate-200">Transaction Summary</p>
        <div className="mt-5 space-y-4 text-sm">
          <SummaryRow label="Reference" value={externalReference} />
          <SummaryRow label="Amount" value={formatMoney(numericAmount, currency)} />
          <SummaryRow label="Source" value={`Account ${sourceAccount}`} />
          <SummaryRow label="Destination" value={`Account ${destinationAccount}`} />
          {result && (
            <div className="rounded-md border border-emerald-500/20 bg-emerald-500/10 p-3">
              <StatusBadge status="SUCCESS" />
              <p className="mt-2 text-sm text-emerald-200">{result}</p>
            </div>
          )}
        </div>
      </aside>
    </div>
  );
}

function Field({ label, children }: { label: string; children: ReactNode }) {
  return (
    <label className="block text-sm">
      <span className="mb-2 block text-slate-400">{label}</span>
      {children}
    </label>
  );
}

function IconButton({ children, ...props }: React.ButtonHTMLAttributes<HTMLButtonElement>) {
  return (
    <button
      {...props}
      className="rounded-md border border-slate-700 px-3 text-slate-300 hover:bg-surface-800"
    >
      {children}
    </button>
  );
}

function SummaryRow({ label, value }: { label: string; value: string }) {
  return (
    <div className="flex items-center justify-between gap-4 border-b border-slate-800 pb-3">
      <span className="text-slate-500">{label}</span>
      <span className="truncate text-slate-200">{value}</span>
    </div>
  );
}
