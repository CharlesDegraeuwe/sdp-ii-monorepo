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

type DagInfo = {
  datum: Date;
  shift: Shift | null;
};

type DagItemProps = {
  info: DagInfo;
  onClick: () => void;
};

function DagItem({ info, onClick }: DagItemProps) {
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

  const borderClass = vandaag
    ? 'border-zinc-200/50 bg-blue-50/40'
    : festdag
      ? 'border-zinc-200/50 bg-yellow-200/30'
      : 'border-zinc-200/50 bg-white/30';

  return (
    <div
      onClick={onClick}
      className={`gap-1.5 py-2.5 rounded-2xl border transition-all duration-200 text-left w-full
        hover:brightness-95 cursor-pointer shadow-lg px-5 justify-between flex flex-row items-center
        ${borderClass}
        ${vrij && !shift ? 'opacity-60' : ''}`}
    >
      <div className="flex flex-col">
        <span
          className={`text-xs font-bold ${
            vandaag ? 'text-blue-900' : 'text-zinc-500'
          }`}
        >
          {dagNaam}
        </span>
        {vrij && !shift ? (
          <span className="text-[10px] text-zinc-400 italic">
            Vrij – {vrijReden(datum)}
          </span>
        ) : (
          <div className="flex items-center gap-1.5">
            <span className="text-[9px] font-bold text-zinc-400 uppercase tracking-wide">
              Shift
            </span>
            <span
              className={`text-[11px] font-semibold ${
                isAfwijkend ? 'text-red-600' : 'text-zinc-600'
              }`}
            >
              {startTijd} – {eindTijd}
            </span>
          </div>
        )}
      </div>

      <span className="text-[10px] text-zinc-400">{dagNummer}</span>
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

    // TODO: vervang door één range endpoint /api/shifts/werknemer/{id}?van=...&tot=...
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
      <div className="flex flex-col gap-5 p-2.5 h-full overflow-y-auto scroll-hidden">
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
