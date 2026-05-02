import type { Metadata } from 'next';
import { PageContainer } from '@/components/design system/PageContainer';
import BreadcrumbInit from '@/components/app/structuur/breadcrumb/BreadCrumbInit';

export const metadata: Metadata = {
  title: 'Teams | Delaware Suite',
};

export default function Page() {
  return (
    <PageContainer className="h-full">
      <BreadcrumbInit pages={['teams']} />
    </PageContainer>
  );
}
