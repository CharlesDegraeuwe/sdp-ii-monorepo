export const VIS_START = 6;
export const VIS_END = 22;
export const UUR_BREEDTE = 64;
export const ROW_H = 60;
export const NAAM_B = 148;
export const TIMELINE_W = (VIS_END - VIS_START) * UUR_BREEDTE;
export const HOUR_LABELS = Array.from(
  { length: VIS_END - VIS_START + 1 },
  (_, i) => VIS_START + i,
);

export const STANDAARD_TIJDEN = {
  startTijd: '09:00',
  eindTijd: '17:00',
  pauzeStart: '12:00',
  pauzeEind: '12:30',
} as const;

export interface Rij {
  werknemerId: number;
  label: string;
  teamNaam: string;
}

export interface ModalForm {
  shiftId: number | null;
  werknemerId: number;
  werknemerNaam: string;
  startDatum: string;
  eindDatum: string;
  startTijd: string;
  eindTijd: string;
  pauzeStart: string;
  pauzeEind: string;
}

export function tijdDecimaal(tijd: string): number {
  const [h, m] = tijd.split(':').map(Number);
  return h + m / 60;
}

export function toTimeInput(tijd: string | null): string {
  if (!tijd) return '';
  return tijd.substring(0, 5);
}

export function toBackendTime(t: string): string {
  return t.length === 5 ? t + ':00' : t;
}
