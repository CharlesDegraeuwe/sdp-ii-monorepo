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

export interface PlannerTaak {
  id: number;
  naam: string;
  specificaties?: string;
  deadline: string;
  duur?: string;
  locatie: string;
  belangrijk: boolean;
  afgewerkt: boolean;
  afgewerktOp?: string;
  werknemerId?: number;
}
