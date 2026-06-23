import { useQuery, useQueryClient } from '@tanstack/react-query';
import { mapTaakVanBackend } from '@/components/app/planner/utils';
import type { PlannerTaak } from '@/components/app/planner/types';

export const takenVoorWerknemerKey = (werknemerId: number) =>
  ['taken', 'werknemer', werknemerId] as const;

export function useTakenVoorWerknemer(werknemerId: number | undefined) {
  return useQuery({
    queryKey: werknemerId
      ? takenVoorWerknemerKey(werknemerId)
      : ['taken', 'werknemer', null],
    queryFn: async (): Promise<PlannerTaak[]> => {
      if (!werknemerId) return [];
      const res = await fetch(`/api/taken/werknemer/${werknemerId}`);
      if (!res.ok) throw new Error('Kon taken niet ophalen');
      const data: Record<string, unknown>[] = await res.json();
      return data.map(mapTaakVanBackend);
    },
    enabled: !!werknemerId,
  });
}

export function useInvalidateTakenVoorWerknemer() {
  const queryClient = useQueryClient();
  return (werknemerId?: number) => {
    if (werknemerId) {
      void queryClient.invalidateQueries({
        queryKey: takenVoorWerknemerKey(werknemerId),
      });
    }
    void queryClient.invalidateQueries({ queryKey: ['taken'] });
  };
}
