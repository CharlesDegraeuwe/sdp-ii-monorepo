'use client';

import { useEffect, useMemo, useState } from 'react';
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
const AANTAL_DAGEN = 7;

function formatDatumLokaal(d: Date): string {
  const jaar = d.getFullYear();
  const maand = String(d.getMonth() + 1).padStart(2, '0');
  const dag = String(d.getDate()).padStart(2, '0');
  return `${jaar}-${maand}-${dag}`;
}

function tijdZonderSeconden(tijd?: string | null): string | null {
  return tijd ? tijd.substring(0, 5) : null;
}

function genereerDagen(startDatum: Date, aantal: number): Date[] {
  return Array.from({ length: aantal }, (_, i) => {
    const d = new Date(startDatum);
    d.setDate(startDatum.getDate() + i);
    return d;
  });
}

type DagInfo = { datum: Date; shift: Shift | null };

function DagItem({ info, onClick }: { info: DagInfo; onClick: () => void }) {
  const { datum, shift } = info;

  const dagNaam = datum.toLocaleDateString('nl-BE', { weekday: 'long' });
  const dagNummer = datum.toLocaleDateString('nl-BE', {
    day: 'numeric',
    month: 'short',
  });

  const vandaag = isVandaag(datum);
  const festdag = isBelgischeFestdag(datum);
  const vrij = isVrij(datum);

  const startTijd = tijdZonderSeconden(shift?.startTijd) ?? STANDAARD_START;
  const eindTijd = tijdZonderSeconden(shift?.eindTijd) ?? STANDAARD_EIND;
  const isAfwijkend =
    shift != null &&
    (startTijd !== STANDAARD_START || eindTijd !== STANDAARD_EIND);

  const bgClass = vandaag
    ? 'border-blue-200/50 bg-blue-50/40'
    : festdag
      ? 'border-yellow-200/50 bg-yellow-50/30'
      : 'border-gray-300/30 bg-gray-300/20';

  return (
    <div
      onClick={onClick}
      className={`flex flex-row items-center justify-between px-3 py-2.5 rounded-2xl border transition-all duration-200 cursor-pointer hover:brightness-95 ${bgClass} ${vrij && !shift ? 'opacity-60' : ''}`}
    >
      <div className="flex flex-col gap-0.5">
        <span
          className={`text-xs font-semibold ${vandaag ? 'text-blue-800' : 'text-zinc-600'}`}
        >
          {dagNaam}
        </span>
        {vrij && !shift ? (
          <span className="text-xs text-zinc-400 italic">
            Vrij – {vrijReden(datum)}
          </span>
        ) : (
          <div className="flex items-center gap-1.5">
            <span className="text-xs text-zinc-400 uppercase tracking-wide font-semibold">
              Shift
            </span>
            <span
              className={`text-xs font-semibold ${isAfwijkend ? 'text-red-600' : 'text-zinc-600'}`}
            >
              {startTijd} – {eindTijd}
            </span>
          </div>
        )}
      </div>
      <span className="text-xs text-zinc-400">{dagNummer}</span>
    </div>
  );
}

export function GeplandUren() {
  const { data: session } = useSession();
  const router = useRouter();
  const werknemerId = session?.user?.id;

  const komendeDagen = useMemo(
    () => genereerDagen(new Date(), AANTAL_DAGEN),
    [],
  );

  const [shifts, setShifts] = useState<(Shift | null)[]>(() =>
    Array(AANTAL_DAGEN).fill(null),
  );

  useEffect(() => {
    if (!werknemerId) return;
    let cancelled = false;

    Promise.all(
      komendeDagen.map((dag) =>
        fetch(
          `/api/shifts/werknemer/${werknemerId}?datum=${formatDatumLokaal(dag)}`,
        )
          .then((r) => (r.ok ? (r.json() as Promise<Shift[]>) : []))
          .then((rij) => rij[0] ?? null)
          .catch(() => null),
      ),
    ).then((resultaat) => {
      if (!cancelled) setShifts(resultaat);
    });

    return () => {
      cancelled = true;
    };
  }, [werknemerId, komendeDagen]);

  const dagen: DagInfo[] = komendeDagen.map((datum, i) => ({
    datum,
    shift: shifts[i],
  }));

  function naarDagPlanning(dag: Date) {
    router.push(`/planner?datum=${formatDatumLokaal(dag)}&view=dag`);
  }

  return (
    <Container height={'full flex-1'} label={'Geplande Uren'}>
      <div className="flex flex-col gap-2 h-full overflow-y-auto scroll-hidden">
        {dagen.map((info) => (
          <DagItem
            key={info.datum.toISOString()}
            info={info}
            onClick={() => naarDagPlanning(info.datum)}
          />
        ))}
      </div>
    </Container>
  );
}
