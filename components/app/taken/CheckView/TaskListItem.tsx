'use client';
import { Task, useTaakStore } from '@/stores/taakStore';
import { FaRegTrashCan } from 'react-icons/fa6';
import { HiOutlineClipboardDocumentList } from 'react-icons/hi2';

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
    <div className="p-0.5">
      <div
        onClick={() => selectTask(task.id)}
        className={`group px-4 py-2 shadow-sm rounded-full text-sm flex flex-row gap-2 items-center min-h-13 cursor-pointer transition ${
          active
            ? 'bg-white ring ring-zinc-50'
            : 'bg-zinc-100 hover:bg-zinc-200'
        }`}
      >
        <div className="w-8 h-8 rounded-full bg-zinc-200 flex items-center justify-center shrink-0">
          <HiOutlineClipboardDocumentList className="w-4 h-4 text-zinc-600" />
        </div>
        <input
          type="checkbox"
          checked={task.finished}
          onChange={(e) => handleFinish(e.target.checked)}
          className="w-4 h-4 rounded-full shrink-0"
          onClick={(e) => e.stopPropagation()}
        />
        <span className="flex-1 text-left truncate">{task.name}</span>
        <span className="text-xs text-zinc-500 shrink-0">
          {formatDue(task.dueDate)}
        </span>
        <button
          onClick={(e) => {
            e.stopPropagation();
            handleDelete();
          }}
          className="p-1.5 rounded-full text-zinc-500 hover:text-rose-500 hover:bg-white/60 transition opacity-0 group-hover:opacity-100 duration-300"
        >
          <FaRegTrashCan className="w-3.5 h-3.5" />
        </button>
      </div>
    </div>
  );
};
