'use client';
import { useState, useMemo } from 'react';
import { Input } from '@/components/design-system/Input';
import { Label } from '@/components/design-system/Label';
import { TaskListItem } from './TaskListItem';
import { useTaken } from '@/hooks/useTaken';

export const TaskList = () => {
  const [search, setSearch] = useState('');
  const { data: tasks = [] } = useTaken();
  const filtered = useMemo(() => {
    let all = tasks.filter((t) => !t.finished);
    if (search) {
      const q = search.toLowerCase();
      all = all.filter((t) => t.name.toLowerCase().includes(q));
    }
    return all.sort((a, b) => {
      if (a.important !== b.important) return a.important ? -1 : 1;
      return new Date(a.dueDate).getTime() - new Date(b.dueDate).getTime();
    });
  }, [tasks, search]);

  return (
    <div className="flex flex-col gap-3">
      <Input
        type="text"
        placeholder="Zoek taken..."
        value={search}
        onChange={(e) => setSearch(e.target.value)}
        errorOption={false}
      />
      <div className="flex flex-col gap-2 max-h-150 min-h-150 relative pr-1 overflow-x-visible scroll-hidden">
        <div className="flex flex-col gap-2 h-full pb-10 overflow-y-auto overflow-x-visible scroll-hidden">
          {filtered.length === 0 ? (
            <div className="w-full py-8 flex items-center justify-center">
              <Label text="Geen taken gevonden" variant="emptystate" />
            </div>
          ) : (
            filtered.map((t) => <TaskListItem key={t.id} task={t} />)
          )}
        </div>
        <div className="absolute bottom-0 w-full h-10 bg-linear-0 from-gray-200 to-transparent" />
      </div>
    </div>
  );
};
