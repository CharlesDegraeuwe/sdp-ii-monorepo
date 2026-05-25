import { useQuery } from '@tanstack/react-query';
import { mapBackendTask } from '@/lib/taakMapper';
import { Task } from '@/stores/taakStore';

export const TAKEN_KEY = ['taken'] as const;

export const useTaken = () => {
  return useQuery({
    queryKey: TAKEN_KEY,
    queryFn: async (): Promise<Task[]> => {
      const res = await fetch('/api/taken/alle');
      if (!res.ok) throw new Error('Kon taken niet ophalen');
      const data = await res.json();
      return (data as Record<string, unknown>[]).map(mapBackendTask);
    },
  });
};
