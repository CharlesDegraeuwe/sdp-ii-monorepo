import { useEffect } from 'react';
import {
  useTeamsStore,
  Werknemer,
  Role,
  Status,
  Team,
} from '@/stores/teamStore';

const STALE_MS = 5 * 60 * 1000;

function mapWerknemer(w: Record<string, unknown>): Werknemer {
  return {
    id: w.id as number,
    voornaam: (w.voornaam as string) ?? '',
    naam: (w.naam as string) ?? '',
    email: (w.email as string) ?? '',
    telefoon: (w.telefoonnummer as string) ?? '',
    beschikbaarheid: '',
    siteId: 0,
    siteNaam: '',
    role: (w.rol as Role) ?? 'Werknemer',
    status: (w.status as Status) ?? 'Actief',
  };
}

export const useTeamsData = () => {
  const {
    setTeams,
    setWerknemers,
    setSites,
    setTeamLeden,
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

        const [teams, werknemersRaw, sites] = await Promise.all([
          teamsRes.json(),
          werknemersRes.json(),
          sitesRes.json(),
        ]);

        const werknemers = (werknemersRaw as Record<string, unknown>[]).map(
          mapWerknemer,
        );

        setTeams(teams);
        setWerknemers(werknemers);
        setSites(sites);

        // Fetch leden per team
        const teamList = teams as Team[];
        await Promise.all(
          teamList.map(async (team: Team) => {
            try {
              const res = await fetch(`/api/teams/${team.id}/leden`);
              if (!res.ok) return;
              const leden = await res.json();
              setTeamLeden(team.id, leden);
            } catch {
              // ignore
            }
          }),
        );

        setLastSynced(Date.now());
        setLoaded(true);
      } catch (e) {
        console.error('Kon teams data niet ophalen', e);
        setLoaded(true);
      }
    };

    void fetchData();
  }, [
    lastSynced,
    setLastSynced,
    setLoaded,
    setSites,
    setTeamLeden,
    setTeams,
    setWerknemers,
  ]);

  return { loaded };
};
