import type { Afwezigheid } from './components/types';
import { afwezighedenOpDag, badgeKleur, rijKleur } from './components/utils';

interface DayViewProps {
  huidigeDatum: Date;
  afwezigheden: Afwezigheid[];
}

export default function DayView({ huidigeDatum, afwezigheden }: DayViewProps) {
  const opDag = afwezighedenOpDag(afwezigheden, huidigeDatum);

  return (
    <div className="flex flex-col gap-3">
      {opDag.length === 0 && (
        <p className="text-sm text-zinc-400">Geen afwezigheden op deze dag.</p>
      )}

      {opDag.map((a, i) => (
        <div
          key={i}
          className={`flex items-center justify-between px-4 py-3 rounded-2xl border ${rijKleur(a)}`}
        >
          <div className="flex flex-col gap-0.5">
            <span className="text-sm font-bold text-zinc-900">
              {a.voornaam} {a.naam}
            </span>
            <span className="text-xs text-zinc-500">
              {new Date(a.startDatum).toLocaleDateString('nl-BE', {
                day: 'numeric',
                month: 'short',
              })}
              {a.startDatum !== a.eindDatum &&
                ` – ${new Date(a.eindDatum).toLocaleDateString('nl-BE', {
                  day: 'numeric',
                  month: 'short',
                })}`}
            </span>
          </div>

          <div className="flex items-center gap-2">
            <span
              className={`text-xs font-bold px-2 py-1 rounded-full ${badgeKleur(a)}`}
            >
              {a.type}
            </span>
            {a.status && (
              <span
                className={`text-xs font-bold px-2 py-1 rounded-full ${badgeKleur(a)}`}
              >
                {a.status}
              </span>
            )}
          </div>
        </div>
      ))}
    </div>
  );
}
