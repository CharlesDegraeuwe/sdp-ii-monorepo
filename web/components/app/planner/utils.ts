import type { Afwezigheid, PlannerTaak, View } from './types';
import { MAANDEN } from './constants';
export {
  isBelgischeFestdag,
  isWeekend,
  isVrij,
  vrijReden,
} from './belgischCalendar';

export function datumNaarString(datum: Date): string {
  const j = datum.getFullYear();
  const m = String(datum.getMonth() + 1).padStart(2, '0');
  const d = String(datum.getDate()).padStart(2, '0');
  return `${j}-${m}-${d}`;
}

export function afwezighedenOpDag(
  afwezigheden: Afwezigheid[],
  datum: Date,
): Afwezigheid[] {
  return afwezigheden.filter((a) => {
    const start = new Date(a.startDatum);
    const eind = new Date(a.eindDatum);
    return datum >= start && datum <= eind;
  });
}

export function formatDag(d: Date): string {
  return d.toLocaleDateString('nl-BE', {
    day: 'numeric',
    month: 'long',
    year: 'numeric',
  });
}

export function isVandaag(d: Date): boolean {
  const v = new Date();
  return (
    d.getDate() === v.getDate() &&
    d.getMonth() === v.getMonth() &&
    d.getFullYear() === v.getFullYear()
  );
}

export function getMaandag(d: Date): Date {
  const ma = new Date(d);
  const dag = ma.getDay() === 0 ? 6 : ma.getDay() - 1;
  ma.setDate(ma.getDate() - dag);
  return ma;
}

export function periodeLabel(view: View, huidigeDatum: Date): string {
  if (view === 'maand')
    return `${MAANDEN[huidigeDatum.getMonth()]} ${huidigeDatum.getFullYear()}`;

  if (view === 'week') {
    const ma = getMaandag(huidigeDatum);
    const zo = new Date(ma);
    zo.setDate(zo.getDate() + 6);
    return `${ma.getDate()} ${MAANDEN[ma.getMonth()]} – ${zo.getDate()} ${MAANDEN[zo.getMonth()]} ${zo.getFullYear()}`;
  }

  return formatDag(huidigeDatum);
}

export function badgeKleur(a: Afwezigheid): string {
  if (a.type === 'Ziekte') return 'bg-red-100 text-red-600';
  if (a.status === 'In afwachting') return 'bg-amber-100 text-amber-700';
  return 'bg-emerald-100 text-emerald-700';
}

export function rijKleur(a: Afwezigheid): string {
  if (a.type === 'Ziekte') return 'bg-red-50/60 border-red-100';
  if (a.status === 'In afwachting') return 'bg-amber-50/60 border-amber-100';
  return 'bg-emerald-50/60 border-emerald-100';
}

export function afwezigheidLabel(a: Afwezigheid): string {
  if (a.type === 'Ziekte') return 'Ziekte';
  if (a.status === 'In afwachting') return 'Verlof (wachten)';
  return 'Verlof';
}

export function getPeriodBounds(
  view: View,
  currentDate: Date,
): { van: string; tot: string } {
  const van = new Date(currentDate.getFullYear(), currentDate.getMonth() - 3, 1)
    .toISOString()
    .split('T')[0];
  const tot = new Date(currentDate.getFullYear(), currentDate.getMonth() + 9, 0)
    .toISOString()
    .split('T')[0];
  return { van, tot };
}

export function takenOpDag(taken: PlannerTaak[], datum: Date): PlannerTaak[] {
  return taken.filter((t) => {
    if (!t.deadline) return false;
    const datumStr = t.deadline.split('T')[0];
    const parts = datumStr.split('-');
    if (parts.length < 3) return false;
    return (
      parseInt(parts[2]) === datum.getDate() &&
      parseInt(parts[1]) - 1 === datum.getMonth() &&
      parseInt(parts[0]) === datum.getFullYear()
    );
  });
}

export function mapTaakVanBackend(dto: Record<string, unknown>): PlannerTaak {
  const werknemer = dto.werknemer as Record<string, unknown> | undefined;
  return {
    id: dto.id as number,
    naam: (dto.titel as string) ?? '',
    specificaties: (dto.beschrijving as string) || undefined,
    deadline: (dto.deadline as string) ?? '',
    duur: undefined,
    locatie: dto.siteId != null ? String(dto.siteId) : '',
    belangrijk: false,
    afgewerkt: dto.afgewerkt === 'ja',
    werknemerId: werknemer?.id != null ? (werknemer.id as number) : undefined,
  };
}

export function taakBadgeKleur(t: PlannerTaak): string {
  if (t.afgewerkt) return 'bg-zinc-100 text-zinc-500';
  if (t.belangrijk) return 'bg-rose-100 text-rose-700';
  return 'bg-blue-100 text-blue-700';
}
