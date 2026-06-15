import { useMemo } from 'react';
import { useTeamsStore } from '@/stores/teamStore';

export const useFilteredTeams = (search: string) => {
  const teams = useTeamsStore((s) => s.teams);
  const filterSiteId = useTeamsStore((s) => s.filterSiteId);

  return useMemo(() => {
    let list = Object.values(teams);
    if (filterSiteId) list = list.filter((t) => t.siteId === filterSiteId);
    if (!search) return list;
    const q = search.toLowerCase();
    return list.filter((t) => t.naam.toLowerCase().includes(q));
  }, [teams, filterSiteId, search]);
};
