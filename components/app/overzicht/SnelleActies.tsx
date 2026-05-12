import {
  MdOutlineAddCircleOutline,
  MdOutlineCheckCircleOutline,
  MdOutlineCalendarMonth,
  MdOutlineWarningAmber,
} from 'react-icons/md';
import Link from '@/components/design-system/Link/Link';
import { Container } from '@/components/design-system/Container';

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
    <Container
      height={'fit'}
      label={'Snelle acties'}
      className="flex items-center justify-center"
    >
      <div className="grid grid-cols-2 gap-2.5">
        {acties.map((actie) => (
          <Link
            href={actie.href}
            key={actie.label}
            label={actie.label}
            icon={actie.icon}
            rounded={'2xl'}
          />
        ))}
      </div>
    </Container>
  );
}
