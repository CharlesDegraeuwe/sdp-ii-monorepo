import { useEffect } from 'react';
import { useTeamsStore } from '@/stores/teamStore';

const STALE_MS = 5 * 60 * 1000;

export const useTeamsData = () => {
  const {
    setTeams,
    setWerknemers,
    setSites,
    setLoaded,
    setLastSynced,
    lastSynced,
    loaded,
  } = useTeamsStore();

  useEffect(() => {
    const fetchData = async () => {
      if (lastSynced && Date.now() - lastSynced < STALE_MS) {
        setLoaded(true);
        return;
      }

      try {
        const [teamsRes, werknemersRes, sitesRes] = await Promise.all([
          fetch('/api/teams'),
          fetch('/api/teams/werknemers'),
          fetch('/api/teams/sites'),
        ]);

        const [teams, werknemers, sites] = await Promise.all([
          teamsRes.json(),
          werknemersRes.json(),
          sitesRes.json(),
        ]);

        setTeams(teams);
        setWerknemers(werknemers);
        setSites(sites);
        setLastSynced(Date.now());
        setLoaded(true);
      } catch (e) {
        console.error('Kon teams data niet ophalen', e);
        setLoaded(true);
      }
    };

    void fetchData();
  }, []);

  return { loaded };
};
