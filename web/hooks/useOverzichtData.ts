'use client';

import { useSession } from 'next-auth/react';
import { useCallback, useEffect, useMemo, useState } from 'react';
import type { Afwezigheid } from '../components/app/planner/types';
import type { Notificatie } from '../components/app/overzicht/types';
import type { Task } from '../stores/taakStore';
import { mapBackendTask } from '../lib/taakMapper';

const BASE = process.env.NEXT_PUBLIC_API_URL ?? 'http://localhost:8080/api';

export function useOverzichtData() {
  const { data: session } = useSession();
  const token = session?.accessToken;
  const user = session?.user;

  const [afwezigheden, setAfwezigheden] = useState<Afwezigheid[]>([]);
  const [notificaties, setNotificaties] = useState<Notificatie[]>([]);
  const [taken, setTaken] = useState<Task[]>([]);

  const authHeader = useMemo(
    () => ({
      Authorization: `Bearer ${token}`,
      'Content-Type': 'application/json',
    }),
    [token],
  );

  const laadNotificaties = useCallback(
    async (userId: string) => {
      const res = await fetch(`${BASE}/notificaties/${userId}`, {
        headers: authHeader,
      });
      if (res.ok) setNotificaties(await res.json());
    },
    [authHeader],
  );

  useEffect(() => {
    if (!user?.id) return;

    const vandaag = new Date();
    const van = new Date(vandaag.getFullYear(), vandaag.getMonth() - 1, 1)
      .toISOString()
      .split('T')[0];
    const tot = new Date(vandaag.getFullYear(), vandaag.getMonth() + 2, 0)
      .toISOString()
      .split('T')[0];

    async function laadData() {
      try {
        const [planRes, takenRes] = await Promise.all([
          fetch(`${BASE}/planning/team/${user!.id}?van=${van}&tot=${tot}`, {
            headers: authHeader,
          }),
          fetch(`${BASE}/taken/werknemer/${user!.id}`, { headers: authHeader }),
        ]);

        if (planRes.ok) setAfwezigheden(await planRes.json());

        if (takenRes.ok) {
          const data: Record<string, unknown>[] = await takenRes.json();
          setTaken(data.map(mapBackendTask).filter((t) => !t.finished));
        }

        await laadNotificaties(user!.id);
      } catch {
        // Verbindingsfout — pagina toont lege staat
      }
    }

    laadData();
  }, [user?.id, authHeader, user, laadNotificaties]);

  function refreshNotificaties() {
    if (!user?.id) return;
    laadNotificaties(user.id).catch(() => {});
  }

  return { afwezigheden, notificaties, taken, refreshNotificaties };
}
