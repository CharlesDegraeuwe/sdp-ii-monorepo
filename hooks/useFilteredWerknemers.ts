import { useMemo } from 'react';
import { useTeamsStore } from '@/stores/teamStore';

export const useFilteredWerknemers = (search: string) => {
  const werknemers = useTeamsStore((s) => s.werknemers);

  return useMemo(() => {
    const list = Object.values(werknemers);
    if (!search) return list;
    const q = search.toLowerCase();
    return list.filter(
      (e) =>
        e.voornaam.toLowerCase().includes(q) ||
        e.naam.toLowerCase().includes(q) ||
        e.email.toLowerCase().includes(q),
    );
  }, [werknemers, search]);
};
