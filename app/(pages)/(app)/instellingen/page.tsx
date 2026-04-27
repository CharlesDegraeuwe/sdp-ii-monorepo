import type { Metadata } from 'next';
import { AppContainer } from '@/components/design system/AppContainer';
import SettingsClient from '@/app/(pages)/(app)/instellingen/components/SettingsClient';

export const metadata: Metadata = {
  title: 'Instellingen | Delaware Suite',
};

export default function Page() {
  return (
    <AppContainer>
      <SettingsClient />
    </AppContainer>
  );
}
