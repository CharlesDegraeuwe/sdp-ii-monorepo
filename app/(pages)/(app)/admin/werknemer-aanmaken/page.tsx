import BreadcrumbInit from '@/components/overig/structuur/breadcrumb/BreadCrumbInit';
import CreeerWerknemerForm from '@/components/app/admin/creeer-werknemer/CreeerWerknemerForm';

export default function Page() {
  return (
    <>
      <BreadcrumbInit pages={['admin', 'creëer Werknemer']} />
      <CreeerWerknemerForm />
    </>
  );
}
