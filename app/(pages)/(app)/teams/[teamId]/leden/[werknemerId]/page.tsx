import type { Metadata } from 'next';
import { PageContainer } from '@/components/design-system/PageContainer';
import BreadcrumbInit from '@/components/app/structuur/breadcrumb/BreadCrumbInit';
import { AppContainer } from '@/components/design-system/AppContainer';
import TeamClient from '@/components/app/team/TeamClient';

export const metadata: Metadata = {
  title: 'Teamlid | Delaware Suite',
};

type Props = {
  params: Promise<{ teamId: string; werknemerId: string }>;
};

export default async function Page({ params }: Props) {
  const { teamId, werknemerId } = await params;
  const teamIdNum = Number(teamId);
  const werknemerIdNum = Number(werknemerId);

  return (
    <PageContainer className="h-full">
      <BreadcrumbInit pages={['teams']} />
      <AppContainer>
        <TeamClient
          selectedTeamId={teamIdNum}
          selectedWerknemerId={werknemerIdNum}
        />
      </AppContainer>
    </PageContainer>
  );
}
