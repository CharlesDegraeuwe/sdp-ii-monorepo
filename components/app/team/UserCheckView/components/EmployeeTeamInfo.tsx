import { HiOutlineUserGroup } from 'react-icons/hi2';
import { useTeamsStore, Werknemer } from '@/stores/teamStore';

type Props = {
  employee: Werknemer;
};

export const EmployeeTeamInfo = ({ employee }: Props) => {
  const team = useTeamsStore((s) =>
    employee.siteId
      ? Object.values(s.teams).find((t) => t.siteId === employee.siteId)
      : null,
  );

  if (!team) return null;

  return (
    <div className="flex justify-between items-center">
      <span className="flex items-center gap-2 text-zinc-600">
        <HiOutlineUserGroup className="w-4 h-4" />
        {team.naam}
        {employee.role !== 'Werknemer' && (
          <span className="text-zinc-400">— {employee.role}</span>
        )}
      </span>
      <button className="text-xs underline text-zinc-500 hover:text-zinc-800">
        zie planning
      </button>
    </div>
  );
};
