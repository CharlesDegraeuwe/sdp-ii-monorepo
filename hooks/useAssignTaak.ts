import { useMutation, useQueryClient } from '@tanstack/react-query';
import { TAKEN_KEY } from './useTaken';

export const useAssignTaak = () => {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: async ({
      taskId,
      memberId,
    }: {
      taskId: string;
      memberId: string;
    }) => {
      const res = await fetch(`/api/taken/${taskId}/toewijzen`, {
        method: 'PUT',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ werknemerId: Number(memberId) }),
      });
      if (!res.ok) throw new Error('Toewijzen mislukt');
    },
    onSuccess: () => {
      void queryClient.invalidateQueries({ queryKey: TAKEN_KEY });
    },
  });
};
