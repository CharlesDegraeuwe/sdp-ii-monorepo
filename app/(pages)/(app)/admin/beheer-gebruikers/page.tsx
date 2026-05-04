import BreadcrumbInit from '@/components/app/structuur/breadcrumb/BreadCrumbInit';
import BeheerGebruikersTable from '@/components/app/admin/beheer-gebruikers/BeheerGebruikersTable';

export default function Page() {
  return (
    <>
      <BreadcrumbInit pages={['admin', 'beheer Gebruikers']} />
      <BeheerGebruikersTable />
    </>
  );
}
