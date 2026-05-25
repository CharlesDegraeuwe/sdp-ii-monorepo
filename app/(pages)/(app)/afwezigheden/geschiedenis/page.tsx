'use client';
import Container from '../../../../../components/design-system/Container/Container';
import { useSession } from 'next-auth/react';
import { useEffect, useMemo, useState } from 'react';
import { Label } from '@/components/design-system/Label';

interface GeschiedenisItem {
  id: number;
  type: string;
  startDatum: string;
  eindDatum: string;
  status: string | null;
  omschrijving: string | null;
}

interface Teamlid {
  id: number;
  naam: string;
  voornaam: string;
  rol: string;
}

const BASE = process.env.NEXT_PUBLIC_API_URL ?? 'http://localhost:8080/api';

export default function GeschiedenisPage() {
  const { data: session } = useSession();
  const token = session?.accessToken;
  const sessionUser = session?.user as Record<string, unknown> | undefined;
  const eigenId = Number(sessionUser?.id ?? 0);
  const rol = (sessionUser?.rol as string) ?? '';

  const isWerknemer = rol === 'Werknemer';
  const isManagerOrSupervisor = ['Manager', 'Supervisor', 'Admin'].includes(rol);

  const [teamleden, setTeamleden] = useState<Teamlid[]>([]);
  const [geselecteerdLid, setGeselecteerdLid] = useState<Teamlid | null>(null);
  const [geschiedenis, setGeschiedenis] = useState<GeschiedenisItem[]>([]);
  const [geschiedenisLaden, setGeschiedenisLaden] = useState(false);

  const authHeader = useMemo(
    () => ({ Authorization: `Bearer ${token}` }),
    [token],
  );

  async function laadGeschiedenis(lid: Teamlid) {
    setGeselecteerdLid(lid);
    setGeschiedenisLaden(true);
    try {
      const res = await fetch(`${BASE}/geschiedenis/werknemer/${lid.id}`, {
        headers: authHeader,
      });
      setGeschiedenis(await res.json());
    } catch {
      setGeschiedenis([]);
    } finally {
      setGeschiedenisLaden(false);
    }
  }

  // Employee: auto-load own absence history on mount
  useEffect(() => {
    if (!token || !eigenId || !isWerknemer) return;
    const zelf: Teamlid = {
      id: eigenId,
      naam: (sessionUser?.naam as string) ?? '',
      voornaam: (sessionUser?.voornaam as string) ?? '',
      rol,
    };
    setGeselecteerdLid(zelf);
    setGeschiedenisLaden(true);
    fetch(`${BASE}/geschiedenis/werknemer/${eigenId}`, { headers: authHeader })
      .then((res) => res.json())
      .then(setGeschiedenis)
      .catch(() => setGeschiedenis([]))
      .finally(() => setGeschiedenisLaden(false));
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [token, eigenId]);

  // Manager/Supervisor: fetch team members and auto-select self
  useEffect(() => {
    if (!token || !eigenId || !isManagerOrSupervisor) return;

    async function init() {
      try {
        const teamsRes = await fetch(`${BASE}/teams/werknemer/${eigenId}`, {
          headers: authHeader,
        });
        const teams: { id: number }[] = teamsRes.ok
          ? await teamsRes.json()
          : [];

        const ledenArrays = await Promise.all(
          teams.map((t) =>
            fetch(`/api/teams/${t.id}/leden`).then((r) =>
              r.ok ? r.json() : [],
            ),
          ),
        );

        const gezien = new Set<number>();
        const alleleden: Teamlid[] = [];

        for (const leden of ledenArrays) {
          for (const lid of leden as Teamlid[]) {
            if (!gezien.has(lid.id)) {
              gezien.add(lid.id);
              alleleden.push(lid);
            }
          }
        }

        // Ensure self appears first in the list
        const zelfIndex = alleleden.findIndex((l) => l.id === eigenId);
        if (zelfIndex > 0) {
          const [zelf] = alleleden.splice(zelfIndex, 1);
          alleleden.unshift(zelf);
        } else if (zelfIndex === -1) {
          alleleden.unshift({
            id: eigenId,
            naam: (sessionUser?.naam as string) ?? '',
            voornaam: (sessionUser?.voornaam as string) ?? '',
            rol,
          });
        }

        setTeamleden(alleleden);

        // Auto-select self and load own history
        const zelf = alleleden[0];
        if (zelf) {
          setGeselecteerdLid(zelf);
          setGeschiedenisLaden(true);
          fetch(`${BASE}/geschiedenis/werknemer/${zelf.id}`, {
            headers: authHeader,
          })
            .then((res) => res.json())
            .then(setGeschiedenis)
            .catch(() => setGeschiedenis([]))
            .finally(() => setGeschiedenisLaden(false));
        }
      } catch {
        // leave teamleden empty on error
      }
    }

    init();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [token, eigenId]);

  function formatDatum(d: string) {
    return new Date(d).toLocaleDateString('nl-BE', {
      day: 'numeric',
      month: 'short',
      year: 'numeric',
    });
  }

  function statusKleur(status: string | null) {
    switch (status) {
      case 'Goedgekeurd':
        return 'text-emerald-600 bg-emerald-50';
      case 'Afgewezen':
        return 'text-red-500 bg-red-50';
      case 'Geannuleerd':
        return 'text-zinc-400 bg-zinc-100';
      case 'In afwachting':
        return 'text-amber-600 bg-amber-50';
      default:
        return 'text-zinc-500 bg-zinc-100';
    }
  }

  const geschiedenisItems = (
    <>
      {geschiedenisLaden && <p className="text-sm text-zinc-400">Laden...</p>}
      {!geschiedenisLaden && geschiedenis.length === 0 && (
        <p className="text-sm text-zinc-400">Geen geschiedenis gevonden.</p>
      )}
      {geschiedenis.map((item) => (
        <div
          key={item.id}
          className={`flex items-center justify-between px-4 py-3 rounded-2xl border ${item.type === 'Ziekte' ? 'bg-red-50/50 border-red-100' : item.status === 'In afwachting' ? 'bg-amber-50/50 border-amber-100' : item.status === 'Afgewezen' || item.status === 'Geannuleerd' ? 'bg-zinc-50 border-zinc-200' : 'bg-emerald-50/50 border-emerald-100'}`}
        >
          <div className="flex flex-col gap-0.5">
            <span className="text-sm font-semibold text-zinc-800">
              {formatDatum(item.startDatum)}
              {item.startDatum !== item.eindDatum &&
                ` – ${formatDatum(item.eindDatum)}`}
            </span>
            {item.omschrijving && (
              <span className="text-xs text-zinc-500">{item.omschrijving}</span>
            )}
          </div>
          <div className="flex items-center gap-2">
            <span
              className={`text-xs font-bold px-2 py-1 rounded-full ${item.type === 'Ziekte' ? 'bg-red-100 text-red-500' : 'bg-zinc-100 text-zinc-600'}`}
            >
              {item.type}
            </span>
            {item.status && (
              <span
                className={`text-xs font-bold px-2 py-1 rounded-full ${statusKleur(item.status)}`}
              >
                {item.status}
              </span>
            )}
          </div>
        </div>
      ))}
    </>
  );

  // Employee view: full-width history panel, no team member selector
  if (isWerknemer) {
    return (
      <Container height="full">
        <div className="flex flex-col gap-3 p-1 h-full">
          {geselecteerdLid && (
            <span className="text-base font-bold text-zinc-900">
              {geselecteerdLid.voornaam} {geselecteerdLid.naam}
            </span>
          )}
          {geschiedenisItems}
        </div>
      </Container>
    );
  }

  // Manager / Supervisor / Admin view: two-column layout with team member selector
  return (
    <>
      <div className="flex flex-col sm:flex-row gap-4 min-h-full w-full">
        <div className="w-full sm:w-1/3 min-h-48 sm:h-full sm:min-h-0">
          <Container height="full">
            <div className="flex flex-col gap-2 p-1 h-full">
              <Label text={'Teamleden'} variant={'caption'} />
              {teamleden.length === 0 && (
                <div className="w-full h-full flex-1 flex items-center justify-center">
                  <Label text={'Geen teamleden'} variant={'emptystate'} />
                </div>
              )}
              {teamleden.map((lid) => (
                <button
                  key={lid.id}
                  onClick={() => laadGeschiedenis(lid)}
                  className={`w-full text-left px-3 py-2.5 rounded-2xl text-sm font-medium transition-all duration-200 ${geselecteerdLid?.id === lid.id ? 'bg-zinc-900 text-white' : 'hover:bg-gray-300/30 text-zinc-700'}`}
                >
                  {lid.voornaam} {lid.naam}
                  <span
                    className={`block text-xs mt-0.5 ${geselecteerdLid?.id === lid.id ? 'text-zinc-300' : 'text-zinc-400'}`}
                  >
                    {lid.rol}
                  </span>
                </button>
              ))}
            </div>
          </Container>
        </div>
        <div className="flex-1 min-w-0">
          <Container height="full">
            <div className="flex flex-col gap-3 p-1 h-full">
              {!geselecteerdLid && (
                <div className="h-full flex items-center justify-center py-20 text-zinc-300 text-sm">
                  <Label
                    text={'Selecteer een teamlid'}
                    variant={'emptystate'}
                  />
                </div>
              )}
              {geselecteerdLid && (
                <>
                  <span className="text-base font-bold text-zinc-900">
                    {geselecteerdLid.voornaam} {geselecteerdLid.naam}
                  </span>
                  {geschiedenisItems}
                </>
              )}
            </div>
          </Container>
        </div>
      </div>
    </>
  );
}
