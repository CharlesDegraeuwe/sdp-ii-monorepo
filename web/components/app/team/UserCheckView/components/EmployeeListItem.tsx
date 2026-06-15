import Avatar from '@/components/design-system/Avatar/Avatar';
import { Werknemer } from '@/stores/teamStore';

type Props = {
  werknemer: Werknemer;
  active: boolean;
  onClick: () => void;
};

export const EmployeeListItem = ({ werknemer, active, onClick }: Props) => (
  <div onClick={onClick} className="p-0.5">
    <div
      className={`px-4 py-2 shadow-sm rounded-full text-sm flex flex-row gap-2 items-center min-h-13 cursor-pointer text-left transition ${
        active ? 'bg-white ring ring-zinc-50' : 'bg-zinc-100 hover:bg-zinc-200'
      }`}
    >
      <Avatar
        id={werknemer.id}
        displayName={`${werknemer.voornaam} ${werknemer.naam}`}
      />
      {werknemer.voornaam} {werknemer.naam}
    </div>
  </div>
);
