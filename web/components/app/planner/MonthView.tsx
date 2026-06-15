import type { Afwezigheid, PlannerTaak } from './types';
import { DAGEN_KORT } from './constants';
import {
  afwezighedenOpDag,
  isVandaag,
  badgeKleur,
  takenOpDag,
  taakBadgeKleur,
} from './utils';

interface MonthViewProps {
  huidigeDatum: Date;
  afwezigheden: Afwezigheid[];
  taken: PlannerTaak[];
  geselecteerdeDag: Date | null;
  onSelectDag: (datum: Date) => void;
}

export default function MonthView({
  huidigeDatum,
  afwezigheden,
  taken,
  geselecteerdeDag,
  onSelectDag,
}: MonthViewProps) {
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
  for (let i = 1; i <= aantalDagen; i++)
    cellen.push(
      new Date(huidigeDatum.getFullYear(), huidigeDatum.getMonth(), i),
    );
  while (cellen.length % 7 !== 0) cellen.push(null);

  return (
    <div className="flex flex-col gap-1 w-full h-full">
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

          const opDag = afwezighedenOpDag(afwezigheden, datum);
          const dagTaken = takenOpDag(taken, datum).filter((t) => !t.afgewerkt);
          const items = [
            ...opDag.slice(0, 2),
            ...dagTaken.slice(0, 2 - Math.min(opDag.length, 2)),
          ];
          const totaal = opDag.length + dagTaken.length;
          const weekend = datum.getDay() === 0 || datum.getDay() === 6;
          const geselecteerd =
            geselecteerdeDag &&
            datum.toDateString() === geselecteerdeDag.toDateString();

          return (
            <div
              key={i}
              onClick={() => onSelectDag(datum)}
              className={`min-h-25 rounded-2xl p-2 cursor-pointer transition-all duration-200 border
                ${weekend ? 'bg-zinc-50 border-zinc-100' : 'bg-white border-gray-200/50'}
                ${isVandaag(datum) ? 'border-zinc-900 border-2' : ''}
                ${geselecteerd ? 'ring-2 ring-zinc-400' : ''}
                hover:shadow-md hover:border-zinc-300`}
            >
              <span
                className={`text-xs font-bold ${isVandaag(datum) ? 'text-zinc-900' : 'text-zinc-500'}`}
              >
                {datum.getDate()}
              </span>
              <div className="flex flex-col gap-0.5 mt-1 h-fit">
                {opDag.slice(0, 2).map((a, j) => (
                  <span
                    key={`a-${j}`}
                    className={`text-[9px] font-bold px-1.5 py-0.5 rounded-full truncate ${badgeKleur(a)}`}
                  >
                    {a.voornaam}
                  </span>
                ))}
                {dagTaken
                  .slice(0, 2 - Math.min(opDag.length, 2))
                  .map((t, j) => (
                    <span
                      key={`t-${j}`}
                      className={`text-[9px] font-bold px-1.5 py-0.5 rounded-full truncate ${taakBadgeKleur(t)}`}
                    >
                      {t.naam}
                    </span>
                  ))}
                {totaal > 2 && (
                  <span className="text-[9px] text-zinc-400 font-bold">
                    +{totaal - items.length}
                  </span>
                )}
              </div>
            </div>
          );
        })}
      </div>
    </div>
  );
}
