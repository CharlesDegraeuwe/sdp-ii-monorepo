import { MONTHS_NL } from './constants';
import type { Afwezigheid, JavaDate, View } from './types';

export function toIso(d: Date) {
  return `${d.getFullYear()}-${String(d.getMonth() + 1).padStart(2, '0')}-${String(d.getDate()).padStart(2, '0')}`;
}

export function parseDate(raw: JavaDate): Date {
  if (Array.isArray(raw)) return new Date(raw[0], raw[1] - 1, raw[2]);
  return new Date(raw + 'T00:00:00');
}

export function getMonthDays(date: Date): (Date | null)[] {
  const year = date.getFullYear();
  const month = date.getMonth();
  const firstDay = new Date(year, month, 1);
  const lastDay = new Date(year, month + 1, 0);
  let startDow = firstDay.getDay();
  startDow = startDow === 0 ? 6 : startDow - 1;
  const days: (Date | null)[] = [];
  for (let i = 0; i < startDow; i++) days.push(null);
  for (let d = 1; d <= lastDay.getDate(); d++)
    days.push(new Date(year, month, d));
  return days;
}

export function getWeekDays(date: Date): Date[] {
  const start = new Date(date);
  const dow = start.getDay();
  start.setDate(start.getDate() + (dow === 0 ? -6 : 1 - dow));
  return Array.from({ length: 7 }, (_, i) => {
    const d = new Date(start);
    d.setDate(d.getDate() + i);
    return d;
  });
}

export function isSameDay(a: Date, b: Date) {
  return (
    a.getDate() === b.getDate() &&
    a.getMonth() === b.getMonth() &&
    a.getFullYear() === b.getFullYear()
  );
}

export function dayInRange(day: Date, a: Afwezigheid) {
  const start = parseDate(a.startDatum);
  const end = parseDate(a.eindDatum);
  start.setHours(0, 0, 0, 0);
  end.setHours(23, 59, 59, 999);
  return day >= start && day <= end;
}

export function getPeriodBounds(
  view: View,
  date: Date,
): { van: string; tot: string } {
  if (view === 'maand')
    return {
      van: toIso(new Date(date.getFullYear(), date.getMonth(), 1)),
      tot: toIso(new Date(date.getFullYear(), date.getMonth() + 1, 0)),
    };
  if (view === 'week') {
    const days = getWeekDays(date);
    return { van: toIso(days[0]), tot: toIso(days[6]) };
  }
  return { van: toIso(date), tot: toIso(date) };
}

export function getPeriodLabel(view: View, date: Date): string {
  if (view === 'maand')
    return `${MONTHS_NL[date.getMonth()]} ${date.getFullYear()}`;
  if (view === 'week') {
    const days = getWeekDays(date);
    const s = days[0],
      e = days[6];
    return `${s.getDate()} ${MONTHS_NL[s.getMonth()].slice(0, 3)} – ${e.getDate()} ${MONTHS_NL[e.getMonth()].slice(0, 3)} ${e.getFullYear()}`;
  }
  return `${date.getDate()} ${MONTHS_NL[date.getMonth()]} ${date.getFullYear()}`;
}
