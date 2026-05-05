import { useEffect } from 'react';
import { useTaakStore } from '@/stores/taakStore';
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

        const teams = (teamsData as Record<string, unknown>[]).map((t) => ({
          id: String(t.id),
          name: (t.naam as string) ?? '',
          plant: (t.siteNaam as string) ?? '',
          members: [],
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

        setTeams(teams);
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
  }, []);

  return { loaded };
};
