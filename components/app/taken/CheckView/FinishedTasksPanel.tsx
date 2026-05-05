'use client';
import { useMemo, useState } from 'react';
import { Container } from '@/components/design system/Container';
import { Label } from '@/components/design system/Label';
import { Button } from '@/components/design system/Button';
import { useTaakStore } from '@/stores/taakStore';

interface Props {
  targetId: string | null;
  scope: 'teams' | 'users';
}

export const FinishedTasksPanel = ({ targetId, scope }: Props) => {
  const tasks = useTaakStore((s) => s.tasks);
  const teams = useTaakStore((s) => s.teams);
  const [showAll, setShowAll] = useState(false);

  const finished = useMemo(() => {
    let all = Object.values(tasks).filter((t) => t.finished);
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
    return all.sort(
      (a, b) =>
        new Date(b.finishedAt ?? 0).getTime() -
        new Date(a.finishedAt ?? 0).getTime(),
    );
  }, [tasks, teams, targetId, scope]);

  const visible = showAll ? finished : finished.slice(0, 6);
  if (finished.length === 0) {
    return (
      <Container height={'1/2'} label={'Afgewerkte taken'}>
        <div className={'w-full h-full flex items-center justify-center'}>
          <Label text={'Nog geen taken afgewerkt'} variant={'emptystate'} />
        </div>
      </Container>
    );
  }

  return (
    <Container height={'1/2'} label={'Afgewerkte taken'}>
      {visible.map((t) => (
        <div
          key={t.id}
          className={
            'flex flex-row items-center gap-3 px-4 py-2 rounded-full bg-zinc-100'
          }
        >
          <span className={'w-4 h-4 rounded-full bg-zinc-400'} />
          <span className={'text-sm flex-1'}>{t.name}</span>
          <span className={'text-xs text-zinc-500'}>
            Afgewerkt {new Date(t.finishedAt ?? '').toLocaleDateString()}
          </span>
        </div>
      ))}
      {finished.length > 6 && (
        <div className={'self-end'}>
          <Button
            type="button"
            label={showAll ? 'Minder tonen' : 'Meer tonen'}
            variant={'outline'}
            textSize={'sm'}
            px={'px-3'}
            onClick={() => setShowAll(!showAll)}
          />
        </div>
      )}
    </Container>
  );
};
