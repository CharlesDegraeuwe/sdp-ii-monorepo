import type { Metadata } from 'next';
import AppContainer from '../../../../components/design system/AppContainer/AppContainer';
import OverzichtClient from './OverzichtClient';
import { PageContainer } from '@/components/design system/PageContainer';

export const metadata: Metadata = {
  title: 'Overzicht | Delaware Suite',
};

export default function Page() {
  return (
    <PageContainer>
      <AppContainer>
        <OverzichtClient />
      </AppContainer>
    </PageContainer>
  );
}
