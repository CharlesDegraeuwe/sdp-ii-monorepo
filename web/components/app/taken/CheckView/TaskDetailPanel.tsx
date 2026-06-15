'use client';
import { useTaakStore } from '@/stores/taakStore';
import { Label } from '@/components/design-system/Label';
import { Button } from '@/components/design-system/Button';
import { HiOutlineClipboardDocumentList } from 'react-icons/hi2';
import { HiOutlineLocationMarker } from 'react-icons/hi';
import { MdOutlineCopyAll } from 'react-icons/md';
import { LiaHashtagSolid } from 'react-icons/lia';
import { FaRegTrashCan } from 'react-icons/fa6';

export const TaskDetailPanel = () => {
  const selectedTaskId = useTaakStore((s) => s.selectedTaskId);
  const task = useTaakStore((s) =>
    selectedTaskId ? s.tasks[selectedTaskId] : null,
  );
  const removeTask = useTaakStore((s) => s.removeTask);

  if (!task) {
    return (
      <div className="w-full h-full flex items-center justify-center pb-12 px-4">
        <Label text="Selecteer een taak" variant="emptystate" />
      </div>
    );
  }

  const due = new Date(task.dueDate);
  const time = `${due.getHours().toString().padStart(2, '0')}:${due
    .getMinutes()
    .toString()
    .padStart(2, '0')}`;

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
    <div className="flex flex-col gap-5 h-full p-3">
      <div className="flex flex-col min-w-0 w-full">
        <div className="flex flex-row items-center justify-between">
          <div className="flex flex-row gap-2 items-center">
            <div className="w-10 h-10 rounded-full bg-zinc-100 flex items-center justify-center">
              <HiOutlineClipboardDocumentList className="w-5 h-5 text-zinc-600" />
            </div>
            <div className="flex flex-col">
              <span
                onClick={() => navigator.clipboard.writeText(task.name)}
                className="font-bold group flex flex-row items-center cursor-pointer text-lg leading-tight truncate"
              >
                {task.name}
                <MdOutlineCopyAll className="group-hover:opacity-100 ml-2 group-active:scale-90 text-base opacity-0 transition-all duration-300" />
              </span>
              <span className="text-xs text-zinc-400">Deadline om {time}</span>
            </div>
          </div>

          <button
            onClick={handleDelete}
            className="p-2 rounded-full text-zinc-400 hover:text-rose-500 hover:bg-rose-50 transition cursor-pointer"
          >
            <FaRegTrashCan className="w-4 h-4" />
          </button>
        </div>

        <div className="flex flex-col gap-2 pt-3 text-sm text-zinc-600 bg-zinc-50 shadow-sm rounded-3xl my-3 p-3">
          <span className="font-bold text-zinc-800">Details:</span>
          <div className="flex flex-row items-center gap-2 text-xs">
            <LiaHashtagSolid className="w-3 h-3" />
            <span>{task.id}</span>
          </div>
          <div className="flex flex-row items-center gap-2 text-xs">
            <HiOutlineLocationMarker className="w-3 h-3" />
            <span>{task.location || 'Geen locatie'}</span>
          </div>
        </div>
      </div>

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
          label="Bewerk"
          variant="outline"
          textSize="sm"
          px="px-3"
        />
      </div>
    </div>
  );
};
