import type { Metadata } from 'next';
import { AppContainer } from '@/components/design system/AppContainer';

export const metadata: Metadata = {
  title: 'Taken | Delaware Suite',
};

export default function Page() {
  return <AppContainer></AppContainer>;
}
