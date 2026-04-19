import { Container } from '@/components/design system/Container';
import { MONTHS_NL, TYPE_COLORS } from './constants';
import { dayInRange } from './utils';
import type { Afwezigheid } from './types';

interface Props {
  selectedDate: Date;
  afwezigheden: Afwezigheid[];
}

export function DetailPanel({ selectedDate, afwezigheden }: Props) {
  const geselecteerdeAfwezigheden = afwezigheden.filter((a) =>
    dayInRange(selectedDate, a),
  );
  const datumLabel = `${selectedDate.getDate()} ${MONTHS_NL[selectedDate.getMonth()]} ${selectedDate.getFullYear()}`;

  return (
    <div className="hidden lg:flex w-60 xl:w-72 shrink-0 flex-col gap-3 overflow-y-auto">
      <Container label={datumLabel}>
        <p className="text-xs text-gray-400">
          {geselecteerdeAfwezigheden.length === 0
            ? 'Geen afwezigheid'
            : `${geselecteerdeAfwezigheden.length} afwezigheid(en)`}
        </p>
      </Container>

      <Container label="Afwezigheden:">
        <div className="flex flex-col gap-2">
          {geselecteerdeAfwezigheden.length === 0 ? (
            <span className="text-gray-400 text-xs">
              Geen afwezigheid op deze dag
            </span>
          ) : (
            geselecteerdeAfwezigheden.map((a, i) => (
              <div key={i} className="flex items-start gap-2 text-xs">
                <span
                  className={`mt-1 w-2 h-2 rounded-full shrink-0 ${TYPE_COLORS[a.type] ?? 'bg-gray-400'}`}
                />
                <div>
                  <p className="font-medium">
                    {a.voornaam} {a.naam}
                  </p>
                  <p className="text-gray-500">
                    {a.type}
                    {a.status ? ` · ${a.status}` : ''}
                  </p>
                </div>
              </div>
            ))
          )}
        </div>
      </Container>

      <Container label="Shifts vandaag:">
        <span className="text-gray-400 text-xs">Geen shifts gepland</span>
      </Container>

      <Container label="Shift details:">
        <span className="text-gray-400 text-xs">Selecteer een shift</span>
      </Container>
    </div>
  );
}
