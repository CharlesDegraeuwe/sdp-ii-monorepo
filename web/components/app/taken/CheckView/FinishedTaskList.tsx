'use client';
import { useState, useMemo } from 'react';
import { Input } from '@/components/design-system/Input';
import { Label } from '@/components/design-system/Label';
import { useTaakStore } from '@/stores/taakStore';
import { HiOutlineClipboardDocumentList } from 'react-icons/hi2';

export const FinishedTaskList = () => {
  const [search, setSearch] = useState('');
  const tasks = useTaakStore((s) => s.tasks);
  const selectedTaskId = useTaakStore((s) => s.selectedTaskId);
  const selectTask = useTaakStore((s) => s.selectTask);

  const finished = useMemo(() => {
    let all = Object.values(tasks).filter((t) => t.finished);
    if (search) {
      const q = search.toLowerCase();
      all = all.filter((t) => t.name.toLowerCase().includes(q));
    }
    return all
      .sort(
        (a, b) =>
          new Date(b.finishedAt ?? 0).getTime() -
          new Date(a.finishedAt ?? 0).getTime(),
      )
      .slice(0, 10);
  }, [tasks, search]);

  return (
    <div className="flex flex-col gap-3">
      <Input
        type="text"
        placeholder="Zoek afgewerkte taken..."
        value={search}
        onChange={(e) => setSearch(e.target.value)}
        errorOption={false}
      />
      <div className="flex flex-col gap-2 max-h-150 min-h-150 relative pr-1 overflow-x-visible scroll-hidden">
        <div className="flex flex-col gap-2 h-full pb-10 overflow-y-auto overflow-x-visible scroll-hidden">
          {finished.length === 0 ? (
            <div className="w-full py-8 flex items-center justify-center">
              <Label text="Geen afgewerkte taken" variant="emptystate" />
            </div>
          ) : (
            finished.map((t) => (
              <div
                key={t.id}
                onClick={() => selectTask(t.id)}
                className="p-0.5"
              >
                <div
                  className={`px-4 py-2 shadow-sm rounded-full text-sm flex flex-row gap-2 items-center min-h-13 cursor-pointer text-left transition ${
                    selectedTaskId === t.id
                      ? 'bg-white ring ring-zinc-50'
                      : 'bg-zinc-100 hover:bg-zinc-200'
                  }`}
                >
                  <div className="w-8 h-8 rounded-full bg-zinc-200 flex items-center justify-center">
                    <HiOutlineClipboardDocumentList className="w-4 h-4 text-zinc-600" />
                  </div>
                  <span className="flex-1 truncate">{t.name}</span>
                  <span className="text-xs text-zinc-500 shrink-0">
                    {new Date(t.finishedAt ?? '').toLocaleDateString()}
                  </span>
                </div>
              </div>
            ))
          )}
        </div>
        <div className="absolute bottom-0 w-full h-10 bg-linear-0 from-gray-200 to-transparent" />
      </div>
    </div>
  );
};
