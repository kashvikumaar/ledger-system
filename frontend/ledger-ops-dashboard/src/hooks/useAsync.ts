import { useCallback, useEffect, useState } from "react";
import { getErrorMessage } from "../services/client";

export function useAsync<T>(loader: () => Promise<T>, dependencies: unknown[] = []) {
  const [data, setData] = useState<T | null>(null);
  const [error, setError] = useState<string | null>(null);
  const [loading, setLoading] = useState(true);

  const run = useCallback(async () => {
    setLoading(true);
    setError(null);

    try {
      const result = await loader();
      setData(result);
    } catch (caughtError) {
      setError(getErrorMessage(caughtError));
    } finally {
      setLoading(false);
    }
  }, dependencies);

  useEffect(() => {
    void run();
  }, [run]);

  return { data, error, loading, refresh: run };
}
