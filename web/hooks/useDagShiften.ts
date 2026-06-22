'use client';

import { useQuery } from '@tanstack/react-query';
import type { Shift } from '@/components/app/planner/types';

export function useDagShiften(werknemerIds: number[], datum: string) {
  return useQuery<Shift[]>({
    queryKey: ['dag-shiften', werknemerIds, datum],
    queryFn: async () => {
      const results = await Promise.all(
        werknemerIds.map((id) =>
          fetch(`/api/shifts/werknemer/${id}?datum=${datum}`)
            .then((r) => (r.ok ? (r.json() as Promise<Shift[]>) : []))
            .catch(() => [] as Shift[]),
        ),
      );
      return results.flat();
    },
    enabled: werknemerIds.length > 0,
    initialData: [],
  });
}
