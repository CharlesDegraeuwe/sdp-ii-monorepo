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
    <Container indent={true} label={'Afwezigheden'} className="">
      <div className="flex flex-col gap-2">
        <div className="flex flex-col gap-1.5 mt-1">
          {afwezigVandaag.length === 0 ? (
            <p className="text-xs text-zinc-400 px-1 py-0.5">
              Niemand afwezig vandaag.
            </p>
          ) : (
            afwezigVandaag.slice(0, 4).map((a, i) => (
              <div
                key={i}
                className="flex items-center justify-between px-2.5 py-1.5 rounded-xl border border-gray-200/40 bg-white/30"
              >
                <span className="text-xs font-semibold text-zinc-700 truncate">
                  {a.voornaam} {a.naam}
                </span>
                <span
                  className={`text-[10px] font-bold px-2 py-0.5 rounded-full ml-2 flex-shrink-0 ${badgeKleur(a)}`}
                >
                  {afwezigheidLabel(a)}
                </span>
              </div>
            ))
          )}
          {afwezigVandaag.length > 4 && (
            <span className="text-[10px] text-zinc-400 font-bold text-center">
              + {afwezigVandaag.length - 4} meer
            </span>
          )}
        </div>

        <div className="flex gap-3 mt-3 pt-2.5 border-t border-gray-200/40">
          <div className="flex-1 text-center">
            <span className="text-lg font-bold text-zinc-900">
              {afwezigVandaag.length}
            </span>
            <p className="text-[10px] text-zinc-400">Afwezig</p>
          </div>
          <div className="flex-1 text-center">
            <span className="text-lg font-bold text-amber-600">
              {inAfwachting.length}
            </span>
            <p className="text-[10px] text-zinc-400">In afwachting</p>
          </div>
          <div className="flex-1 text-center">
            <span className="text-lg font-bold text-blue-600">
              {aantalOngelezen}
            </span>
            <p className="text-[10px] text-zinc-400">Ongelezen</p>
          </div>
        </div>
      </div>
    </Container>
  );
}
