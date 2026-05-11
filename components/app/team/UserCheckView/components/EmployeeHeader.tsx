import Avatar from '@/components/design-system/Avatar/Avatar';
import { MdOutlineCopyAll } from 'react-icons/md';
import { StatusBadge } from './StatusBadge';
import { Werknemer } from '@/stores/teamStore';

type Props = {
  employee: Werknemer;
};

export const EmployeeHeader = ({ employee }: Props) => {
  const fullName = `${employee.voornaam} ${employee.naam}`;

  return (
    <div className="flex flex-row items-center justify-between">
      <div className="flex flex-row gap-2 items-center">
        <Avatar id={employee.id} displayName={fullName} />
        <div className="flex flex-col">
          <span
            onClick={() => navigator.clipboard.writeText(fullName)}
            className="font-bold group flex flex-row items-center cursor-pointer text-lg leading-tight truncate"
          >
            {fullName}
            <MdOutlineCopyAll className="group-hover:opacity-100 ml-2 group-active:scale-90 text-base opacity-0 transition-all duration-300" />
          </span>
          <span className="text-xs text-zinc-400">{employee.role}</span>
        </div>
      </div>
      <StatusBadge status={employee.status} />
    </div>
  );
};
