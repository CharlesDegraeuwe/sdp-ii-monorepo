import type { Metadata } from 'next';
import AppContainer from '../../../../components/design system/AppContainer/AppContainer';
import OverzichtClient from './OverzichtClient';

export const metadata: Metadata = {
  title: 'Overzicht | Delaware Suite',
};

export default function Page() {
  return (
    <AppContainer>
      <OverzichtClient />
    </AppContainer>
  );
}
