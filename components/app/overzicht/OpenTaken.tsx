import type { Afwezigheid } from '../planner/types';
import { Container } from '@/components/design-system/Container';

interface OpenTakenProps {
  inAfwachting: Afwezigheid[];
}

export function OpenTaken({ inAfwachting }: OpenTakenProps) {
  return (
    <Container
      label={'Snelle statistieken:'}
      className="col-start-3 col-end-4 row-start-3 row-end-4"
    >
      <div className="flex flex-col gap-2">
        <div className="flex flex-col gap-1.5 mt-1">
          {inAfwachting.length === 0 ? (
            <p className="text-xs text-zinc-400">Geen openstaande taken.</p>
          ) : (
            inAfwachting.slice(0, 4).map((a, i) => (
              <div
                key={i}
                className="flex items-center justify-between px-3 py-2 rounded-xl bg-amber-50/60 border border-amber-100"
              >
                <span className="text-xs font-semibold text-zinc-700 truncate">
                  {a.voornaam} {a.naam}
                </span>
                <span className="text-[10px] font-bold text-amber-600 ml-2 flex-shrink-0">
                  Verlof
                </span>
              </div>
            ))
          )}
          {inAfwachting.length > 4 && (
            <span className="text-[10px] text-zinc-400 font-bold text-center">
              + {inAfwachting.length - 4} meer
            </span>
          )}
        </div>
      </div>
    </Container>
  );
}
