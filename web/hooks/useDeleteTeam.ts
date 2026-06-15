import { useTeamsStore } from '@/stores/teamStore';

export const useDeleteTeam = () => {
  const teams = useTeamsStore((s) => s.teams);
  const removeTeam = useTeamsStore((s) => s.removeTeam);
  const addTeam = useTeamsStore((s) => s.addTeam);

  return async (id: number) => {
    const backup = teams[id];
    removeTeam(id);

    try {
      const res = await fetch(`/api/teams/${id}`, { method: 'DELETE' });
      if (!res.ok) throw new Error('Delete failed');
    } catch (e) {
      if (backup) addTeam(backup);
      throw e;
    }
  };
};
