import { ReactNode } from 'react';
import { MdOutlineCopyAll } from 'react-icons/md';

type Props = {
  icon: ReactNode;
  value: string;
  copyValue?: string;
};

export const CopyableField = ({ icon, value, copyValue }: Props) => (
  <div
    onClick={() => navigator.clipboard.writeText(copyValue ?? value)}
    className="group flex gap-1 cursor-pointer flex-row items-center"
  >
    {icon}
    <span>{value}</span>
    <MdOutlineCopyAll className="group-hover:opacity-100 group-active:scale-95 opacity-0 transition-all duration-300" />
  </div>
);
