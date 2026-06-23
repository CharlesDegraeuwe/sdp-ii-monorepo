'use client';

import type { Afwezigheid, PlannerTaak, Shift } from './types';
import type { WerknemerOptie } from '@/hooks/usePlanningFilters';
import { DAGEN_KORT, UREN } from './constants';
import {
  afwezighedenOpDag,
  datumNaarString,
  getMaandag,
  isVandaag,
  badgeKleur,
  afwezigheidLabel,
  takenOpDag,
  taakBadgeKleur,
  isVrij,
} from './utils';

interface WeekViewProps {
  huidigeDatum: Date;
  afwezigheden: Afwezigheid[];
  taken: PlannerTaak[];
  eigenShiften: Shift[];
  geselecteerdeDag: Date | null;
  onSelectDag: (datum: Date) => void;
  onNavigeerNaarDag: (datum: Date) => void;
  tab?: 'you' | 'team';
  teamWerknemers?: WerknemerOptie[];
  teamShiften?: Shift[];
  geselecteerdeTeamWerknemer?: number | null;
  onSelectTeamWerknemer?: (werknemerId: number, datum: Date) => void;
}

const START_UUR = 0;
const EIND_UUR = 24;
const ZICHTBARE_UREN = UREN.slice(START_UUR, EIND_UUR);
const UUR_HOOGTE = 60;

function parseDuur(duur?: string): number {
  if (!duur) return 1;
  const urenMatch = duur.match(/(\d+)\s*u/i);
  const minMatch = duur.match(/(\d+)\s*m/i);
  let totaal = 0;
  if (urenMatch) totaal += parseInt(urenMatch[1]);
  if (minMatch) totaal += parseInt(minMatch[1]) / 60;
  return totaal || 1;
}

function parseUur(datum: string): number {
  const d = new Date(datum);
  return d.getHours() + d.getMinutes() / 60;
}

function tijdDecimaal(t: string | null | undefined, fallback: number): number {
  if (!t) return fallback;
  const parts = t.substring(0, 5).split(':');
  return parseInt(parts[0]) + parseInt(parts[1]) / 60;
}

function formatTijd(decimal: number): string {
  const h = Math.floor(decimal);
  const m = Math.round((decimal - h) * 60);
  return `${String(h).padStart(2, '0')}:${String(m).padStart(2, '0')}`;
}

function getShiftVoorWerknemer(
  datum: Date,
  werknemerId: number,
  shifts: Shift[],
): Shift | undefined {
  const ds = datumNaarString(datum);
  return shifts.find(
    (s) =>
      s.werknemerId === werknemerId && s.startDatum <= ds && s.eindDatum >= ds,
  );
}

function getShiftVoorDag(datum: Date, shifts: Shift[]): Shift | undefined {
  const ds = datumNaarString(datum);
  return shifts.find((s) => s.startDatum <= ds && s.eindDatum >= ds);
}

