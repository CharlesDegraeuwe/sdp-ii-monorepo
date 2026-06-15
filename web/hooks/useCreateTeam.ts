import { useTeamsStore } from '@/stores/teamStore';
import { Team } from '@/types/types';

export interface CreateTeamInput {
  naam: string;
  beschrijving: string;
  managerId: number;
  siteId: number;
  leden: { werknemerId: number; isSupervisor: boolean }[];
}

export const useCreateTeam = () => {
  const addTeam = useTeamsStore((s) => s.addTeam);
  const removeTeam = useTeamsStore((s) => s.removeTeam);
  const setTeamLeden = useTeamsStore((s) => s.setTeamLeden);
  const werknemers = useTeamsStore((s) => s.werknemers);

  return async (input: CreateTeamInput) => {
    const tempId = -Date.now();
    const manager = werknemers[input.managerId];
    const site = useTeamsStore.getState().sites[input.siteId];

    const optimistic: Team = {
      id: tempId,
      naam: input.naam,
      beschrijving: input.beschrijving,
      managerId: input.managerId,
      managerNaam: manager ? `${manager.voornaam} ${manager.naam}` : '',
      siteId: input.siteId,
      siteNaam: site?.naam ?? '',
    };

    addTeam(optimistic);
    setTeamLeden(
      tempId,
      input.leden.map((l) => {
        const w = werknemers[l.werknemerId];
        return {
          werknemerId: l.werknemerId,
          voornaam: w?.voornaam ?? '',
          naam: w?.naam ?? '',
          isSupervisor: l.isSupervisor,
        };
      }),
    );

    try {
      const res = await fetch('/api/teams', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(input),
      });
      if (!res.ok) throw new Error('Create failed');
      const saved: Team = await res.json();

      removeTeam(tempId);
      addTeam(saved);

      const ledenRes = await fetch(`/api/teams/${saved.id}/leden`);
      const leden = await ledenRes.json();
      setTeamLeden(saved.id, leden);

      return saved;
    } catch (e) {
      removeTeam(tempId);
      throw e;
    }
  };
};
