import { useRouter } from 'next/navigation';
import { HiOutlineUserGroup } from 'react-icons/hi2';
import { useTeamsStore, Werknemer } from '@/stores/teamStore';
import { useMemo } from 'react';

type Props = {
  employee: Werknemer;
};

export const EmployeeTeamInfo = ({ employee }: Props) => {
  const router = useRouter();
  const teams = useTeamsStore((s) => s.teams);
  const teamLeden = useTeamsStore((s) => s.teamLeden);

  const team = useMemo(() => {
    for (const [teamId, leden] of Object.entries(teamLeden)) {
      if (leden.some((l) => l.werknemerId === employee.id)) {
        return teams[Number(teamId)] ?? null;
      }
    }
    return null;
  }, [teams, teamLeden, employee.id]);

  if (!team) return null;

  return (
    <div className="flex justify-between items-center">
      <span
        onClick={() => router.push(`/teams/${team.id}`)}
        className="flex items-center gap-2 text-zinc-600 cursor-pointer hover:text-zinc-900 transition"
      >
        <HiOutlineUserGroup className="w-4 h-4" />
        {team.naam}
        {employee.role !== 'Werknemer' && (
          <span className="text-zinc-400">— {employee.role}</span>
        )}
      </span>
    </div>
  );
};
