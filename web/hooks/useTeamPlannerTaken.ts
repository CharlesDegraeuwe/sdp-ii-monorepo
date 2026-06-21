'use client';

import { useQuery } from '@tanstack/react-query';
import type { PlannerTaak } from '@/components/app/planner/types';
import { mapTaakVanBackend } from '@/components/app/planner/utils';

export function useTeamPlannerTaken(
  werknemerIds: number[],
  enabled: boolean,
): { data: Record<number, PlannerTaak[]> } {
  return useQuery<Record<number, PlannerTaak[]>>({
    queryKey: ['team-planner-taken', werknemerIds],
    queryFn: async () => {
      const results = await Promise.all(
        werknemerIds.map((id) =>
          fetch(`/api/taken/werknemer/${id}`)
            .then((r) => (r.ok ? r.json() : []))
            .then(
              (data: Record<string, unknown>[]) =>
                [id, data.map(mapTaakVanBackend)] as [number, PlannerTaak[]],
            )
            .catch(() => [id, []] as [number, PlannerTaak[]]),
        ),
      );
      return Object.fromEntries(results);
    },
    enabled: enabled && werknemerIds.length > 0,
    initialData: {},
  });
}
