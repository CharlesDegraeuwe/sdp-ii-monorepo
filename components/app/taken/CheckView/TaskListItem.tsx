'use client';
import { Task, useTaakStore } from '@/stores/taakStore';
import { FaRegTrashCan } from 'react-icons/fa6';

const formatDue = (iso: string) => {
  const d = new Date(iso);
  const hh = d.getHours().toString().padStart(2, '0');
  const mm = d.getMinutes().toString().padStart(2, '0');
  return `${hh}:${mm}`;
};

export const TaskListItem = ({ task }: { task: Task }) => {
  const selectedTaskId = useTaakStore((s) => s.selectedTaskId);
  const selectTask = useTaakStore((s) => s.selectTask);
  const updateTask = useTaakStore((s) => s.updateTask);
  const removeTask = useTaakStore((s) => s.removeTask);

  const active = selectedTaskId === task.id;

  const handleFinish = async (checked: boolean) => {
    updateTask(task.id, {
      finished: checked,
      finishedAt: checked ? new Date().toISOString() : undefined,
    });
    if (checked) {
      try {
        await fetch(`/api/taken/${task.id}/afgewerkt`, { method: 'PUT' });
      } catch (e) {
        updateTask(task.id, { finished: false, finishedAt: undefined });
        console.error(e);
      }
    }
  };

  const handleDelete = async () => {
    const backup = { ...task };
    removeTask(task.id);
    try {
      await fetch(`/api/taken/${task.id}`, { method: 'DELETE' });
    } catch (e) {
      useTaakStore.getState().addTask(backup);
      console.error(e);
    }
  };

  return (
    <div className={'flex flex-row items-center gap-2'}>
      <button
        onClick={() => selectTask(task.id)}
        className={`flex-1 flex flex-row items-center gap-3 px-4 py-2 rounded-full bg-zinc-100 hover:bg-zinc-200 transition ${
          active ? 'ring-2 ring-blue-400' : ''
        }`}
      >
        <input
          type={'checkbox'}
          checked={task.finished}
          onChange={(e) => handleFinish(e.target.checked)}
          className={'w-4 h-4 rounded-full'}
          onClick={(e) => e.stopPropagation()}
        />
        <span className={'text-sm flex-1 text-left'}>{task.name}</span>
        <span className={'text-xs text-zinc-500'}>
          {formatDue(task.dueDate)}
        </span>
      </button>
      <button
        onClick={handleDelete}
        className={'p-2 rounded-full hover:bg-zinc-100'}
      >
        <FaRegTrashCan className={'w-4 h-4'} />
      </button>
    </div>
  );
};
