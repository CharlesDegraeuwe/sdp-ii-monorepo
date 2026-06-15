import { useEffect } from 'react';
import { useTaakStore, Team, TeamMember } from '@/stores/taakStore';
import { mapBackendTask } from '@/lib/taakMapper';

const STALE_MS = 5 * 60 * 1000;

export const useTaakData = () => {
  const {
    setTeams,
    setMembers,
    setTasks,
    setLoaded,
    setLastSynced,
    lastSynced,
    loaded,
  } = useTaakStore();

  useEffect(() => {
    const fetchData = async () => {
      if (lastSynced && Date.now() - lastSynced < STALE_MS) {
        setLoaded(true);
        return;
      }

      try {
        const [teamsRes, werknemersRes, takenRes] = await Promise.all([
          fetch('/api/teams'),
          fetch('/api/teams/werknemers'),
          fetch('/api/taken/alle'),
        ]);

        const [teamsData, werknemersData, takenData] = await Promise.all([
          teamsRes.json(),
          werknemersRes.json(),
          takenRes.json(),
        ]);

        const baseTeams = (teamsData as Record<string, unknown>[]).map((t) => ({
          id: String(t.id),
          name: (t.naam as string) ?? '',
          plant: (t.siteNaam as string) ?? '',
          members: [] as TeamMember[],
        }));

        const members = (werknemersData as Record<string, unknown>[]).map(
          (w) => ({
            id: String(w.id),
            firstName: (w.voornaam as string) ?? '',
            lastName: (w.naam as string) ?? '',
            email: (w.email as string) ?? '',
          }),
        );

        const tasks = (takenData as Record<string, unknown>[]).map(
          mapBackendTask,
        );

        // Fetch members per team
        const ledenResults = await Promise.all(
          baseTeams.map(async (team): Promise<Team> => {
            try {
              const res = await fetch(`/api/teams/${team.id}/leden`);
              if (!res.ok) return team;
              const leden = (await res.json()) as Record<string, unknown>[];
              return {
                ...team,
                members: leden.map((l) => ({
                  id: String(l.werknemerId),
                  firstName: (l.voornaam as string) ?? '',
                  lastName: (l.naam as string) ?? '',
                  email: (l.email as string) ?? '',
                })),
              };
            } catch {
              return team;
            }
          }),
        );

        setTeams(ledenResults);
        setMembers(members);
        setTasks(tasks);
        setLastSynced(Date.now());
        setLoaded(true);
      } catch (e) {
        console.error('Kon taakdata niet ophalen', e);
        setLoaded(true);
      }
    };

    void fetchData();
  }, [lastSynced, setLastSynced, setLoaded, setMembers, setTasks, setTeams]);

  return { loaded };
};
