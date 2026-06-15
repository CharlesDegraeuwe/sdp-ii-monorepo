'use client';

import NextLink from 'next/link';
import { useRouter } from 'next/navigation';
import type { Task } from '@/stores/taakStore';
import { Label } from '@/components/design-system/Label';
import { Container } from '@/components/design-system/Container';

interface OpenTakenProps {
  taken: Task[];
}

function formatDeadline(dueDate: string) {
  const d = new Date(dueDate);
  return d.toLocaleDateString('nl-BE', { day: 'numeric', month: 'short' });
}

function isDichtbijDeadline(dueDate: string): boolean {
  const deadline = new Date(dueDate);
  const vandaag = new Date();
  deadline.setHours(0, 0, 0, 0);
  vandaag.setHours(0, 0, 0, 0);
  return deadline <= vandaag;
}

export function OpenTaken({ taken }: OpenTakenProps) {
  const router = useRouter();
  const openTaken = taken.filter((t) => !t.finished);

  return (
    <Container
      onClick={() => router.push('/taken')}
      label={'Open Taken'}
      className="col-start-3 col-end-4 row-start-3 row-end-4"
    >
      {openTaken.length > 0 && (
        <span className="text-[10px] font-bold text-zinc-400">
          {openTaken.length} open
        </span>
      )}
      <div className="flex flex-col">
        <div className="max-h-[160px] overflow-y-auto scroll-hidden flex flex-col gap-1 p-2.5 rounded-t-3xl">
          {openTaken.length === 0 && (
            <div className="flex items-center justify-center py-4">
              <Label text="Geen openstaande taken." variant="emptystate" />
            </div>
          )}

          {openTaken.map((taak) => {
            const deadline = isDichtbijDeadline(taak.dueDate);
            const roodKader = taak.important || deadline;
            return (
              <NextLink
                key={taak.id}
                href={`/taken?taakId=${taak.id}`}
                onClick={(e) => e.stopPropagation()}
                className={`flex items-center gap-2 px-2.5 py-2 rounded-xl border transition-all duration-200 hover:brightness-95
                    ${roodKader ? 'border-red-300/70 bg-red-50/60' : 'border-gray-300/40 bg-white/40'}`}
              >
                <div
                  className={`w-1.5 h-1.5 rounded-full flex-shrink-0 ${roodKader ? 'bg-red-400' : 'bg-zinc-300'}`}
                />

                <div className="flex-1 min-w-0">
                  <span
                    className={`text-[11px] font-bold truncate block ${roodKader ? 'text-red-700' : 'text-zinc-800'}`}
                  >
                    {taak.name}
                  </span>
                  <span className="text-[9px] text-zinc-400">
                    {taak.location}
                  </span>
                </div>

                <span
                  className={`text-[9px] font-bold flex-shrink-0 ${roodKader ? 'text-red-400' : 'text-zinc-400'}`}
                >
                  {formatDeadline(taak.dueDate)}
                </span>
              </NextLink>
            );
          })}
        </div>

        <div className="border-t border-gray-300/30 py-2 flex items-center justify-center pointer-events-none rounded-b-3xl">
          <span className="text-[10px] font-bold text-zinc-400">
            Bekijk alle taken
          </span>
        </div>
      </div>
    </Container>
  );
}
