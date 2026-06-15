'use client';

import { useState, useEffect } from 'react';
import { MdEventAvailable, MdLocalHospital } from 'react-icons/md';
import type { Afwezigheid, Shift } from './types';
import type { WerknemerOptie } from '@/hooks/usePlanningFilters';
import { DAGEN_KORT } from './constants';
import { isVandaag, badgeKleur, afwezigheidLabel, isVrij } from './utils';

interface ContextMenuState {
  datum: Date;
  x: number;
  y: number;
}

interface MonthViewProps {
  huidigeDatum: Date;
  afwezigheden: Afwezigheid[];
  eigenShiften: Shift[];
  geselecteerdeDag: Date | null;
  onSelectDag: (datum: Date) => void;
  onNavigeerNaarDag: (datum: Date) => void;
  onVerlofAanvragen: (datum: Date) => void;
  onAfwezigheidMelden: (datum: Date) => void;
  tab?: 'you' | 'team';
  teamWerknemers?: WerknemerOptie[];
  teamShiften?: Shift[];
  geselecteerdeTeamWerknemer?: number | null;
  onSelectTeamWerknemer?: (werknemerId: number, datum: Date) => void;
}

function getShiftVoorWerknemer(
  datum: Date,
  werknemerId: number,
  shifts: Shift[],
): Shift | undefined {
  const ds = datum.toISOString().split('T')[0];
  return shifts.find(
    (s) =>
      s.werknemerId === werknemerId && s.startDatum <= ds && s.eindDatum >= ds,
  );
}

function getShiftVoorDag(datum: Date, shifts: Shift[]): Shift | undefined {
  const ds = datum.toISOString().split('T')[0];
  return shifts.find((s) => s.startDatum <= ds && s.eindDatum >= ds);
}

function tijdStr(t: string | null | undefined, fallback: string): string {
  if (!t) return fallback;
  return t.substring(0, 5);
}

function shiftBlokken(shift?: Shift) {
  const start = tijdStr(shift?.startTijd, '09:00');
  const pauzeS = tijdStr(shift?.pauzeStart, '12:00');
  const pauzeE = tijdStr(shift?.pauzeEind, '13:00');
  const eind = tijdStr(shift?.eindTijd, '17:00');
  return { am: `${start}–${pauzeS}`, pm: `${pauzeE}–${eind}` };
}

function afwezigVoorWerknemer(
  datum: Date,
  werknemerId: number,
  afwezigheden: Afwezigheid[],
): Afwezigheid | undefined {
  return afwezigheden.find((a) => {
    if (a.werknemerId !== werknemerId) return false;
    const start = new Date(a.startDatum);
    const eind = new Date(a.eindDatum);
    return datum >= start && datum <= eind;
  });
}

