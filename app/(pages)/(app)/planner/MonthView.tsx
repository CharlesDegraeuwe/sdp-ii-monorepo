import { Container } from '@/components/design system/Container';
import { DAYS_NL, TYPE_COLORS } from './constants';
import { getMonthDays, isSameDay, dayInRange } from './utils';
import type { Afwezigheid } from './types';

interface Props {
  currentDate: Date;
  selectedDate: Date;
  today: Date;
  afwezigheden: Afwezigheid[];
  onSelectDay: (day: Date) => void;
}

export function MonthView({
  currentDate,
  selectedDate,
  today,
  afwezigheden,
  onSelectDay,
}: Props) {
  const afwezighedenOpDag = (day: Date) =>
    afwezigheden.filter((a) => dayInRange(day, a));

  return (
    <Container className="h-full">
      <div className="p-3 h-full flex flex-col">
        <div className="grid grid-cols-7 mb-1">
          {DAYS_NL.map((d) => (
            <div
              key={d}
              className="text-center text-xs font-semibold text-gray-500 py-1.5"
            >
              {d}
            </div>
          ))}
        </div>
        <div className="grid grid-cols-7 gap-1 flex-1">
          {getMonthDays(currentDate).map((day, i) => {
            const dagAfwezigheid = day ? afwezighedenOpDag(day) : [];
            const isSelected = day ? isSameDay(day, selectedDate) : false;
            const isVandaag = day ? isSameDay(day, today) : false;

            return (
              <div
                key={i}
                onClick={() => day && onSelectDay(day)}
                className={`flex flex-col items-center justify-center gap-0.5 rounded-lg text-sm transition-all select-none
                  ${day ? 'cursor-pointer' : ''}
                  ${
                    isSelected
                      ? 'bg-delaware_red text-white'
                      : dagAfwezigheid.length > 0
                        ? 'bg-delaware_red/15 hover:bg-delaware_red/25 text-delaware_red font-medium'
                        : 'hover:bg-gray-400/30'
                  }
                  ${isVandaag && !isSelected ? 'ring-2 ring-delaware_red font-bold' : ''}
                  ${isVandaag && isSelected ? 'ring-2 ring-white' : ''}
                `}
              >
                {day?.getDate()}
                {dagAfwezigheid.length > 0 && (
                  <div className="flex gap-0.5">
                    {dagAfwezigheid.slice(0, 3).map((a, j) => (
                      <span
                        key={j}
                        className={`w-1 h-1 rounded-full ${isSelected ? 'bg-white' : (TYPE_COLORS[a.type] ?? 'bg-gray-400')}`}
                      />
                    ))}
                  </div>
                )}
              </div>
            );
          })}
        </div>
      </div>
    </Container>
  );
}
