import { useMutation, useQueryClient } from '@tanstack/react-query';
import { TAKEN_KEY } from './useTaken';

export const useDeleteTaak = () => {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: async (taskId: string) => {
      const res = await fetch(`/api/taken/${taskId}`, { method: 'DELETE' });
      if (!res.ok) throw new Error('Verwijderen mislukt');
    },
    onSuccess: () => {
      void queryClient.invalidateQueries({ queryKey: TAKEN_KEY });
    },
  });
};
