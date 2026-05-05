import type { Afwezigheid, PlannerTaak } from './types';
import {
  afwezighedenOpDag,
  formatDag,
  rijKleur,
  badgeKleur,
  afwezigheidLabel,
  takenOpDag,
  taakBadgeKleur,
} from './utils';
import { Container } from '@/components/design system/Container';

interface DetailPanelProps {
  geselecteerdeDag: Date;
  afwezigheden: Afwezigheid[];
  taken: PlannerTaak[];
}

export default function DetailPanel({
  geselecteerdeDag,
  afwezigheden,
  taken,
}: DetailPanelProps) {
  const opDag = afwezighedenOpDag(afwezigheden, geselecteerdeDag);
  const dagTaken = takenOpDag(taken, geselecteerdeDag).filter(
    (t) => !t.afgewerkt,
  );

  return (
    <div className={'w-80'}>
      <Container width={'80'}>
        <span className="text-sm font-bold text-zinc-900">
          {formatDag(geselecteerdeDag)}
        </span>

        {opDag.length === 0 && dagTaken.length === 0 && (
          <p className="text-xs text-zinc-400">Niets gepland.</p>
        )}

        {dagTaken.map((t) => (
          <div
            key={`t-${t.id}`}
            className={`flex flex-col gap-0.5 px-3 py-2.5 rounded-xl border ${
              t.belangrijk
                ? 'bg-rose-50/60 border-rose-100'
                : 'bg-blue-50/60 border-blue-100'
            }`}
          >
            <span className="text-xs font-bold text-zinc-800">{t.naam}</span>
            <span
              className={`text-[10px] font-bold w-fit px-1.5 py-0.5 rounded-full ${taakBadgeKleur(t)}`}
            >
              {t.belangrijk ? 'Belangrijk' : 'Taak'}
            </span>
          </div>
        ))}

        {opDag.map((a, i) => (
          <div
            key={`a-${i}`}
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
      </Container>
    </div>
  );
}
