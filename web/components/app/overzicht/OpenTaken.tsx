'use client';

import NextLink from 'next/link';
import { useRouter } from 'next/navigation';
import { CheckIcon } from '@phosphor-icons/react';
import type { Task } from '@/stores/taakStore';
import { Container } from '@/components/design-system/Container';

interface OpenTakenProps {
  taken: Task[];
}

function formatDeadline(dueDate: string) {
  return new Date(dueDate).toLocaleDateString('nl-BE', {
    day: 'numeric',
    month: 'short',
  });
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
    <Container onClick={() => router.push('/taken')} label={'Open Taken'}>
      <div className="flex flex-col h-full gap-3">
        {openTaken.length > 0 && (
          <span className="text-xs text-zinc-400 shrink-0">
            {openTaken.length} open
          </span>
        )}

        <div className="flex-1 min-h-0 overflow-y-auto scroll-hidden flex flex-col gap-1.5">
          {openTaken.length === 0 ? (
            <div className="flex h-full items-center justify-center py-4">
              <p className="text-xs text-zinc-400">Geen openstaande taken.</p>
            </div>
          ) : (
            openTaken.map((taak) => {
              const urgent = taak.important || isDichtbijDeadline(taak.dueDate);
              return (
                <NextLink
                  key={taak.id}
                  href={`/taken?taakId=${taak.id}`}
                  onClick={(e) => e.stopPropagation()}
                  className={`flex items-center gap-2.5 px-3 py-2 rounded-2xl border transition-all duration-200 hover:brightness-95 ${
                    urgent
                      ? 'border-rose-200 bg-rose-50/60'
                      : 'border-gray-300/30 bg-gray-300/20'
                  }`}
                >
                  <div
                    className={`w-1.5 h-1.5 rounded-full shrink-0 ${urgent ? 'bg-rose-400' : 'bg-zinc-300'}`}
                  />
                  <span
                    className={`text-xs font-semibold flex-1 truncate ${urgent ? 'text-rose-700' : 'text-zinc-800'}`}
                  >
                    {taak.name}
                  </span>
                  <span
                    className={`text-xs shrink-0 ${urgent ? 'text-rose-400' : 'text-zinc-400'}`}
                  >
                    {formatDeadline(taak.dueDate)}
                  </span>
                </NextLink>
              );
            })
          )}
        </div>

        <NextLink
          href="/taken"
          onClick={(e) => e.stopPropagation()}
          className="shrink-0 flex items-center justify-center gap-1.5 border-t border-gray-300/30 pt-3 text-xs font-semibold text-zinc-400 hover:text-zinc-700 transition-colors duration-200"
        >
          <CheckIcon size={13} />
          Bekijk alle taken
        </NextLink>
      </div>
    </Container>
  );
}
