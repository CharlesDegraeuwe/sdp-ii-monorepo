import { Container } from '@/components/design system/Container';
import type { Metadata } from 'next';
import AppContainer from '../../../../components/design system/AppContainer/AppContainer';

export const metadata: Metadata = {
  title: 'Overzicht | Delaware Suite',
};

export default function Page() {
  return (
    <AppContainer>
      <div className="w-full h-full grid grid-cols-4 grid-rows-4 gap-5">
        <Container
          label={'Snelle acties:'}
          className="col-start-1 col-end-3 row-start-1 row-end-2"
        />

        <Container
          label={'Geplande uren:'}
          className="col-start-1 col-end-3 row-start-2 row-end-5"
        />

        <Container
          label={'Kalender:'}
          className="col-start-3 col-end-4 row-start-1 row-end-3"
        />

        <Container
          label={'Notificaties:'}
          className="col-start-4 col-end-5 row-start-1 row-end-3"
        />

        <Container
          label={'Snelle statistieken:'}
          className="col-start-3 col-end-4 row-start-3 row-end-4"
        />

        <Container
          indent={true}
          className="col-start-4 col-end-5 row-start-3 row-end-4"
        />

        <Container className="col-start-3 col-end-5 row-start-4 row-end-5" />
      </div>
    </AppContainer>
  );
}