export default function WeekView({
  huidigeDatum,
  afwezigheden,
  taken,
  eigenShiften,
  geselecteerdeDag,
  onSelectDag,
  onNavigeerNaarDag,
  tab = 'you',
  teamWerknemers = [],
  teamShiften = [],
  geselecteerdeTeamWerknemer = null,
  onSelectTeamWerknemer,
}: WeekViewProps) {
  const maandag = getMaandag(huidigeDatum);
  const dagen = Array.from({ length: 7 }, (_, i) => {
    const d = new Date(maandag);
    d.setDate(d.getDate() + i);
    return d;
  });

  const isTeam = tab === 'team' && teamWerknemers.length > 0;

  return (
    <div className="flex flex-col h-full min-h-0 overflow-x-auto scroll-hidden">
      <div className="min-w-[500px] flex flex-col h-full min-h-0">
        {/* Header */}
        <div className="flex border-b border-zinc-200">
          <div className="w-14 shrink-0" />
          <div className="grid grid-cols-7 flex-1 gap-px">
            {dagen.map((datum, i) => {
              const opDag = afwezighedenOpDag(afwezigheden, datum);
              const weekend = datum.getDay() === 0 || datum.getDay() === 6;
              const geselecteerd =
                geselecteerdeDag &&
                datum.toDateString() === geselecteerdeDag.toDateString();

              return (
                <div
                  key={i}
                  onClick={() => onSelectDag(datum)}
                  className={[
                    'flex flex-col cursor-pointer p-2 transition-colors',
                    weekend ? 'bg-zinc-50' : 'bg-white',
                    geselecteerd ? 'bg-zinc-100' : '',
                  ]
                    .filter(Boolean)
                    .join(' ')}
                >
                  <div className="flex flex-col items-center mb-1">
                    <span className="text-[10px] font-bold text-zinc-400 uppercase">
                      {DAGEN_KORT[i]}
                    </span>
                    <span
                      className={[
                        'text-lg font-bold w-9 h-9 flex items-center justify-center rounded-full',
                        isVandaag(datum)
                          ? 'bg-zinc-900 text-white'
                          : 'text-zinc-600',
                      ].join(' ')}
                    >
                      {datum.getDate()}
                    </span>
                  </div>
                  {opDag.length > 0 && !isTeam ? (
                    <div className="flex flex-col gap-0.5 max-h-7 min-h-7">
                      <div
                        className={`rounded px-1.5 py-0.5 ${badgeKleur(opDag[0])}`}
                      >
                        <span className="text-[9px] font-bold block truncate">
                          {afwezigheidLabel(opDag[0])}
                        </span>
                      </div>
                    </div>
                  ) : (
                    <div className="max-h-7 min-h-7" />
                  )}
                </div>
              );
            })}
          </div>
        </div>

        {/* Timeline */}
        <div className="flex-1 overflow-y-scroll scroll-hidden min-h-0 pb-10 rounded-2xl overflow-hidden">
          <div
            className="flex"
            style={{ height: ZICHTBARE_UREN.length * UUR_HOOGTE }}
          >
            {/* Uur labels */}
            <div className="w-14 shrink-0 relative">
              {ZICHTBARE_UREN.map((uur, i) => (
                <div
                  key={uur}
                  className="absolute right-2 text-[10px] text-zinc-400 -translate-y-1/2 pt-3"
                  style={{ top: i * UUR_HOOGTE }}
                >
                  {uur}
                </div>
              ))}
            </div>

            {/* Dag kolommen */}
            <div className="grid grid-cols-7 flex-1 gap-px relative">
              {dagen.map((datum, i) => {
                const dagTaken = takenOpDag(taken, datum).filter(
                  (t) => !t.afgewerkt,
                );
                const weekend = datum.getDay() === 0 || datum.getDay() === 6;
                const feestdag = isVrij(datum) && !weekend;
                const vrij = weekend || feestdag;
                const geselecteerd =
                  geselecteerdeDag &&
                  datum.toDateString() === geselecteerdeDag.toDateString();

                return (
                  <div
                    key={i}
                    onClick={() => onSelectDag(datum)}
                    className={[
                      'relative cursor-pointer border-r border-zinc-100 last:border-r-0',
                      weekend ? 'bg-zinc-50/50' : 'bg-white',
                      geselecteerd ? 'bg-blue-50/30' : '',
                    ]
                      .filter(Boolean)
                      .join(' ')}
                  >
                    {/* Uur lijnen */}
                    {ZICHTBARE_UREN.map((_, j) => (
                      <div
                        key={j}
                        className="absolute w-full border-t border-zinc-100"
                        style={{ top: j * UUR_HOOGTE }}
                      />
                    ))}

                    {/* TEAM MODE */}
                    {isTeam &&
                      !vrij &&
                      (() => {
                        const enkelvoudig = teamWerknemers.length === 1;

                        return (
                          <div
                            className={`absolute inset-0 ${enkelvoudig ? '' : 'flex'}`}
                          >
                            {teamWerknemers.map((w) => {
                              const wAfwezig = afwezigheden.find((a) => {
                                if (a.werknemerId !== w.id) return false;
                                const start = new Date(a.startDatum);
                                const eind = new Date(a.eindDatum);
                                return datum >= start && datum <= eind;
                              });
                              const wShift = getShiftVoorWerknemer(
                                datum,
                                w.id,
                                teamShiften,
                              );
                              const amStart = tijdDecimaal(
                                wShift?.startTijd,
                                9,
                              );
                              const amEind = tijdDecimaal(
                                wShift?.pauzeStart,
                                12,
                              );
                              const pmStart = tijdDecimaal(
                                wShift?.pauzeEind,
                                13,
                              );
                              const pmEind = tijdDecimaal(wShift?.eindTijd, 17);
                              const isActief =
                                geselecteerdeTeamWerknemer === w.id &&
                                geselecteerdeDag?.toDateString() ===
                                  datum.toDateString();

                              if (enkelvoudig) {
                                // Full-width render met naam + tijden
                                return (
                                  <div
                                    key={w.id}
                                    className="absolute inset-0 cursor-pointer"
                                    onClick={(e) => {
                                      e.stopPropagation();
                                      onSelectTeamWerknemer?.(w.id, datum);
                                    }}
                                  >
                                    {wAfwezig ? (
                                      <div
                                        style={{
                                          top: 8,
                                          height:
                                            (pmEind - amStart) * UUR_HOOGTE,
                                        }}
                                        className="absolute left-0.5 right-0.5 rounded-lg flex items-center justify-center pointer-events-none"
                                      >
                                        <span
                                          className={`text-[9px] font-bold px-2 py-0.5 rounded-full ${wAfwezig.type === 'Ziekte' ? 'bg-red-100 text-red-700' : wAfwezig.status === 'In afwachting' ? 'bg-amber-100 text-amber-700' : 'bg-emerald-100 text-emerald-700'}`}
                                        >
                                          {afwezigheidLabel(wAfwezig)}
                                        </span>
                                      </div>
                                    ) : wShift ? (
                                      <>
                                        <div
                                          style={{
                                            top: amStart * UUR_HOOGTE,
                                            height: Math.max(
                                              4,
                                              (amEind - amStart) * UUR_HOOGTE -
                                                1,
                                            ),
                                          }}
                                          className={`absolute left-0.5 right-0.5 rounded-lg px-1.5 py-1 overflow-hidden ${isActief ? 'bg-rose-400' : 'bg-rose-100 border border-rose-200 hover:bg-rose-200'} transition-colors`}
                                        >
                                          <span
                                            className={`text-[9px] font-bold truncate block ${isActief ? 'text-white' : 'text-rose-700'}`}
                                          >
                                            {formatTijd(amStart)} –{' '}
                                            {formatTijd(amEind)}
                                          </span>
                                          <span
                                            className={`text-[8px] truncate block ${isActief ? 'text-white/70' : 'text-rose-500'}`}
                                          >
                                            {w.voornaam} {w.naam}
                                          </span>
                                        </div>
                                        <div
                                          style={{
                                            top: pmStart * UUR_HOOGTE,
                                            height: Math.max(
                                              4,
                                              (pmEind - pmStart) * UUR_HOOGTE -
                                                1,
                                            ),
                                          }}
                                          className={`absolute left-0.5 right-0.5 rounded-lg px-1.5 py-1 overflow-hidden ${isActief ? 'bg-rose-400' : 'bg-rose-100 border border-rose-200 hover:bg-rose-200'} transition-colors`}
                                        >
                                          <span
                                            className={`text-[9px] font-bold truncate block ${isActief ? 'text-white' : 'text-rose-700'}`}
                                          >
                                            {formatTijd(pmStart)} –{' '}
                                            {formatTijd(pmEind)}
                                          </span>
                                        </div>
                                      </>
                                    ) : null}
                                  </div>
                                );
                              }

                              // Sub-kolom render (meerdere leden)
                              const initials = `${w.voornaam[0]}${w.naam[0]}`;
                              return (
                                <div
                                  key={w.id}
                                  className="flex-1 relative border-l border-zinc-200 first:border-l-0 cursor-pointer"
                                  onClick={(e) => {
                                    e.stopPropagation();
                                    onSelectTeamWerknemer?.(w.id, datum);
                                  }}
                                >
                                  <div className="absolute top-1 inset-x-0 flex justify-center z-10">
                                    <span
                                      className={`text-[7px] font-bold px-1 rounded ${isActief ? 'bg-zinc-800 text-white' : 'text-zinc-400'}`}
                                    >
                                      {initials}
                                    </span>
                                  </div>
                                  {wAfwezig ? (
                                    <div
                                      style={{
                                        top: amStart * UUR_HOOGTE,
                                        height: (pmEind - amStart) * UUR_HOOGTE,
                                      }}
                                      className="absolute inset-x-0.5 rounded flex items-center justify-center pointer-events-none"
                                    >
                                      <div
                                        className={`w-1 h-full rounded-full ${wAfwezig.type === 'Ziekte' ? 'bg-red-300' : wAfwezig.status === 'In afwachting' ? 'bg-amber-300' : 'bg-emerald-300'}`}
                                      />
                                    </div>
                                  ) : wShift ? (
                                    <>
                                      <div
                                        style={{
                                          top: amStart * UUR_HOOGTE,
                                          height: Math.max(
                                            4,
                                            (amEind - amStart) * UUR_HOOGTE - 1,
                                          ),
                                        }}
                                        className={`absolute inset-x-0.5 rounded overflow-hidden ${isActief ? 'bg-rose-400' : 'bg-rose-100 hover:bg-rose-200'} transition-colors`}
                                      >
                                        <span
                                          className={`text-[7px] font-bold px-0.5 truncate block ${isActief ? 'text-white' : 'text-rose-700'}`}
                                        >
                                          {formatTijd(amStart)}
                                        </span>
                                      </div>
                                      <div
                                        style={{
                                          top: pmStart * UUR_HOOGTE,
                                          height: Math.max(
                                            4,
                                            (pmEind - pmStart) * UUR_HOOGTE - 1,
                                          ),
                                        }}
                                        className={`absolute inset-x-0.5 rounded overflow-hidden ${isActief ? 'bg-rose-400' : 'bg-rose-100 hover:bg-rose-200'} transition-colors`}
                                      >
                                        <span
                                          className={`text-[7px] font-bold px-0.5 truncate block ${isActief ? 'text-white' : 'text-rose-700'}`}
                                        >
                                          {formatTijd(pmStart)}
                                        </span>
                                      </div>
                                    </>
                                  ) : null}
                                </div>
                              );
                            })}
                          </div>
                        );
                      })()}

                    {/* PERSONAL MODE: eigen shift blokken */}
                    {!isTeam &&
                      !vrij &&
                      (() => {
                        const dagAfwezigheid = afwezighedenOpDag(
                          afwezigheden,
                          datum,
                        );
                        const isAfwezig = dagAfwezigheid.length > 0;
                        const shift = getShiftVoorDag(datum, eigenShiften);
                        const amStart = tijdDecimaal(shift?.startTijd, 9);
                        const amEind = tijdDecimaal(shift?.pauzeStart, 12);
                        const pmStart = tijdDecimaal(shift?.pauzeEind, 13);
                        const pmEind = tijdDecimaal(shift?.eindTijd, 17);

                        if (isAfwezig) {
                          return (
                            <div
                              style={{
                                top: amStart * UUR_HOOGTE,
                                height: (pmEind - amStart) * UUR_HOOGTE,
                              }}
                              className="absolute left-0.5 right-0.5 rounded-lg flex items-center justify-center z-10 pointer-events-none"
                            >
                              <span
                                className={`text-[9px] font-bold px-2 py-0.5 rounded-full ${badgeKleur(dagAfwezigheid[0])}`}
                              >
                                {afwezigheidLabel(dagAfwezigheid[0])}
                              </span>
                            </div>
                          );
                        }
                        if (!shift) return null;
                        return (
                          <>
                            <button
                              type="button"
                              onClick={(e) => {
                                e.stopPropagation();
                                onNavigeerNaarDag(datum);
                              }}
                              style={{
                                top: amStart * UUR_HOOGTE,
                                height: (amEind - amStart) * UUR_HOOGTE,
                              }}
                              className="absolute left-0.5 right-0.5 rounded-lg bg-rose-100 border border-rose-200 flex flex-col items-start justify-start px-1.5 py-1 overflow-hidden hover:bg-rose-200 transition-colors z-10 cursor-pointer"
                            >
                              <span className="text-[9px] font-bold text-rose-700 truncate w-full">
                                {formatTijd(amStart)} – {formatTijd(amEind)}
                              </span>
                              {shift.werknemerNaam && (
                                <span className="text-[8px] text-rose-500 truncate w-full">
                                  {shift.werknemerNaam}
                                </span>
                              )}
                            </button>
                            <button
                              type="button"
                              onClick={(e) => {
                                e.stopPropagation();
                                onNavigeerNaarDag(datum);
                              }}
                              style={{
                                top: pmStart * UUR_HOOGTE,
                                height: (pmEind - pmStart) * UUR_HOOGTE,
                              }}
                              className="absolute left-0.5 right-0.5 rounded-lg bg-rose-100 border border-rose-200 flex flex-col items-start justify-start px-1.5 py-1 overflow-hidden hover:bg-rose-200 transition-colors z-10 cursor-pointer"
                            >
                              <span className="text-[9px] font-bold text-rose-700 truncate w-full">
                                {formatTijd(pmStart)} – {formatTijd(pmEind)}
                              </span>
                            </button>
                          </>
                        );
                      })()}

                    {/* Huidige tijdlijn */}
                    {isVandaag(datum) &&
                      (() => {
                        const nu = new Date();
                        const minutenSindsStart =
                          (nu.getHours() - START_UUR) * 60 + nu.getMinutes();
                        const top = (minutenSindsStart / 60) * UUR_HOOGTE;
                        if (top < 0 || top > ZICHTBARE_UREN.length * UUR_HOOGTE)
                          return null;
                        return (
                          <div
                            className="absolute w-full z-20 pointer-events-none"
                            style={{ top }}
                          >
                            <div className="w-2 h-2 bg-red-500 rounded-full -ml-1 -mt-1 absolute" />
                            <div className="h-px bg-red-500 w-full" />
                          </div>
                        );
                      })()}

                    {/* Taken (personal mode only) */}
                    {!isTeam &&
                      dagTaken.map((t, j) => {
                        const uur = parseUur(t.deadline);
                        const duur = parseDuur(t.duur);
                        const top = (uur - START_UUR) * UUR_HOOGTE;
                        const hoogte = duur * UUR_HOOGTE;
                        if (
                          top < 0 ||
                          top >= ZICHTBARE_UREN.length * UUR_HOOGTE
                        )
                          return null;
                        return (
                          <div
                            key={`t-${j}`}
                            className={`absolute left-0.5 right-0.5 rounded-lg px-1.5 py-1 overflow-hidden z-20 border-l-2 ${taakBadgeKleur(t)}`}
                            style={{
                              top: Math.max(0, top),
                              height: Math.max(UUR_HOOGTE * 0.5, hoogte - 2),
                            }}
                          >
                            <span className="text-[10px] font-bold block truncate">
                              {t.naam}
                            </span>
                            <span className="text-[9px] block truncate opacity-75">
                              {t.locatie}
                            </span>
                          </div>
                        );
                      })}
                  </div>
                );
              })}
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}
