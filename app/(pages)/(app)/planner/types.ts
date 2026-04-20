export type View = 'maand' | 'week' | 'dag';

export type JavaDate = string | number[];

export interface Afwezigheid {
  werknemerId: number;
  voornaam: string;
  naam: string;
  type: string;
  startDatum: JavaDate;
  eindDatum: JavaDate;
  status: string | null;
}
