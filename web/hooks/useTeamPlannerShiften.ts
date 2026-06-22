'use client';

import { useQuery } from '@tanstack/react-query';
import type { Shift } from '@/components/app/planner/types';

export function useTeamPlannerShiften(
  werknemerIds: number[],
  van: string,
  tot: string,
  enabled: boolean,
) {
  return useQuery<Shift[]>({
    queryKey: ['team-planner-shiften', werknemerIds, van, tot],
    queryFn: async () => {
      const results = await Promise.all(
        werknemerIds.map((id) =>
          fetch(`/api/shifts/werknemer/${id}/bereik?van=${van}&tot=${tot}`)
            .then((r) => (r.ok ? (r.json() as Promise<Shift[]>) : []))
            .catch(() => [] as Shift[]),
        ),
      );
      return results.flat();
    },
    enabled: enabled && werknemerIds.length > 0,
    initialData: [],
  });
}
