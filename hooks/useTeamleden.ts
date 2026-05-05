import { useEffect } from 'react';
import { useTeamsStore } from '@/stores/teamStore';

export const useTeamLeden = (teamId: number | null) => {
  const teamLeden = useTeamsStore((s) => s.teamLeden);
  const setTeamLeden = useTeamsStore((s) => s.setTeamLeden);

  useEffect(() => {
    if (!teamId || teamLeden[teamId]) return;

    const fetchLeden = async () => {
      try {
        const res = await fetch(`/api/teams/${teamId}/leden`);
        const data = await res.json();
        setTeamLeden(teamId, data);
      } catch (e) {
        console.error('Kon team leden niet ophalen', e);
      }
    };

    void fetchLeden();
  }, [teamId]);

  return teamId ? (teamLeden[teamId] ?? []) : [];
};
