import { useQuery } from '@tanstack/react-query';
import { Team, TeamMember } from '@/stores/taakStore';

export const TEAMS_KEY = ['teams'] as const;

export const useTeams = () => {
  return useQuery({
    queryKey: TEAMS_KEY,
    queryFn: async (): Promise<Team[]> => {
      const [teamsRes] = await Promise.all([fetch('/api/teams')]);
      const teamsData = (await teamsRes.json()) as Record<string, unknown>[];

      const baseTeams = teamsData.map((t) => ({
        id: String(t.id),
        name: (t.naam as string) ?? '',
        plant: (t.siteNaam as string) ?? '',
        members: [] as TeamMember[],
      }));

      const ledenResults = await Promise.all(
        baseTeams.map(async (team): Promise<Team> => {
          try {
            const res = await fetch(`/api/teams/${team.id}/leden`);
            if (!res.ok) return team;
            const leden = (await res.json()) as Record<string, unknown>[];
            return {
              ...team,
              members: leden.map((l) => ({
                id: String(l.werknemerId),
                firstName: (l.voornaam as string) ?? '',
                lastName: (l.naam as string) ?? '',
                email: (l.email as string) ?? '',
              })),
            };
          } catch {
            return team;
          }
        }),
      );

      return ledenResults;
    },
  });
};
