'use client';
import { useMemo, useState } from 'react';
import { Label } from '@/components/design-system/Label';
import { Button } from '@/components/design-system/Button';
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

  return (
    <div className="flex flex-col gap-3 text-sm">
      <hr className="opacity-10" />
      <div className="flex flex-row items-center justify-between">
        <span className="text-xs text-zinc-400">Afgewerkte taken</span>
        {finished.length > 6 && (
          <Button
            type="button"
            label={showAll ? 'Minder tonen' : 'Meer tonen'}
            variant="outline"
            textSize="sm"
            px="px-3"
            onClick={() => setShowAll(!showAll)}
          />
        )}
      </div>

      {finished.length === 0 ? (
        <div className="w-full py-8 flex items-center justify-center">
          <Label text="Nog geen taken afgewerkt" variant="emptystate" />
        </div>
      ) : (
        <div className="flex flex-col gap-2 overflow-y-auto scroll-hidden pr-1">
          {visible.map((t) => (
            <div key={t.id} className="p-0.5">
              <div className="flex flex-row items-center gap-3 px-4 py-2 shadow-sm rounded-full bg-zinc-100 min-h-13">
                <span className="w-4 h-4 rounded-full bg-zinc-400 shrink-0" />
                <span className="text-sm flex-1 truncate">{t.name}</span>
                <span className="text-xs text-zinc-500 shrink-0">
                  {new Date(t.finishedAt ?? '').toLocaleDateString()}
                </span>
              </div>
            </div>
          ))}
        </div>
      )}
    </div>
  );
};
