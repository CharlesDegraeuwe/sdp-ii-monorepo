import { AppContainer } from '@/components/design-system/AppContainer';
import type { Metadata } from 'next';
import { PageContainer } from '@/components/design-system/PageContainer';
import { useBreadCrumbs } from '@/providers/BreadCrumbProvider';

export const metadata: Metadata = {
  title: 'Account | Delaware Suite',
};

export default function Page() {
  useBreadCrumbs().pages.push('account');
  return (
    <PageContainer className="h-full">
      <AppContainer></AppContainer>
    </PageContainer>
  );
}
