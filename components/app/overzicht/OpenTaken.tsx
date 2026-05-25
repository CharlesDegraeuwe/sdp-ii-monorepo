'use client';

import NextLink from 'next/link';
import { useRouter } from 'next/navigation';
import type { Task } from '@/stores/taakStore';
import { Label } from '@/components/design-system/Label';
import { Container } from '@/components/design-system/Container';
import Link from '@/components/design-system/Link/Link';
import { IoMdCheckmark } from 'react-icons/io';

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
      className=""
    >
      {openTaken.length > 0 && (
        <span className="text-[10px] font-bold text-zinc-400">
          {openTaken.length} open
        </span>
      )}
      <div className="flex flex-col h-full relative jutify-between pb-5">
        <div className=" flex-1 overflow-y-auto scroll-hidden flex flex-col gap-1">
          {openTaken.length === 0 && (
            <div className="flex h-full items-center justify-center py-4">
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
                className={`flex items-center gap-2 px-2.5 py-1 rounded-xl border transition-all duration-200 hover:brightness-95
                    ${roodKader ? 'border-red-500/70 bg-rose-300/60 text-white' : 'border-gray-300/40 bg-white/40'}`}
              >
                <div
                  className={`w-1.5 h-1.5 rounded-full flex-shrink-0 ${roodKader ? 'bg-red-400 text-white' : 'bg-zinc-300'}`}
                />

                <div className="flex-1 min-w-0">
                  <span
                    className={`text-[11px] font-bold truncate block ${roodKader ? 'text-rose-700' : 'text-zinc-800'}`}
                  >
                    {taak.name}
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

        <Link
          icon={<IoMdCheckmark />}
          href={'/taken'}
          label={'taken'}
          className="text-[10px] font-bold text-zinc-400"
        />
      </div>
    </Container>
  );
}
