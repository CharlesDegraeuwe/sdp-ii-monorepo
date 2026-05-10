import Avatar from '@/components/design-system/Avatar/Avatar';
import { FaRegTrashCan } from 'react-icons/fa6';
import { useTeamMembership } from '@/hooks/useTeamMemberShip';
import { TeamLid } from '@/stores/teamStore';

type Props = {
  lid: TeamLid;
  teamId: number;
};

export const TeamMemberCard = ({ lid, teamId }: Props) => {
  const { promoot, verwijder } = useTeamMembership();
  const fullName = `${lid.voornaam} ${lid.naam}`;

  return (
    <div className="p-0.5">
      <div className="group px-4 py-2 shadow-sm rounded-full text-sm flex flex-row gap-2 items-center justify-between min-h-13 transition bg-zinc-100 hover:bg-zinc-200">
        <div className="flex flex-row items-center gap-2 min-w-0">
          <Avatar id={lid.werknemerId} displayName={fullName} />
          <span className="font-medium truncate">{fullName}</span>
          {lid.isSupervisor && (
            <span className="text-xs text-amber-600 bg-amber-100 px-2 py-0.5 rounded-full whitespace-nowrap">
              supervisor
            </span>
          )}
        </div>

        <div className="flex flex-row items-center gap-3 opacity-0 group-hover:opacity-100 transition-opacity duration-300">
          <button
            onClick={() => promoot(teamId, lid.werknemerId)}
            className="text-xs underline text-zinc-600 hover:text-zinc-900 cursor-pointer"
          >
            {lid.isSupervisor ? 'demoot' : 'promoot'}
          </button>
          <button
            onClick={() => verwijder(teamId, lid.werknemerId)}
            className="p-1.5 rounded-full text-zinc-500 hover:text-rose-500 hover:bg-white/60 transition cursor-pointer"
          >
            <FaRegTrashCan className="w-3.5 h-3.5" />
          </button>
        </div>
      </div>
    </div>
  );
};
