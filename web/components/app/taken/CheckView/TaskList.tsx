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
    <div className="flex flex-col gap-3 h-full">
      <Input
        type="text"
        placeholder="Zoek taken..."
        value={search}
        onChange={(e) => setSearch(e.target.value)}
        errorOption={false}
      />
      <div className="flex-1 min-h-0 relative">
        <div className="absolute inset-0 overflow-y-auto scroll-hidden flex flex-col gap-2 pb-10 pr-1">
          {filtered.length === 0 ? (
            <div className="w-full py-8 flex items-center justify-center">
              <Label text="Geen taken gevonden" variant="emptystate" />
            </div>
          ) : (
            filtered.map((t) => <TaskListItem key={t.id} task={t} />)
          )}
        </div>
        <div className="absolute bottom-0 w-full h-10 bg-linear-0 from-gray-200 to-transparent pointer-events-none" />
      </div>
    </div>
  );
};
