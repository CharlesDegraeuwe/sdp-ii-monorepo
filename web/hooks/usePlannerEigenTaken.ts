'use client';

import { useQuery } from '@tanstack/react-query';
import type { PlannerTaak } from '@/components/app/planner/types';
import { mapTaakVanBackend } from '@/components/app/planner/utils';

export function usePlannerEigenTaken(userId: number | undefined) {
  return useQuery<PlannerTaak[]>({
    queryKey: ['planner-taken-werknemer', userId],
    queryFn: async () => {
      const res = await fetch(`/api/taken/werknemer/${userId}`);
      if (!res.ok) return [];
      const data: Record<string, unknown>[] = await res.json();
      return data.map(mapTaakVanBackend);
    },
    enabled: !!userId,
    initialData: [],
  });
}
