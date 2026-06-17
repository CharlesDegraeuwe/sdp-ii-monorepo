'use client';
import { Label } from '@/components/design-system/Label';
import { Button } from '@/components/design-system/Button';
import { useTaakStore } from '@/stores/taakStore';
import { TaskHeader } from './TaskHeader';
import { useToast } from '@/providers/ToastProvider';
import { useTaken } from '@/hooks/useTaken';
import { useMarkeerAfgewerkt } from '@/hooks/useMarkeerAfgewerkt';

export const TaskDetails = () => {
  const selectedTaskId = useTaakStore((s) => s.selectedTaskId);
  const { data: tasks = [] } = useTaken();
  const { mutateAsync: markeerAfgewerkt, isPending } = useMarkeerAfgewerkt();
  const toast = useToast();

  const task = selectedTaskId
    ? (tasks.find((t) => t.id === selectedTaskId) ?? null)
    : null;

  if (!task) {
    return (
      <div className="w-full h-full flex items-center justify-center pb-12 px-4">
        <Label text="Selecteer een taak" variant="emptystate" />
      </div>
    );
  }

  const handleFinish = async () => {
    try {
      await markeerAfgewerkt(task.id);
      toast.success('Taak afgewerkt');
    } catch {
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
          label={isPending ? 'Bezig...' : 'Markeer als afgewerkt'}
          variant="primary"
          onClick={handleFinish}
          disabled={isPending}
        />
      </div>
    </div>
  );
};
