'use client';
import { Label } from '@/components/design-system/Label';
import { useTaakStore } from '@/stores/taakStore';
import { TaskHeader } from './TaskHeader';
import { useTaken } from '@/hooks/useTaken';

export const FinishedTaskDetails = () => {
  const selectedTaskId = useTaakStore((s) => s.selectedTaskId);
  const { data: tasks = [] } = useTaken();

  const task = selectedTaskId
    ? (tasks.find((t) => t.id === selectedTaskId) ?? null)
    : null;

  if (!task || !task.finished) {
    return (
      <div className="w-full h-full flex items-center justify-center pb-12 px-4">
        <Label text="Selecteer een afgewerkte taak" variant="emptystate" />
      </div>
    );
  }

  return (
    <div className="flex flex-col gap-5 h-full p-3">
      <TaskHeader task={task} />

      <hr className="opacity-10" />

      <div className="flex flex-col gap-3 text-sm flex-1 min-h-0">
        <span className="text-xs text-zinc-400">Specificaties</span>
        <div className="px-4 py-3 bg-zinc-50 rounded-3xl text-sm min-h-24">
          {task.specifications || 'Geen specificaties'}
        </div>
      </div>

      <div className="text-xs text-zinc-400 mt-auto">
        Afgewerkt op {new Date(task.finishedAt ?? '').toLocaleDateString()}
      </div>
    </div>
  );
};
