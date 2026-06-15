import type { Metadata } from 'next';
import { PageContainer } from '@/components/design-system/PageContainer';
import { AppContainer } from '@/components/design-system/AppContainer';
import TeamClient from '@/components/app/team/TeamClient';
import BreadcrumbInit from '@/components/overig/structuur/breadcrumb/BreadCrumbInit';

export const metadata: Metadata = {
  title: 'Teams | Delaware Suite',
};

type Props = {
  params: Promise<{ teamId: string }>;
};

export default async function Page({ params }: Props) {
  const { teamId } = await params;
  const teamIdNum = Number(teamId);

  return (
    <PageContainer className="h-full">
      <BreadcrumbInit pages={['teams']} />
      <AppContainer>
        <TeamClient selectedTeamId={teamIdNum} />
      </AppContainer>
    </PageContainer>
  );
}
