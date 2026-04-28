import Link from 'next/link';
import {
  MdOutlineAddCircleOutline,
  MdOutlineCheckCircleOutline,
  MdOutlineCalendarMonth,
  MdOutlineWarningAmber,
} from 'react-icons/md';
import { Card, SectionTitle } from './Card';

const acties = [
  {
    label: 'Shift Toevoegen',
    href: '/planner',
    icon: <MdOutlineAddCircleOutline size={16} />,
    iconClass: 'text-zinc-600',
  },
  {
    label: 'Taak Toewijzen',
    href: '/taken',
    icon: <MdOutlineCheckCircleOutline size={16} />,
    iconClass: 'text-zinc-600',
  },
  {
    label: 'Meld Afwezigheid',
    href: '/afwezigheden',
    icon: <MdOutlineWarningAmber size={16} />,
    iconClass: 'text-red-500',
  },
  {
    label: 'Plan Vakantie',
    href: '/afwezigheden',
    icon: <MdOutlineCalendarMonth size={16} />,
    iconClass: 'text-zinc-600',
  },
];

export function SnelleActies() {
  return (
    <div className="flex flex-col gap-2">
      <SectionTitle>Snelle Acties</SectionTitle>
      <Card className="p-4">
        <div className="grid grid-cols-2 gap-2.5">
          {acties.map((actie) => (
            <Link
              key={actie.label}
              href={actie.href}
              className="flex items-center gap-2.5 px-4 py-3 rounded-2xl border border-gray-200/40 bg-white/40 hover:bg-white/70 hover:border-gray-300/60 transition-all duration-200 active:scale-[0.98]"
            >
              <span className={actie.iconClass}>{actie.icon}</span>
              <span className="text-sm font-semibold text-zinc-700">
                {actie.label}
              </span>
            </Link>
          ))}
        </div>
      </Card>
    </div>
  );
}
