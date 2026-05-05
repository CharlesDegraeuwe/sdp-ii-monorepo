'use client';

import { useState, useEffect } from 'react';
import { useSession } from 'next-auth/react';
import type { Afwezigheid, View } from './types';
import { getPeriodBounds } from './utils';

export function usePlanning(view: View, currentDate: Date) {
  const { status } = useSession();
  const [afwezigheden, setAfwezigheden] = useState<Afwezigheid[]>([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    if (status !== 'authenticated') return;

    const { van, tot } = getPeriodBounds(view, currentDate);
    let cancelled = false;

    fetch(`/api/planning?van=${van}&tot=${tot}`)
      .then(async (res) => {
        if (!res.ok) throw new Error(`server_error_${res.status}`);
        return res.json();
      })
      .then((data) => {
        if (!cancelled) setAfwezigheden(data);
      })
      .catch(() => {
        if (!cancelled) setAfwezigheden([]);
      })
      .finally(() => {
        if (!cancelled) setLoading(false);
      });

    return () => {
      cancelled = true;
    };
  }, [status, view, currentDate]);

  return { afwezigheden, loading };
}
