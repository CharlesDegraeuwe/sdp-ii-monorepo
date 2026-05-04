import type { Afwezigheid } from './types';
import { DAGEN_KORT } from './constants';
import {
  afwezighedenOpDag,
  getMaandag,
  isVandaag,
  badgeKleur,
  afwezigheidLabel,
} from './utils';

interface WeekViewProps {
  huidigeDatum: Date;
  afwezigheden: Afwezigheid[];
  geselecteerdeDag: Date | null;
  onSelectDag: (datum: Date) => void;
}

export default function WeekView({
  huidigeDatum,
  afwezigheden,
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
    <div className="grid grid-cols-7 gap-2 min-h-64">
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
            className={`flex flex-col gap-2 rounded-2xl p-3 cursor-pointer transition-all duration-200 border
              ${weekend ? 'bg-zinc-50 border-zinc-100' : 'bg-white border-gray-200/50'}
              ${isVandaag(datum) ? 'border-zinc-900 border-2' : ''}
              ${geselecteerd ? 'ring-2 ring-zinc-400' : ''}
              hover:shadow-md`}
          >
            <div className="flex flex-col">
              <span className="text-xs font-bold text-zinc-400 uppercase">
                {DAGEN_KORT[i]}
              </span>
              <span
                className={`text-lg font-bold ${isVandaag(datum) ? 'text-zinc-900' : 'text-zinc-600'}`}
              >
                {datum.getDate()}
              </span>
            </div>

            {opDag.length === 0 && (
              <span className="text-xs text-zinc-300">–</span>
            )}

            {opDag.map((a, j) => (
              <div
                key={j}
                className={`rounded-xl px-2 py-1.5 h-full ${badgeKleur(a)}`}
              >
                <span className="text-[10px] font-bold block truncate">
                  {a.voornaam} {a.naam}
                </span>
                <span className="text-[9px] block">{afwezigheidLabel(a)}</span>
              </div>
            ))}
          </div>
        );
      })}
    </div>
  );
}
