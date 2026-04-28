import { AppContainer } from '@/components/design system/AppContainer';
import { PageContainer } from '@/components/design system/PageContainer';
import BreadcrumbInit from '@/components/app/structuur/breadcrumb/BreadCrumbInit';

export default function Page() {
  return (
    <PageContainer className="h-full">
      <BreadcrumbInit pages={['admin']} />
      <AppContainer></AppContainer>
    </PageContainer>
  );
}
