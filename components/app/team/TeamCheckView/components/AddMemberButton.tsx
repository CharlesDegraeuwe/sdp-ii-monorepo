import { Button } from '@/components/design-system/Button';
import { IoIosAdd } from 'react-icons/io';

type Props = {
  teamId: number;
};

export const AddMemberButton = ({ teamId }: Props) => (
  <Button
    onClick={() => {
      // TODO: open add member modal voor teamId
      console.log('add member to team', teamId);
    }}
    className="flex flex-row items-center gap-1 text-xs text-zinc-500 hover:text-zinc-800 hover:bg-zinc-200/40 transition px-3 py-2 rounded-full"
    textSize="sm"
    iconLeft={<IoIosAdd className="w-5 h-5" />}
    label="Lid toevoegen"
    variant="ghost"
  />
);
