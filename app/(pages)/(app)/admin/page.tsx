import { Container } from '@/components/design-system/Container';
import {
  IoAddCircleOutline,
  IoAddCircleSharp,
  IoServerOutline,
} from 'react-icons/io5';
import Link from '@/components/design-system/Link/Link';
import BreadcrumbInit from '@/components/app/structuur/breadcrumb/BreadCrumbInit';

const links = [
  {
    href: '/admin/werknemer-aanmaken',
    label: 'Creëer werknemer',
    icon: <IoAddCircleOutline />,
  },
  {
    href: '/admin/manager-aanmaken',
    label: 'Creëer manager',
    icon: <IoAddCircleSharp />,
  },
  {
    href: '/admin/beheer-gebruikers',
    label: 'Beheer werknemers',
    icon: <IoServerOutline />,
  },
];
export default function Page() {
  return (
    <div className="w-full sm:w-3/4 md:w-1/2 h-full flex items-center justify-center">
      <BreadcrumbInit pages={['admin']} />
      <Container label={'Back Office'} width={'1/2'} height={'fit'}>
        <span></span>
        <div className="flex flex-col gap-5 w-full">
          {links.map((link, index) => (
            <Link
              href={link.href}
              key={index}
              label={link.label}
              icon={link.icon}
            />
          ))}
        </div>
      </Container>
    </div>
  );
}
