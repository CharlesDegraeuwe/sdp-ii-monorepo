'use client';

import { useSession } from 'next-auth/react';
import { useEffect, useMemo, useState } from 'react';
import type { Afwezigheid } from '../components/app/planner/types';
import type { Notificatie } from '../components/app/overzicht/types';

const BASE = process.env.NEXT_PUBLIC_API_URL ?? 'http://localhost:8080/api';

export function useOverzichtData() {
  const { data: session } = useSession();
  const token = session?.accessToken;
  const user = session?.user;

  const [afwezigheden, setAfwezigheden] = useState<Afwezigheid[]>([]);
  const [notificaties, setNotificaties] = useState<Notificatie[]>([]);

  const authHeader = useMemo(
    () => ({ Authorization: `Bearer ${token}` }),
    [token],
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
        const [planRes, notifRes] = await Promise.all([
          fetch(`${BASE}/planning/team/${user!.id}?van=${van}&tot=${tot}`, {
            headers: authHeader,
          }),
          fetch(`${BASE}/notificaties/${user!.id}`, { headers: authHeader }),
        ]);
        if (planRes.ok) setAfwezigheden(await planRes.json());
        if (notifRes.ok) setNotificaties(await notifRes.json());
      } catch {
        // silent fail
      }
    }

    laadData();
  }, [user?.id, authHeader, user]);

  return { afwezigheden, notificaties };
}
