'use client';
import { Label } from '@/components/design-system/Label';
import { Button } from '@/components/design-system/Button';
import { useTaakStore } from '@/stores/taakStore';
import { TaskHeader } from './TaskHeader';
import { useToast } from '@/providers/ToastProvider';

export const TaskDetails = () => {
  const selectedTaskId = useTaakStore((s) => s.selectedTaskId);
  const task = useTaakStore((s) =>
    selectedTaskId ? s.tasks[selectedTaskId] : null,
  );
  const updateTask = useTaakStore((s) => s.updateTask);
  const toast = useToast();

  if (!task) {
    return (
      <div className="w-full h-full flex items-center justify-center pb-12 px-4">
        <Label text="Selecteer een taak" variant="emptystate" />
      </div>
    );
  }

  const handleFinish = async () => {
    updateTask(task.id, {
      finished: true,
      finishedAt: new Date().toISOString(),
    });
    try {
      await fetch(`/api/taken/${task.id}/afgewerkt`, { method: 'PUT' });
      toast.success('Taak afgewerkt');
    } catch {
      updateTask(task.id, { finished: false, finishedAt: undefined });
      toast.error('Kon taak niet als afgewerkt markeren');
    }
  };

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

      <div className="mt-auto">
        <Button
          type="button"
          label="Markeer als afgewerkt"
          variant="primary"
          onClick={handleFinish}
        />
      </div>
    </div>
  );
};
