'use client';

import { useSession } from 'next-auth/react';
import { useEffect, useMemo, useState } from 'react';
import { WerknemerUser } from '@/types/types';

const BASE = process.env.NEXT_PUBLIC_API_URL ?? 'http://localhost:8080/api';

export function useBeheerGebruikers() {
  const { data: session } = useSession();
  const token = session?.accessToken;
  const user = session?.user;

  const [werknemers, setWerknemers] = useState<WerknemerUser[]>([]);

  const authHeader = useMemo(
    () => ({ Authorization: `Bearer ${token}` }),
    [token],
  );

  useEffect(() => {
    if (!user?.id) return;
    async function laadData() {
      try {
        const werknemersRes = await fetch(`${BASE}/werknemers`, {
          headers: authHeader,
        });

        if (werknemersRes.ok) {
          setWerknemers(await werknemersRes.json());
        }
      } catch {
        // silent fail
      }
    }

    laadData();
  }, [user?.id, authHeader, user]);

  return werknemers;
}
