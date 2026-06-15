'use client';

import { useCallback, useEffect, useRef, useState } from 'react';

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
  authHeader: Record<string, string>,
  eigenId?: number,
  isSupervisor?: boolean,
) {
  const [sites, setSites] = useState<SiteOptie[]>([]);
  const [teams, setTeams] = useState<TeamOptie[]>([]);
  const [teamWerknemers, setTeamWerknemers] = useState<WerknemerOptie[]>([]);
  const [alleWerknemers, setAlleWerknemers] = useState<WerknemerMetTeam[]>([]);

  // Ref zodat callbacks stabiel blijven ook al verandert authHeader (token refresh)
  const authRef = useRef(authHeader);
  authRef.current = authHeader;

  useEffect(() => {
    const bearer = authHeader.Authorization;
    if (!bearer || bearer === 'Bearer undefined') return;

    if (isSupervisor && eigenId) {
      fetch(`${BASE}/teams/werknemer/${eigenId}`, { headers: authHeader })
        .then((r) => (r.ok ? (r.json() as Promise<TeamOptie[]>) : Promise.resolve([])))
        .then((supervisorTeams) => {
          setTeams(supervisorTeams);
          const uniekeSites = Array.from(
            new Map(
              supervisorTeams
                .filter((t) => t.siteId)
                .map((t) => [t.siteId, { id: t.siteId, naam: t.siteNaam, locatie: '' }]),
            ).values(),
          );
          setSites(uniekeSites);
        })
        .catch(console.error);
      return;
    }

    Promise.all([
      fetch(`${BASE}/teams/sites`, { headers: authHeader }),
      fetch(`${BASE}/teams`, { headers: authHeader }),
    ])
      .then(async ([sitesRes, teamsRes]) => {
        if (sitesRes.ok) setSites(await sitesRes.json());
        if (teamsRes.ok) setTeams(await teamsRes.json());
      })
      .catch(console.error);
  }, [authHeader, eigenId, isSupervisor]);

  const laadWerknemersVanTeam = useCallback(async (teamId: number) => {
    setTeamWerknemers([]);
    const res = await fetch(`${BASE}/teams/${teamId}/werknemers`, {
      headers: authRef.current,
    }).catch(() => null);
    if (res?.ok) setTeamWerknemers(await res.json());
  }, []);

  const resetTeamWerknemers = useCallback(() => setTeamWerknemers([]), []);

  const laadAlleWerknemers = useCallback(async (teamsToLoad: TeamOptie[]) => {
    if (teamsToLoad.length === 0) return;
    setAlleWerknemers([]);
    const results = await Promise.all(
      teamsToLoad.map((t) =>
        fetch(`${BASE}/teams/${t.id}/werknemers`, { headers: authRef.current })
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
    setAlleWerknemers(unique);
  }, []);

  return {
    sites,
    teams,
    teamWerknemers,
    alleWerknemers,
    laadWerknemersVanTeam,
    laadAlleWerknemers,
    resetTeamWerknemers,
  };
}
