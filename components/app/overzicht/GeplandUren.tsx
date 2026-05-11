'use client';

import { useEffect, useState } from 'react';
import { useSession } from 'next-auth/react';
import { useRouter } from 'next/navigation';
import type { Shift } from '../planner/types';
import {
  isVrij,
  isVandaag,
  vrijReden,
  isBelgischeFestdag,
} from '../planner/utils';
import { Container } from '@/components/design-system/Container';

const STANDAARD_START = '09:00';
const STANDAARD_EIND = '17:00';

export function GeplandUren() {
  const { data: session } = useSession();
  const router = useRouter();
  const werknemerId = (session?.user as Record<string, unknown>)?.id;

  const vandaag = new Date();
  const komendeDagen: Date[] = Array.from({ length: 7 }, (_, i) => {
    const d = new Date(vandaag);
    d.setDate(vandaag.getDate() + i);
    return d;
  });

  const [dagShifts, setDagShifts] = useState<(Shift | null)[]>(
    Array(7).fill(null),
  );

  useEffect(() => {
    if (!werknemerId) return;
    Promise.all(
      komendeDagen.map((dag) => {
        const datum = dag.toISOString().split('T')[0];
        return fetch(`/api/shifts/werknemer/${werknemerId}?datum=${datum}`)
          .then((r) =>
            r.ok ? (r.json() as Promise<Shift[]>) : Promise.resolve([]),
          )
          .then((shifts) => shifts[0] ?? null)
          .catch(() => null);
      }),
    ).then(setDagShifts);
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [werknemerId]);

  function naarDagPlanning(dag: Date) {
    const datum = dag.toISOString().split('T')[0];
    router.push(`/planner?datum=${datum}&view=dag`);
  }

  return (
    <Container
      label={'Geplande Uren'}
      className="col-start-1 col-end-3 row-start-2 row-end-6"
    >
      <div className="flex flex-col gap-1.5 p-2.5 h-full overflow-y-auto scroll-hidden">
        {komendeDagen.map((dag, i) => {
          const dagNaam = dag.toLocaleDateString('nl-BE', { weekday: 'long' });
          const dagNummer = dag.toLocaleDateString('nl-BE', {
            day: 'numeric',
            month: 'short',
          });
          const vrij = isVrij(dag);
          const festdag = isBelgischeFestdag(dag);
          const shift = dagShifts[i];

          const startTijd =
            shift?.startTijd?.substring(0, 5) ?? STANDAARD_START;
          const eindTijd = shift?.eindTijd?.substring(0, 5) ?? STANDAARD_EIND;
          const isAfwijkend =
            shift != null &&
            (startTijd !== STANDAARD_START || eindTijd !== STANDAARD_EIND);

          const borderClass = isVandaag(dag)
            ? 'border-blue-900 bg-blue-50/40'
            : festdag
              ? 'border-yellow-400 bg-yellow-50/30'
              : 'border-blue-900/25 bg-white/30';

          return (
            <button
              key={i}
              onClick={() => naarDagPlanning(dag)}
              className={`flex flex-col gap-1.5 px-3 py-2.5 rounded-xl border transition-all duration-200 text-left w-full
                hover:brightness-95 cursor-pointer
                ${borderClass}
                ${vrij && !shift ? 'opacity-60' : ''}`}
            >
              <div className="flex items-center justify-between">
                <span
                  className={`text-xs font-bold capitalize ${isVandaag(dag) ? 'text-blue-900' : 'text-zinc-500'}`}
                >
                  {dagNaam}
                </span>
                <span className="text-[10px] text-zinc-400">{dagNummer}</span>
              </div>

              {vrij && !shift ? (
                <span className="text-[10px] text-zinc-400 italic">
                  Vrij – {vrijReden(dag)}
                </span>
              ) : (
                <div className="flex items-center gap-1.5">
                  <span className="text-[9px] font-bold text-zinc-400 uppercase tracking-wide">
                    Shift
                  </span>
                  <span
                    className={`text-[11px] font-semibold ${isAfwijkend ? 'text-red-600' : 'text-zinc-600'}`}
                  >
                    {startTijd} – {eindTijd}
                  </span>
                </div>
              )}
            </button>
          );
        })}
      </div>
    </Container>
  );
}
