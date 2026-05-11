function pasen(jaar: number): Date {
  const a = jaar % 19;
  const b = Math.floor(jaar / 100);
  const c = jaar % 100;
  const d = Math.floor(b / 4);
  const e = b % 4;
  const f = Math.floor((b + 8) / 25);
  const g = Math.floor((b - f + 1) / 3);
  const h = (19 * a + b - d - g + 15) % 30;
  const i = Math.floor(c / 4);
  const k = c % 4;
  const l = (32 + 2 * e + 2 * i - h - k) % 7;
  const m = Math.floor((a + 11 * h + 22 * l) / 451);
  const month = Math.floor((h + l - 7 * m + 114) / 31) - 1;
  const day = ((h + l - 7 * m + 114) % 31) + 1;
  return new Date(jaar, month, day);
}

function feestdagNaam(d: Date): string | null {
  const dag = d.getDate();
  const maand = d.getMonth();
  const jaar = d.getFullYear();

  const vaste: [number, number, string][] = [
    [1, 0, 'Nieuwjaar'],
    [1, 4, 'Dag van de Arbeid'],
    [21, 6, 'Nationale Feestdag'],
    [15, 7, 'O.-L.-V. Hemelvaart'],
    [1, 10, 'Allerheiligen'],
    [11, 10, 'Wapenstilstand'],
    [25, 11, 'Kerstmis'],
  ];
  for (const [fd, fm, naam] of vaste) {
    if (dag === fd && maand === fm) return naam;
  }

  const paaszondag = pasen(jaar);
  const variabele: [number, string][] = [
    [0, 'Paaszondag'],
    [1, 'Paasmaandag'],
    [39, 'Hemelvaartsdag'],
    [50, 'Pinkstermaandag'],
  ];
  for (const [offset, naam] of variabele) {
    const f = new Date(paaszondag);
    f.setDate(f.getDate() + offset);
    if (dag === f.getDate() && maand === f.getMonth()) return naam;
  }

  return null;
}

export function isBelgischeFestdag(d: Date): boolean {
  return feestdagNaam(d) !== null;
}

export function isWeekend(d: Date): boolean {
  return d.getDay() === 0 || d.getDay() === 6;
}

export function isVrij(d: Date): boolean {
  return isWeekend(d) || isBelgischeFestdag(d);
}

export function vrijReden(d: Date): string {
  const dagNr = d.getDay();
  if (dagNr === 6 || dagNr === 0) return 'Weekend';
  return feestdagNaam(d) ?? '';
}
