import { useMutation, useQueryClient } from '@tanstack/react-query';
import { mapTaskToBackend } from '@/lib/taakMapper';
import { Task } from '@/stores/taakStore';
import { TAKEN_KEY } from './useTaken';

export const useCreateTaak = () => {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: async (input: Omit<Task, 'id' | 'finished'>) => {
      const res = await fetch('/api/taken', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(mapTaskToBackend(input)),
      });
      if (!res.ok) throw new Error('Aanmaken mislukt');
    },
    onSuccess: () => {
      void queryClient.invalidateQueries({ queryKey: TAKEN_KEY });
    },
  });
};
