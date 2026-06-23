import type { Afwezigheid } from '../planner/types';
import { badgeKleur, afwezigheidLabel } from '../planner/utils';
import { Container } from '@/components/design-system/Container';

interface AfwezighedenWidgetProps {
  afwezigVandaag: Afwezigheid[];
  inAfwachting: Afwezigheid[];
  aantalOngelezen: number;
}

export function AfwezighedenWidget({
  afwezigVandaag,
  inAfwachting,
  aantalOngelezen,
}: AfwezighedenWidgetProps) {
  return (
    <Container indent={true} label={'Afwezigheden'}>
      <div className="flex flex-col gap-3">
        <div className="flex flex-col gap-1.5">
          {afwezigVandaag.length === 0 ? (
            <p className="text-xs text-zinc-400">Niemand afwezig vandaag.</p>
          ) : (
            afwezigVandaag.slice(0, 4).map((a, i) => (
              <div
                key={i}
                className="flex items-center justify-between px-3 py-2 rounded-2xl border border-gray-300/30 bg-gray-300/20"
              >
                <span className="text-xs font-semibold text-zinc-700 truncate">
                  {a.voornaam} {a.naam}
                </span>
                <span
                  className={`text-xs font-semibold px-2 py-0.5 rounded-full ml-2 shrink-0 ${badgeKleur(a)}`}
                >
                  {afwezigheidLabel(a)}
                </span>
              </div>
            ))
          )}
          {afwezigVandaag.length > 4 && (
            <span className="text-xs text-zinc-400 text-center">
              +{afwezigVandaag.length - 4} meer
            </span>
          )}
        </div>

        <div className="flex gap-3 pt-3 border-t border-gray-300/30">
          <div className="flex-1 text-center">
            <p className="text-lg font-bold text-zinc-900">
              {afwezigVandaag.length}
            </p>
            <p className="text-xs text-zinc-400">Afwezig</p>
          </div>
          <div className="flex-1 text-center">
            <p className="text-lg font-bold text-amber-600">
              {inAfwachting.length}
            </p>
            <p className="text-xs text-zinc-400">In afwachting</p>
          </div>
          <div className="flex-1 text-center">
            <p className="text-lg font-bold text-blue-600">{aantalOngelezen}</p>
            <p className="text-xs text-zinc-400">Ongelezen</p>
          </div>
        </div>
      </div>
    </Container>
  );
}
