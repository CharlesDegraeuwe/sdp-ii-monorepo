'use client';

import { useQuery } from '@tanstack/react-query';
import type { Shift } from '@/components/app/planner/types';

export function useEigenPlannerShiften(
  userId: number | undefined,
  van: string,
  tot: string,
) {
  return useQuery<Shift[]>({
    queryKey: ['eigen-planner-shiften', userId, van, tot],
    queryFn: async () => {
      const res = await fetch(
        `/api/shifts/werknemer/${userId}/bereik?van=${van}&tot=${tot}`,
      );
      if (!res.ok) return [];
      return res.json() as Promise<Shift[]>;
    },
    enabled: !!userId,
    initialData: [],
  });
}
