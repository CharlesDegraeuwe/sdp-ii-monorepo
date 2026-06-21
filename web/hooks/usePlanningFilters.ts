'use client';

import { useMemo } from 'react';
import { useQuery } from '@tanstack/react-query';
import { useSession } from 'next-auth/react';

const BASE = process.env.NEXT_PUBLIC_API_URL ?? 'http://localhost:8080/api';

export interface SiteOptie {
  id: number;
  naam: string;
  locatie: string;
}

export interface TeamOptie {
  id: number;
  naam: string;
  siteId: number;
  siteNaam: string;
}

export interface WerknemerOptie {
  id: number;
  voornaam: string;
  naam: string;
}

export interface WerknemerMetTeam extends WerknemerOptie {
  teamId: number;
  teamNaam: string;
}

export function usePlanningFilters(
  eigenId: number | undefined,
  isSupervisor: boolean,
  teamId: number | null,
  laadAlle: boolean,
) {
  const { data: session } = useSession();
  const token = session?.accessToken;
  const authHeader = token ? { Authorization: `Bearer ${token}` } : undefined;
  const hasToken = !!token;

  // Supervisor: laad teams van eigen werknemer
  const supervisorTeamsQuery = useQuery<TeamOptie[]>({
    queryKey: ['supervisor-teams', eigenId],
    queryFn: async () => {
      const res = await fetch(`${BASE}/teams/werknemer/${eigenId}`, {
        headers: authHeader!,
      });
      if (!res.ok) return [];
      return res.json() as Promise<TeamOptie[]>;
    },
    enabled: isSupervisor && !!eigenId && hasToken,
    initialData: [],
  });

  // Normal: laad sites
  const sitesQuery = useQuery<SiteOptie[]>({
    queryKey: ['planner-sites'],
    queryFn: async () => {
      const res = await fetch(`${BASE}/teams/sites`, { headers: authHeader! });
      if (!res.ok) return [];
      return res.json() as Promise<SiteOptie[]>;
    },
    enabled: !isSupervisor && hasToken,
    initialData: [],
  });

  // Normal: laad teams
  const teamsQuery = useQuery<TeamOptie[]>({
    queryKey: ['planner-teams', token],
    queryFn: async () => {
      if (!res.ok) return [];
      return res.json() as Promise<TeamOptie[]>;
    },
    enabled: !isSupervisor && hasToken,
    initialData: [],
  });

  const teams: TeamOptie[] = isSupervisor
    ? supervisorTeamsQuery.data
    : teamsQuery.data;

  const calcSupervisorSites = useMemo(
    () =>
      Array.from(
        new Map(
          supervisorTeamsQuery.data
            .filter((t) => t.siteId)
            .map((t) => [
              t.siteId,
              { id: t.siteId, naam: t.siteNaam, locatie: '' },
            ]),
        ).values(),
      ),
    [supervisorTeamsQuery.data],
  );
  const sites: SiteOptie[] = isSupervisor
    ? calcSupervisorSites
    : sitesQuery.data;

  // Team werknemers voor geselecteerd team
  const teamWerknemersQuery = useQuery<WerknemerOptie[]>({
    queryKey: ['team-werknemers', teamId],
    queryFn: async () => {
      const res = await fetch(`${BASE}/teams/${teamId}/werknemers`, {
        headers: authHeader!,
      });
      if (!res.ok) return [];
      return res.json() as Promise<WerknemerOptie[]>;
    },
    enabled: teamId !== null && hasToken,
    initialData: [],
  });

  // Alle werknemers voor alle teams (wanneer laadAlle=true)
  const sortedTeamIds = useMemo(
    () => [...teams.map((t) => t.id)].sort((a, b) => a - b),
    [teams],
  );

  const alleWerknemersQuery = useQuery<WerknemerMetTeam[]>({
    queryKey: ['alle-team-werknemers', sortedTeamIds],
    queryFn: async () => {
      const results = await Promise.all(
        teams.map((t) =>
          fetch(`${BASE}/teams/${t.id}/werknemers`, { headers: authHeader! })
            .then((r) =>
              r.ok
                ? (r.json() as Promise<WerknemerOptie[]>)
                : Promise.resolve([]),
            )
            .then((ws: WerknemerOptie[]) =>
              ws.map((w) => ({ ...w, teamId: t.id, teamNaam: t.naam })),
            )
            .catch(() => [] as WerknemerMetTeam[]),
        ),
      );
      const seen = new Set<number>();
      const unique: WerknemerMetTeam[] = [];
      for (const batch of results) {
        for (const w of batch) {
          if (!seen.has(w.id)) {
            seen.add(w.id);
            unique.push(w);
          }
        }
      }
      return unique;
    },
    enabled: laadAlle && teams.length > 0 && hasToken,
    initialData: [],
  });

  return {
    sites,
    teams,
    teamWerknemers: teamWerknemersQuery.data,
    alleWerknemers: alleWerknemersQuery.data,
  };
}
