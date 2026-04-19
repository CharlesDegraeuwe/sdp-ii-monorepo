import { Fragment } from 'react';
import { Container } from '@/components/design system/Container';
import { DAYS_NL, HOURS, TYPE_COLORS } from './constants';
import { getWeekDays, isSameDay, dayInRange } from './utils';
import type { Afwezigheid } from './types';

interface Props {
  currentDate: Date;
  selectedDate: Date;
  today: Date;
  afwezigheden: Afwezigheid[];
  onSelectDay: (day: Date) => void;
}

export function WeekView({
  currentDate,
  selectedDate,
  today,
  afwezigheden,
  onSelectDay,
}: Props) {
  const weekDays = getWeekDays(currentDate);
  const afwezighedenOpDag = (day: Date) =>
    afwezigheden.filter((a) => dayInRange(day, a));

  return (
    <Container className="h-full">
      <div className="h-full overflow-auto">
        <div className="grid grid-cols-8 min-w-[500px]">
          <div className="sticky top-0 z-10 bg-white/5" />
          {weekDays.map((day, i) => {
            const dagAfwezigheid = afwezighedenOpDag(day);
            return (
              <div
                key={i}
                onClick={() => onSelectDay(day)}
                className={`sticky top-0 z-10 text-center py-2 text-xs font-semibold cursor-pointer select-none backdrop-blur-sm rounded-xl
                  ${isSameDay(day, selectedDate) || dagAfwezigheid.length > 0 ? 'bg-delaware_red/10' : ''}
                  ${isSameDay(day, today) ? 'text-delaware_red' : 'text-gray-600'}
                `}
              >
                <div>{DAYS_NL[i]}</div>
                <div
                  className={`text-sm mt-0.5 ${isSameDay(day, today) ? 'font-bold' : ''}`}
                >
                  {day.getDate()}
                </div>
                {dagAfwezigheid.length > 0 && (
                  <div className="flex justify-center gap-0.5 mt-0.5">
                    {dagAfwezigheid.slice(0, 3).map((a, j) => (
                      <span
                        key={j}
                        className={`w-1 h-1 rounded-full ${TYPE_COLORS[a.type] ?? 'bg-gray-400'}`}
                      />
                    ))}
                  </div>
                )}
              </div>
            );
          })}
          {HOURS.map((hour) => (
            <Fragment key={hour}>
              <div className="text-xs text-gray-400 pr-2 text-right py-2 border-t border-gray-200/30 leading-tight">
                {String(hour).padStart(2, '0')}:00
              </div>
              {weekDays.map((_, di) => (
                <div key={di} className="border-t border-gray-200/30 h-10" />
              ))}
            </Fragment>
          ))}
        </div>
      </div>
    </Container>
  );
}
