import { HiOutlineUserGroup } from 'react-icons/hi2';
import { MdOutlineCopyAll } from 'react-icons/md';
import { LiaHashtagSolid } from 'react-icons/lia';
import { HiOutlineLocationMarker } from 'react-icons/hi';
import { FaRegTrashCan } from 'react-icons/fa6';
import { useDeleteTeam } from '@/hooks/useDeleteTeam';
import { useTeamsStore } from '@/stores/teamStore';
import { Team } from '@/types/types';
import { CopyableField } from '@/components/app/team/UserCheckView/components/CopyableField';

type Props = {
  team: Team;
  ledenAantal: number;
};

export const TeamHeader = ({ team, ledenAantal }: Props) => {
  const deleteTeam = useDeleteTeam();
  const site = useTeamsStore((s) =>
    Object.values(s.sites ?? {}).find((x) => x.id === team.siteId),
  );

  return (
    <div className="flex flex-col min-w-0 w-full">
      <div className="flex flex-row items-center justify-between">
        <div className="flex flex-row gap-2 items-center">
          <div className="w-10 h-10 rounded-full bg-zinc-100 flex items-center justify-center">
            <HiOutlineUserGroup className="w-5 h-5 text-zinc-600" />
          </div>
          <div className="flex flex-col">
            <span
              onClick={() => navigator.clipboard.writeText(team.naam)}
              className="font-bold group flex flex-row items-center cursor-pointer text-lg leading-tight truncate"
            >
              {team.naam}
              <MdOutlineCopyAll className="group-hover:opacity-100 ml-2 group-active:scale-90 text-base opacity-0 transition-all duration-300" />
            </span>
            <span className="text-xs text-zinc-400">
              {ledenAantal} {ledenAantal === 1 ? 'lid' : 'leden'}
            </span>
          </div>
        </div>

        <button
          onClick={() => deleteTeam(team.id)}
          className="p-2 rounded-full text-zinc-400 hover:text-rose-500 hover:bg-rose-50 transition cursor-pointer"
        >
          <FaRegTrashCan className="w-4 h-4" />
        </button>
      </div>

      <div className="flex flex-col gap-2 pt-3 text-sm text-zinc-600 bg-zinc-50 shadow-sm rounded-3xl my-3 p-3">
        <span className="font-bold text-zinc-800">Details:</span>

        <CopyableField
          icon={<LiaHashtagSolid className="w-3 h-3" />}
          value={team.id.toString()}
        />
        <CopyableField
          icon={<HiOutlineLocationMarker className="w-3 h-3" />}
          value={site?.naam || 'onbekend'}
        />
      </div>
    </div>
  );
};
