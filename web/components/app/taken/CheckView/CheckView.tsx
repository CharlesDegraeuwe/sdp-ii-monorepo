'use client';
import { TaskColumn } from './TaskColumn';
import { TaskDetailPanel } from './TaskDetailPanel';
import { FinishedTasksPanel } from './FinishedTasksPanel';
import { useTaakStore } from '@/stores/taakStore';

interface CheckViewProps {
  scope: 'teams' | 'users';
}

export const CheckView = ({ scope }: CheckViewProps) => {
  const selectedMemberId = useTaakStore((s) => s.selectedMemberId);
  const selectedTeamId = useTaakStore((s) => s.selectedTeamId);

  const targetId = scope === 'teams' ? selectedTeamId : selectedMemberId;

  return (
    <div className={'w-full flex flex-col gap-4  min-h-full pt-5 '}>
      <div className={'w-full min-h-3/4 grid grid-cols-2 gap-5'}>
        <TaskColumn targetId={targetId} scope={scope} />
        <div className={'flex flex-col gap-5 min-h-full'}>
          <TaskDetailPanel />
          <FinishedTasksPanel targetId={targetId} scope={scope} />
        </div>
      </div>
    </div>
  );
};
