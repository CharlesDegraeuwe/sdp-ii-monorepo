export interface Notificatie {
  id: number;
  werknemerId?: number;
  titel: string;
  bericht: string;
  gelezen: string; // 'Ja' | 'Nee'
  datum: string;
  referentieId?: number | null;
}
