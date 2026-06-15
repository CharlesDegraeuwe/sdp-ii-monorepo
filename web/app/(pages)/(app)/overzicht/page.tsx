import { Container } from '@/components/design system/Container';
import type { Metadata } from 'next';
import AppContainer from '../../../../components/design system/AppContainer/AppContainer';
import { PageContainer } from '@/components/design system/PageContainer';
import BreadcrumbInit from '@/components/app/structuur/breadcrumb/BreadCrumbInit';

export const metadata: Metadata = {
  title: 'Overzicht | Delaware Suite',
};

export default function Page() {
  return (
    <PageContainer className="h-full">
      <AppContainer>
        <BreadcrumbInit pages={['overzicht']} />
        <div className="w-full h-auto lg:h-full grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 lg:grid-rows-4 gap-5">
          <Container
            label={'Snelle acties:'}
            className="min-h-[160px] sm:col-span-2 lg:min-h-0 lg:col-start-1 lg:col-end-3 lg:row-start-1 lg:row-end-2"
          />

          <Container
            label={'Geplande uren:'}
            className="min-h-[200px] sm:col-span-2 lg:min-h-0 lg:col-start-1 lg:col-end-3 lg:row-start-2 lg:row-end-5"
          />

          <Container
            label={'Kalender:'}
            className="min-h-[160px] lg:min-h-0 lg:col-start-3 lg:col-end-4 lg:row-start-1 lg:row-end-3"
          />

          <Container
            label={'Notificaties:'}
            className="min-h-[160px] lg:min-h-0 lg:col-start-4 lg:col-end-5 lg:row-start-1 lg:row-end-3"
          />

          <Container
            label={'Snelle statistieken:'}
            className="min-h-[160px] lg:min-h-0 lg:col-start-3 lg:col-end-4 lg:row-start-3 lg:row-end-4"
          />

          <Container
            indent={true}
            className="min-h-[160px] lg:min-h-0 lg:col-start-4 lg:col-end-5 lg:row-start-3 lg:row-end-4"
          />

          <Container className="min-h-[160px] sm:col-span-2 lg:min-h-0 lg:col-start-3 lg:col-end-5 lg:row-start-4 lg:row-end-5" />
        </div>
      </AppContainer>
    </PageContainer>
  );
}
