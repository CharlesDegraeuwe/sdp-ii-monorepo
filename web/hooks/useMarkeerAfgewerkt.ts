import { useMutation, useQueryClient } from '@tanstack/react-query';
import { TAKEN_KEY } from './useTaken';

export const useMarkeerAfgewerkt = () => {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: async (taskId: string) => {
      const res = await fetch(`/api/taken/${taskId}/afgewerkt`, {
        method: 'PUT',
      });
      if (!res.ok) throw new Error('Afwerken mislukt');
    },
    onSuccess: () => {
      void queryClient.invalidateQueries({ queryKey: TAKEN_KEY });
    },
  });
};
