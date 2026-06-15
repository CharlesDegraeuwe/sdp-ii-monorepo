'use client';
import { Task, useTaakStore } from '@/stores/taakStore';
import { useMemo, useState } from 'react';
import { TaskListItem } from './TaskListItem';
import { Input } from '@/components/design-system/Input';
import { Label } from '@/components/design-system/Label';

interface TaskColumnProps {
  targetId: string | null;
  scope: 'teams' | 'users';
}

const isToday = (d: Date) => {
  const now = new Date();
  return (
    d.getDate() === now.getDate() &&
    d.getMonth() === now.getMonth() &&
    d.getFullYear() === now.getFullYear()
  );
};

const isTomorrow = (d: Date) => {
  const t = new Date();
  t.setDate(t.getDate() + 1);
  return (
    d.getDate() === t.getDate() &&
    d.getMonth() === t.getMonth() &&
    d.getFullYear() === t.getFullYear()
  );
};

export const TaskColumn = ({ targetId, scope }: TaskColumnProps) => {
  const tasks = useTaakStore((s) => s.tasks);
  const teams = useTaakStore((s) => s.teams);
  const [search, setSearch] = useState('');

  const filtered = useMemo(() => {
    let all = Object.values(tasks).filter((t) => !t.finished);
    if (targetId) {
      if (scope === 'users') {
        all = all.filter((t) => t.assigneeId === targetId);
      } else {
        const memberIds = teams[targetId]?.members.map((m) => m.id) ?? [];
        all = all.filter(
          (t) => t.assigneeId && memberIds.includes(t.assigneeId),
        );
      }
    }
    if (search) {
      const q = search.toLowerCase();
      all = all.filter((t) => t.name.toLowerCase().includes(q));
    }
    return all;
  }, [tasks, teams, targetId, scope, search]);

  const important = filtered.filter((t) => t.important);
  const today = filtered.filter(
    (t) => !t.important && isToday(new Date(t.dueDate)),
  );
  const tomorrow = filtered.filter(
    (t) => !t.important && isTomorrow(new Date(t.dueDate)),
  );

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
            <>
              <Section title="Belangrijk:" tasks={important} />
              <Section title="Taken tegen vandaag:" tasks={today} />
              <Section title="Taken tegen morgen:" tasks={tomorrow} />
            </>
          )}
        </div>
        <div className="absolute bottom-0 w-full h-10 bg-linear-0 from-gray-200 to-transparent" />
      </div>
    </div>
  );
};

interface SectionProps {
  title: string;
  tasks: Task[];
}

const Section = ({ title, tasks }: SectionProps) => {
  if (tasks.length === 0) return null;
  return (
    <div className="flex flex-col gap-2">
      <Label text={title} size="sm" weight={600} />
      {tasks.map((t) => (
        <TaskListItem key={t.id} task={t} />
      ))}
    </div>
  );
};
