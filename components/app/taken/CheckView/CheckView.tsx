'use client';
import { TaskColumn } from './TaskColumn';
import { TaskDetailPanel } from './TaskDetailPanel';
import { FinishedTasksPanel } from './FinishedTasksPanel';
import { TeamFilterBar } from './TeamFilterBar';
import { MemberFilterBar } from './MemberFilterBar';
import { useTaakStore } from '@/stores/taakStore';
import { Container } from '@/components/design-system/Container';

interface CheckViewProps {
  scope: 'teams' | 'users';
}

export const CheckView = ({ scope }: CheckViewProps) => {
  const selectedMemberId = useTaakStore((s) => s.selectedMemberId);
  const selectedTeamId = useTaakStore((s) => s.selectedTeamId);

  const targetId = scope === 'teams' ? selectedTeamId : selectedMemberId;

  return (
    <div className="relative w-full h-3/4 flex flex-col gap-3 pt-5">
      {scope === 'teams' ? <TeamFilterBar /> : <MemberFilterBar />}

      <div className="w-full grid grid-cols-2 gap-5 min-h-full">
        <Container label="Taken" height="full" padding="0">
          <TaskColumn targetId={targetId} scope={scope} />
        </Container>

        <Container label="Details" height="full">
          <TaskDetailPanel />
          <FinishedTasksPanel targetId={targetId} scope={scope} />
        </Container>
      </div>
    </div>
  );
};
