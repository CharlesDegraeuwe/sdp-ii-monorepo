'use client';
import { Task, useTaakStore } from '@/stores/taakStore';
import { useMemo } from 'react';
import { TaskListItem } from './TaskListItem';
import { Container } from '@/components/design system/Container';
import { Label } from '@/components/design system/Label';

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

  const filtered = useMemo(() => {
    const all = Object.values(tasks).filter((t) => !t.finished);
    if (!targetId) return all;

    if (scope === 'users') {
      return all.filter((t) => t.assigneeId === targetId);
    }
    const memberIds = teams[targetId]?.members.map((m) => m.id) ?? [];
    return all.filter((t) => t.assigneeId && memberIds.includes(t.assigneeId));
  }, [tasks, teams, targetId, scope]);

  const important = filtered.filter((t) => t.important);
  const today = filtered.filter(
    (t) => !t.important && isToday(new Date(t.dueDate)),
  );
  const tomorrow = filtered.filter(
    (t) => !t.important && isTomorrow(new Date(t.dueDate)),
  );

  if (filtered.length === 0) {
    return (
      <Container label={'Taken'}>
        <div className={'w-full h-full flex items-center justify-center'}>
          <Label text={'Geen taken'} variant={'emptystate'} />
        </div>
      </Container>
    );
  }
  return (
    <Container label={'Taken'}>
      <Section title={'Belangrijk:'} tasks={important} />
      <Section title={'Taken tegen vandaag:'} tasks={today} />
      <Section title={'Tasks due tomorrow:'} tasks={tomorrow} />
    </Container>
  );
};

interface SectionProps {
  title: string;
  tasks: Task[];
}

const Section = (props: SectionProps) => {
  const { title, tasks } = props;
  return (
    <div className={'flex flex-col gap-2'}>
      <Label text={title} size={'sm'} weight={600} />
      {tasks.map((t) => (
        <TaskListItem key={t.id} task={t} />
      ))}
    </div>
  );
};
