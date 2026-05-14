import type { Afwezigheid, PlannerTaak } from './types';
import { DAGEN_KORT, UREN } from './constants';
import {
  afwezighedenOpDag,
  getMaandag,
  isVandaag,
  badgeKleur,
  afwezigheidLabel,
  takenOpDag,
  taakBadgeKleur,
} from './utils';

interface WeekViewProps {
  huidigeDatum: Date;
  afwezigheden: Afwezigheid[];
  taken: PlannerTaak[];
  geselecteerdeDag: Date | null;
  onSelectDag: (datum: Date) => void;
}

const START_UUR = 0;
const EIND_UUR = 24;
const ZICHTBARE_UREN = UREN.slice(START_UUR, EIND_UUR);
const UUR_HOOGTE = 60; // px per uur

function parseUur(datum: string): number {
  const d = new Date(datum);
  return d.getHours() + d.getMinutes() / 60;
}

function parseDuur(duur?: string): number {
  if (!duur) return 1;
  const urenMatch = duur.match(/(\d+)\s*u/i);
  const minMatch = duur.match(/(\d+)\s*m/i);
  let totaal = 0;
  if (urenMatch) totaal += parseInt(urenMatch[1]);
  if (minMatch) totaal += parseInt(minMatch[1]) / 60;
  return totaal || 1;
}

export default function WeekView({
  huidigeDatum,
  afwezigheden,
  taken,
  geselecteerdeDag,
  onSelectDag,
}: WeekViewProps) {
  const maandag = getMaandag(huidigeDatum);
  const dagen = Array.from({ length: 7 }, (_, i) => {
    const d = new Date(maandag);
    d.setDate(d.getDate() + i);
    return d;
  });

  return (
    <div className="flex flex-col h-full min-h-0 overflow-x-auto scroll-hidden">
      <div className="min-w-[500px] flex flex-col h-full min-h-0">
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
                  className={`flex flex-col cursor-pointer p-2 transition-colors
                                    ${weekend ? 'bg-zinc-50' : 'bg-white'}
                                    ${geselecteerd ? 'bg-zinc-100' : ''}
                                `}
                >
                  <div className="flex flex-col items-center mb-1">
                    <span className="text-[10px] font-bold text-zinc-400 uppercase">
                      {DAGEN_KORT[i]}
                    </span>
                    <span
                      className={`text-lg font-bold w-9 h-9 flex items-center justify-center rounded-full
                                            ${isVandaag(datum) ? 'bg-zinc-900 text-white' : 'text-zinc-600'}
                                        `}
                    >
                      {datum.getDate()}
                    </span>
                  </div>

                  {opDag.length > 0 ? (
                    <div className="flex flex-col gap-0.5 max-h-7 min-h-7">
                      {opDag.map((a, j) => (
                        <div
                          key={`a-${j}`}
                          className={`rounded px-1.5 py-0.5 ${badgeKleur(a)}`}
                        >
                          <span className="text-[9px] font-bold block truncate">
                            {a.voornaam} {a.naam}
                          </span>
                          <span className="text-[8px] block truncate">
                            {afwezigheidLabel(a)}
                          </span>
                        </div>
                      ))}
                    </div>
                  ) : (
                    <div className={'max-h-7 min-h-7'} />
                  )}
                </div>
              );
            })}
          </div>
        </div>

        <div className="flex-1 overflow-y-scroll scroll-hidden min-h-0 pb-10 rounded-2xl overflow-hidden">
          <div
            className="flex"
            style={{ height: ZICHTBARE_UREN.length * UUR_HOOGTE }}
          >
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
            <div className="grid grid-cols-7 flex-1 gap-px relative">
              {dagen.map((datum, i) => {
                const dagTaken = takenOpDag(taken, datum).filter(
                  (t) => !t.afgewerkt,
                );
                const weekend = datum.getDay() === 0 || datum.getDay() === 6;
                const geselecteerd =
                  geselecteerdeDag &&
                  datum.toDateString() === geselecteerdeDag.toDateString();

                return (
                  <div
                    key={i}
                    onClick={() => onSelectDag(datum)}
                    className={`relative cursor-pointer border-r border-zinc-100 last:border-r-0
                                        ${weekend ? 'bg-zinc-50/50' : 'bg-white'}
                                        ${geselecteerd ? 'bg-blue-50/30' : ''}
                                    `}
                  >
                    {/* Uurlijnen */}
                    {ZICHTBARE_UREN.map((_, j) => (
                      <div
                        key={j}
                        className="absolute w-full border-t border-zinc-100"
                        style={{ top: j * UUR_HOOGTE }}
                      />
                    ))}

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

                    {/* Taken op hun tijdstip */}
                    {dagTaken.map((t, j) => {
                      const uur = parseUur(t.deadline);
                      const duur = parseDuur(t.duur);
                      const top = (uur - START_UUR) * UUR_HOOGTE;
                      const hoogte = duur * UUR_HOOGTE;

                      if (top < 0 || top >= ZICHTBARE_UREN.length * UUR_HOOGTE)
                        return null;

                      return (
                        <div
                          key={`t-${j}`}
                          className={`absolute left-0.5 right-0.5 rounded-lg px-1.5 py-1 overflow-hidden z-10 border-l-2 ${taakBadgeKleur(t)}`}
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
