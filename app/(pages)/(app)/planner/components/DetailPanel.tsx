import type { Afwezigheid } from './types';
import {
  afwezighedenOpDag,
  formatDag,
  rijKleur,
  badgeKleur,
  afwezigheidLabel,
} from './utils';

interface DetailPanelProps {
  geselecteerdeDag: Date;
  afwezigheden: Afwezigheid[];
}

export default function DetailPanel({
  geselecteerdeDag,
  afwezigheden,
}: DetailPanelProps) {
  const opDag = afwezighedenOpDag(afwezigheden, geselecteerdeDag);

  return (
    <div className="bg-white border border-gray-200/50 rounded-2xl p-5 flex flex-col gap-3 min-w-80">
      <span className="text-sm font-bold text-zinc-900">
        {formatDag(geselecteerdeDag)}
      </span>

      {opDag.length === 0 && (
        <p className="text-xs text-zinc-400">Geen afwezigheden.</p>
      )}

      {opDag.map((a, i) => (
        <div
          key={i}
          className={`flex flex-col gap-0.5 px-3 py-2.5 rounded-xl border ${rijKleur(a)}`}
        >
          <span className="text-xs font-bold text-zinc-800">
            {a.voornaam} {a.naam}
          </span>
          <span
            className={`text-[10px] font-bold w-fit px-1.5 py-0.5 rounded-full ${badgeKleur(a)}`}
          >
            {afwezigheidLabel(a)}
          </span>
        </div>
      ))}
    </div>
  );
}