export default function MonthView({
  huidigeDatum,
  afwezigheden,
  eigenShiften,
  geselecteerdeDag,
  onSelectDag,
  onNavigeerNaarDag,
  onVerlofAanvragen,
  onAfwezigheidMelden,
  tab = 'you',
  teamWerknemers = [],
  teamShiften = [],
  geselecteerdeTeamWerknemer = null,
  onSelectTeamWerknemer,
}: MonthViewProps) {
  const [contextMenu, setContextMenu] = useState<ContextMenuState | null>(null);

  useEffect(() => {
    if (!contextMenu) return;
    const handleClick = () => setContextMenu(null);
    const handleKey = (e: KeyboardEvent) => {
      if (e.key === 'Escape') setContextMenu(null);
    };
    document.addEventListener('click', handleClick);
    document.addEventListener('keydown', handleKey);
    return () => {
      document.removeEventListener('click', handleClick);
      document.removeEventListener('keydown', handleKey);
    };
  }, [contextMenu]);

  const eersteVanMaand = new Date(
    huidigeDatum.getFullYear(),
    huidigeDatum.getMonth(),
    1,
  );
  const startKolom =
    eersteVanMaand.getDay() === 0 ? 6 : eersteVanMaand.getDay() - 1;
  const aantalDagen = new Date(
    huidigeDatum.getFullYear(),
    huidigeDatum.getMonth() + 1,
    0,
  ).getDate();

  const cellen: (Date | null)[] = Array(startKolom).fill(null);
  for (let i = 1; i <= aantalDagen; i++) {
    cellen.push(
      new Date(huidigeDatum.getFullYear(), huidigeDatum.getMonth(), i),
    );
  }
  while (cellen.length % 7 !== 0) cellen.push(null);

  return (
    <div className="flex flex-col gap-1 w-full h-full overflow-x-auto scroll-hidden">
      <div className="min-w-[320px] flex flex-col gap-1 h-full">
        <div className="grid grid-cols-7 gap-1">
          {DAGEN_KORT.map((d) => (
            <div
              key={d}
              className="text-center text-xs font-bold text-zinc-400 uppercase py-2"
            >
              {d}
            </div>
          ))}
        </div>

        <div className="grid grid-cols-7 grid-rows-6 gap-1 flex-1">
          {cellen.map((datum, i) => {
            if (!datum) return <div key={i} className="min-h-20" />;

            const weekend = datum.getDay() === 0 || datum.getDay() === 6;
            const feestdag = isVrij(datum) && !weekend;
            const vrij = weekend || feestdag;
            const geselecteerd =
              geselecteerdeDag &&
              datum.toDateString() === geselecteerdeDag.toDateString();
            const vandaag = isVandaag(datum);

            return (
              <div
                key={i}
                onClick={() => onSelectDag(datum)}
                onContextMenu={(e) => {
                  e.preventDefault();
                  setContextMenu({ datum, x: e.clientX, y: e.clientY });
                }}
                className={[
                  'min-h-20 rounded-2xl p-2 cursor-pointer transition-all duration-200 border flex flex-col gap-1',
                  weekend
                    ? 'bg-zinc-50 border-zinc-100'
                    : 'bg-white border-gray-200/50',
                  vandaag ? 'border-2 border-zinc-900' : '',
                  geselecteerd ? 'ring-2 ring-zinc-400' : '',
                  'hover:shadow-md hover:border-zinc-300',
                ]
                  .filter(Boolean)
                  .join(' ')}
              >
                <span
                  className={`text-xs font-bold ${vandaag ? 'text-zinc-900' : 'text-zinc-500'}`}
                >
                  {datum.getDate()}
                </span>

                {!vrij &&
                  (tab === 'team' && teamWerknemers.length > 0 ? (
                    /* TEAM MODE: één blok per lid */
                    <div className="flex flex-col gap-0.5 mt-0.5 overflow-y-auto max-h-20">
                      {teamWerknemers.map((w) => {
                        const wAfwezig = afwezigVoorWerknemer(
                          datum,
                          w.id,
                          afwezigheden,
                        );
                        const wShift = getShiftVoorWerknemer(
                          datum,
                          w.id,
                          teamShiften,
                        );
                        const blokken = shiftBlokken(wShift);
                        const isActief = geselecteerdeTeamWerknemer === w.id;

                        if (!wAfwezig && !wShift) return null;

                        return (
                          <button
                            key={w.id}
                            onClick={(e) => {
                              e.stopPropagation();
                              onSelectTeamWerknemer?.(w.id, datum);
                            }}
                            className={[
                              'text-left w-full rounded-lg px-1.5 py-0.5 transition-colors border',
                              isActief
                                ? 'bg-zinc-800 border-zinc-800'
                                : wAfwezig
                                  ? `${badgeKleur(wAfwezig)} border-transparent`
                                  : 'bg-rose-50 border-rose-100 hover:bg-rose-100',
                            ].join(' ')}
                          >
                            <span
                              className={`text-[8px] font-bold block truncate ${isActief ? 'text-white' : ''}`}
                            >
                              {w.voornaam}
                            </span>
                            {wAfwezig ? (
                              <span
                                className={`text-[7px] font-semibold block truncate ${isActief ? 'text-white/70' : ''}`}
                              >
                                {afwezigheidLabel(wAfwezig)}
                              </span>
                            ) : (
                              <span
                                className={`text-[7px] block truncate ${isActief ? 'text-white/70' : 'text-rose-600'}`}
                              >
                                {blokken.am} · {blokken.pm}
                              </span>
                            )}
                          </button>
                        );
                      })}
                    </div>
                  ) : (
                    /* PERSONAL MODE: AM/PM blokjes */
                    (() => {
                      const dagAfwezigheid = afwezigheden.filter((a) => {
                        const start = new Date(a.startDatum);
                        const eind = new Date(a.eindDatum);
                        return datum >= start && datum <= eind;
                      });
                      const isAfwezig = dagAfwezigheid.length > 0;
                      const shift = getShiftVoorDag(datum, eigenShiften);
                      const blokken = shiftBlokken(shift);

                      return (
                        <div className="flex flex-col gap-0.5 mt-0.5">
                          {isAfwezig ? (
                            <span
                              className={`text-[9px] font-bold px-1.5 py-0.5 rounded-full truncate w-full text-center ${badgeKleur(dagAfwezigheid[0])}`}
                            >
                              {afwezigheidLabel(dagAfwezigheid[0])}
                            </span>
                          ) : shift ? (
                            <>
                              <button
                                onClick={(e) => {
                                  e.stopPropagation();
                                  onNavigeerNaarDag(datum);
                                }}
                                className="text-[9px] font-semibold px-1.5 py-0.5 rounded-full bg-rose-100 text-rose-700 hover:bg-rose-200 transition-colors text-left truncate"
                              >
                                {blokken.am}
                              </button>
                              <button
                                onClick={(e) => {
                                  e.stopPropagation();
                                  onNavigeerNaarDag(datum);
                                }}
                                className="text-[9px] font-semibold px-1.5 py-0.5 rounded-full bg-rose-100 text-rose-700 hover:bg-rose-200 transition-colors text-left truncate"
                              >
                                {blokken.pm}
                              </button>
                            </>
                          ) : null}
                        </div>
                      );
                    })()
                  ))}
              </div>
            );
          })}
        </div>
      </div>

      {contextMenu && (
        <>
          <div
            className="fixed inset-0 z-40"
            onClick={() => setContextMenu(null)}
          />
          <div
            className="fixed z-50 bg-white rounded-xl shadow-xl border border-zinc-100 py-1 min-w-[12rem]"
            style={{ top: contextMenu.y, left: contextMenu.x }}
            onClick={(e) => e.stopPropagation()}
          >
            <div className="px-3 py-1.5 text-[9px] font-bold text-zinc-400 uppercase tracking-wide border-b border-zinc-100 mb-1">
              {contextMenu.datum.toLocaleDateString('nl-BE', {
                weekday: 'long',
                day: 'numeric',
                month: 'long',
              })}
            </div>
            <button
              className="w-full text-left px-3 py-2 text-xs font-semibold text-zinc-700 hover:bg-zinc-50 transition-colors flex items-center gap-2"
              onClick={() => {
                onVerlofAanvragen(contextMenu.datum);
                setContextMenu(null);
              }}
            >
              <MdEventAvailable
                size={15}
                className="text-emerald-500 shrink-0"
              />
              Verlof aanvragen
            </button>
            <button
              className="w-full text-left px-3 py-2 text-xs font-semibold text-zinc-700 hover:bg-zinc-50 transition-colors flex items-center gap-2"
              onClick={() => {
                onAfwezigheidMelden(contextMenu.datum);
                setContextMenu(null);
              }}
            >
              <MdLocalHospital size={15} className="text-red-400 shrink-0" />
              Afwezigheid melden
            </button>
          </div>
        </>
      )}
    </div>
  );
}
