import type { Afwezigheid } from '../planner/types';
import { afwezighedenOpDag, isVandaag } from '../planner/utils';
import { DAGEN_KORT, MAANDEN } from '../planner/constants';
import { Container } from '@/components/design-system/Container';

function KalenderGrid({ afwezigheden }: { afwezigheden: Afwezigheid[] }) {
  const vandaag = new Date();
  const eersteVanMaand = new Date(vandaag.getFullYear(), vandaag.getMonth(), 1);
  const startKolom =
    eersteVanMaand.getDay() === 0 ? 6 : eersteVanMaand.getDay() - 1;
  const aantalDagen = new Date(
    vandaag.getFullYear(),
    vandaag.getMonth() + 1,
    0,
  ).getDate();

  const cellen: (Date | null)[] = Array(startKolom).fill(null);
  for (let i = 1; i <= aantalDagen; i++)
    cellen.push(new Date(vandaag.getFullYear(), vandaag.getMonth(), i));
  while (cellen.length % 7 !== 0) cellen.push(null);

  return (
    <div className="flex flex-col gap-1 p-2.5 h-full">
      <span className="text-xs font-bold text-zinc-500 capitalize mb-1">
        {MAANDEN[vandaag.getMonth()]} {vandaag.getFullYear()}
      </span>
      <div className="grid grid-cols-7 mb-0.5">
        {DAGEN_KORT.map((d) => (
          <div
            key={d}
            className="text-center text-[9px] font-bold text-zinc-400 uppercase py-0.5 flex items-center justify-center"
          >
            {d}
          </div>
        ))}
      </div>
      <div className="grid grid-cols-7 gap-0.5 flex-1">
        {cellen.map((datum, i) => {
          if (!datum) return <div key={i} />;
          const opDag = afwezighedenOpDag(afwezigheden, datum);
          const weekend = datum.getDay() === 0 || datum.getDay() === 6;
          const vandaagDag = isVandaag(datum);
          return (
            <div
              key={i}
              className={`relative flex flex-col items-center justify-center
                ${weekend ? 'opacity-40' : ''}
                ${vandaagDag ? 'bg-zinc-900 rounded-full' : ''}`}
            >
              <span
                className={`text-[9px] font-bold ${vandaagDag ? 'text-white' : 'text-zinc-600'}`}
              >
                {datum.getDate()}
              </span>
              {opDag.length > 0 && (
                <div
                  className={`absolute bottom-1 left-1/2 -translate-1/2 w-1 h-1 rounded-full mt-0.5 ${opDag[0].type === 'Ziekte' ? 'bg-red-400' : 'bg-emerald-400'}`}
                />
              )}
            </div>
          );
        })}
      </div>
    </div>
  );
}

export function MiniKalender({
  afwezigheden,
}: {
  afwezigheden: Afwezigheid[];
}) {
  return (
    <Container
      label={'Kalender'}
      className="col-start-3 col-end-4 row-start-1 row-end-3"
    >
      <KalenderGrid afwezigheden={afwezigheden} />
    </Container>
  );
}
