import BreadcrumbInit from '@/components/app/structuur/breadcrumb/BreadCrumbInit';
import CreeerManagerForm from '@/components/app/admin/creeer-manager/CreeerManagerForm';
export default function Page() {
  return (
    <>
      <BreadcrumbInit pages={['admin', 'creëer Manager']} />
      <CreeerManagerForm />
    </>
  );
}
