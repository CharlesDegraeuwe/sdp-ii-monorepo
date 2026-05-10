'use client';
import { Container } from '@/components/design-system/Container';
import { useTeamsStore } from '@/stores/teamStore';
import { TeamList } from './TeamList';
import { TeamDetails } from './TeamDetails';

const TeamsOverview = () => {
  const selectedTeamId = useTeamsStore((s) => s.selectedTeamId);

  return (
    <div className="relative w-full h-3/4 flex flex-col gap-3 pt-5">
      <div className="w-full grid grid-cols-2 gap-5 min-h-full">
        <Container label="Teams" height="full" padding="0">
          <TeamList />
        </Container>

        <Container label="Details" height="full">
          <TeamDetails teamId={selectedTeamId} />
        </Container>
      </div>
    </div>
  );
};

TeamsOverview.displayName = 'TeamsOverview';
export default TeamsOverview;
