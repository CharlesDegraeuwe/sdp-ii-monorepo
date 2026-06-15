export type View = 'maand' | 'week' | 'dag';

export interface Afwezigheid {
  werknemerId: number;
  voornaam: string;
  naam: string;
  type: string;
  startDatum: string;
  eindDatum: string;
  status: string | null;
}
