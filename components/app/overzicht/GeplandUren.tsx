import type { Afwezigheid } from '../planner/types';
import { afwezighedenOpDag, badgeKleur, isVandaag } from '../planner/utils';
import { Container } from '@/components/design-system/Container';

interface GeplandUrenProps {
  afwezigheden: Afwezigheid[];
}

export function GeplandUren({ afwezigheden }: GeplandUrenProps) {
  const vandaag = new Date();
  const komendeDagen: Date[] = Array.from({ length: 7 }, (_, i) => {
    const d = new Date(vandaag);
    d.setDate(vandaag.getDate() + i);
    return d;
  });

  return (
    <Container
      label={'Geplande Uren'}
      className="col-start-1 col-end-3 row-start-2 row-end-6"
    >
      <div className="flex flex-col gap-2 p-4 h-full overflow-y-auto">
        {komendeDagen.map((dag, i) => {
          const opDag = afwezighedenOpDag(afwezigheden, dag);
          const dagNaam = dag.toLocaleDateString('nl-BE', {
            weekday: 'long',
          });
          const dagNummer = dag.toLocaleDateString('nl-BE', {
            day: 'numeric',
            month: 'short',
          });
          const weekend = dag.getDay() === 0 || dag.getDay() === 6;

          return (
            <div
              key={i}
              className={`flex flex-col gap-1.5 px-3 py-2.5 rounded-2xl border transition-all duration-200
                  ${isVandaag(dag) ? 'border-zinc-900 bg-bg-white' : 'border-gray-300/40 bg-bg-white/30'}
                  ${weekend ? 'opacity-50' : ''}`}
            >
              <div className="flex items-center justify-between">
                <span
                  className={`text-xs font-bold capitalize ${isVandaag(dag) ? 'text-zinc-900' : 'text-zinc-500'}`}
                >
                  {dagNaam}
                </span>
                <span className="text-[10px] text-zinc-400">{dagNummer}</span>
              </div>
              {opDag.length === 0 ? (
                <span className="text-[10px] text-zinc-500">
                  Niemand afwezig
                </span>
              ) : (
                <div className="flex flex-wrap gap-1">
                  {opDag.map((a, j) => (
                    <span
                      key={j}
                      className={`text-[10px] font-bold px-2 py-0.5 rounded-full ${badgeKleur(a)}`}
                    >
                      {a.voornaam} {a.naam}
                    </span>
                  ))}
                </div>
              )}
            </div>
          );
        })}
      </div>
    </Container>
  );
}
