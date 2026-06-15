import { HiOutlineClipboardDocumentList } from 'react-icons/hi2';
import { Task } from '@/stores/taakStore';

type Props = {
  task: Task;
  active: boolean;
  onClick: () => void;
};

export const TaskListItem = ({ task, active, onClick }: Props) => (
  <div onClick={onClick} className="p-0.5">
    <div
      className={`px-4 py-2 shadow-sm rounded-full text-sm flex flex-row gap-2 items-center min-h-13 cursor-pointer text-left transition ${
        active ? 'bg-white ring ring-zinc-50' : 'bg-zinc-100 hover:bg-zinc-200'
      }`}
    >
      <div className="w-8 h-8 rounded-full bg-zinc-200 flex items-center justify-center">
        <HiOutlineClipboardDocumentList className="w-4 h-4 text-zinc-600" />
      </div>
      <span className="flex-1 truncate">{task.name}</span>
    </div>
  </div>
);
