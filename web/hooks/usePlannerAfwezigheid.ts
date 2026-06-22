'use client';

import { useQuery } from '@tanstack/react-query';
import { useSession } from 'next-auth/react';
import type { Afwezigheid } from '@/components/app/planner/types';

const BASE = process.env.NEXT_PUBLIC_API_URL ?? 'http://localhost:8080/api';

export function usePlannerAfwezigheid(
  userId: number | undefined,
  van: string,
  tot: string,
) {
  const { data: session } = useSession();
  const token = session?.accessToken;

  return useQuery<Afwezigheid[]>({
    queryKey: ['planner-afwezigheid', userId, van, tot],
    queryFn: async () => {
      const res = await fetch(
        `${BASE}/planning/team/${userId}?van=${van}&tot=${tot}`,
        { headers: { Authorization: `Bearer ${token}` } },
      );
      if (!res.ok) return [];
      return res.json() as Promise<Afwezigheid[]>;
    },
    enabled: !!userId && !!token,
    initialData: [],
  });
}
