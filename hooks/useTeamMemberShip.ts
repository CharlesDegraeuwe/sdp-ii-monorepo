import { useTeamsStore } from '@/stores/teamStore';

export const useTeamMembership = () => {
  const setSupervisor = useTeamsStore((s) => s.setSupervisor);
  const setTeamLeden = useTeamsStore((s) => s.setTeamLeden);
  const teamLeden = useTeamsStore((s) => s.teamLeden);
  const werknemers = useTeamsStore((s) => s.werknemers);

  const voegToe = async (teamId: number, werknemerId: number) => {
    const huidigeLeden = teamLeden[teamId] ?? [];
    const w = werknemers[werknemerId];
    if (!w) return;

    setTeamLeden(teamId, [
      ...huidigeLeden,
      {
        werknemerId,
        voornaam: w.voornaam,
        naam: w.naam,
        isSupervisor: false,
      },
    ]);

    try {
      await fetch(`/api/teams/${teamId}/${werknemerId}`, { method: 'PUT' });
    } catch (e) {
      setTeamLeden(teamId, huidigeLeden);
      throw e;
    }
  };

  const verwijder = async (teamId: number, werknemerId: number) => {
    const huidigeLeden = teamLeden[teamId] ?? [];
    setTeamLeden(
      teamId,
      huidigeLeden.filter((l) => l.werknemerId !== werknemerId),
    );

    try {
      await fetch(`/api/teams/${teamId}/${werknemerId}`, { method: 'DELETE' });
    } catch (e) {
      setTeamLeden(teamId, huidigeLeden);
      throw e;
    }
  };

  const promoot = async (teamId: number, werknemerId: number) => {
    setSupervisor(teamId, werknemerId);

    try {
      await fetch(`/api/teams/${teamId}/${werknemerId}/supervisor`, {
        method: 'PUT',
      });
    } catch (e) {
      console.error('Kon supervisor niet promoten', e);
    }
  };

  return { voegToe, verwijder, promoot };
};
