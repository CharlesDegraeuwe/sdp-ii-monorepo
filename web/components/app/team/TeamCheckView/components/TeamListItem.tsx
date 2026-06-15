import { HiOutlineUserGroup } from 'react-icons/hi2';
import { Team } from '@/types/types';

type Props = {
  team: Team;
  active: boolean;
  onClick: () => void;
};

export const TeamListItem = ({ team, active, onClick }: Props) => (
  <div onClick={onClick} className="p-0.5">
    <div
      className={`px-4 py-2 shadow-sm rounded-full text-sm flex flex-row gap-2 items-center min-h-13 cursor-pointer text-left transition ${
        active ? 'bg-white ring ring-zinc-50' : 'bg-zinc-100 hover:bg-zinc-200'
      }`}
    >
      <div className="w-8 h-8 rounded-full bg-zinc-200 flex items-center justify-center">
        <HiOutlineUserGroup className="w-4 h-4 text-zinc-600" />
      </div>
      {team.naam}
    </div>
  </div>
);
