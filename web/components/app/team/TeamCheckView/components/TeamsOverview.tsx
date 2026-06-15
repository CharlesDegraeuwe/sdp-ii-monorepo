'use client';
import { Container } from '@/components/design-system/Container';
import { TeamList } from './TeamList';
import { TeamDetails } from './TeamDetails';
import { EmployeeDetails } from '@/components/app/team/UserCheckView/components/EmployeeDetails';

type Props = {
  selectedTeamId?: number | null;
  selectedWerknemerId?: number | null;
};

const TeamsOverview = ({
  selectedTeamId = null,
  selectedWerknemerId = null,
}: Props) => {
  return (
    <div className="relative w-full h-3/4 flex flex-col gap-3 pt-5">
      <div className="w-full grid grid-cols-2 gap-5 min-h-full">
        <Container label="Teams" height="full" padding="0">
          <TeamList selectedTeamId={selectedTeamId} />
        </Container>

        <Container label="Details" height="full">
          {selectedWerknemerId ? (
            <EmployeeDetails werknemerId={selectedWerknemerId} />
          ) : (
            <TeamDetails teamId={selectedTeamId} />
          )}
        </Container>
      </div>
    </div>
  );
};

TeamsOverview.displayName = 'TeamsOverview';
export default TeamsOverview;
