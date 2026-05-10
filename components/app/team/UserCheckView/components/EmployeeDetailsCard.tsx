import { LiaHashtagSolid } from 'react-icons/lia';
import { HiOutlineLocationMarker } from 'react-icons/hi';
import { MdAlternateEmail } from 'react-icons/md';
import { CopyableField } from './CopyableField';
import { Werknemer } from '@/stores/teamStore';

type Props = {
  employee: Werknemer;
};

export const EmployeeDetailsCard = ({ employee }: Props) => (
  <div className="flex flex-col gap-2 pt-3 text-sm text-zinc-600 bg-zinc-50 shadow-sm rounded-3xl my-3 p-3">
    <span className="font-bold text-zinc-800">Details:</span>

    <CopyableField
      icon={<LiaHashtagSolid className="w-3 h-3" />}
      value={employee.id.toString()}
    />
    <CopyableField
      icon={<HiOutlineLocationMarker className="w-3 h-3" />}
      value={employee.siteNaam || 'onbekend'}
    />
    <CopyableField
      icon={<MdAlternateEmail className="w-3 h-3" />}
      value={employee.email}
    />

    <span className="text-sm text-zinc-700">{employee.telefoon}</span>
  </div>
);
