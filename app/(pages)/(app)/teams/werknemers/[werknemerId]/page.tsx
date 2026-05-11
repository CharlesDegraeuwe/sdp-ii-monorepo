import type { Metadata } from 'next';
import { PageContainer } from '@/components/design-system/PageContainer';
import BreadcrumbInit from '@/components/app/structuur/breadcrumb/BreadCrumbInit';
import { AppContainer } from '@/components/design-system/AppContainer';
import TeamClient from '@/components/app/team/TeamClient';

export const metadata: Metadata = {
  title: 'Werknemers | Delaware Suite',
};

type Props = {
  params: Promise<{ werknemerId: string }>;
};

export default async function Page({ params }: Props) {
  const { werknemerId } = await params;
  const werknmerIdNum = Number(werknemerId);

  return (
    <PageContainer className="h-full">
      <BreadcrumbInit pages={['teams', 'werknemers']} />
      <AppContainer>
        <TeamClient selectedWerknemerId={werknmerIdNum} defaultScope="users" />
      </AppContainer>
    </PageContainer>
  );
}
