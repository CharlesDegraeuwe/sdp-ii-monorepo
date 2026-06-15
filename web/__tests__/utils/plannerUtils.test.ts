import { describe, it, expect } from 'vitest';
import {
  afwezighedenOpDag,
  isVandaag,
  getMaandag,
  periodeLabel,
  badgeKleur,
  rijKleur,
  afwezigheidLabel,
  getPeriodBounds,
  takenOpDag,
  taakBadgeKleur,
} from '@/components/app/planner/utils';
import type { Afwezigheid, PlannerTaak } from '@/components/app/planner/types';

const maakAfwezigheid = (
  overrides: Partial<Afwezigheid> = {},
): Afwezigheid => ({
  werknemerId: 1,
  voornaam: 'Jan',
  naam: 'Janssen',
  type: 'Verlof',
  startDatum: '2025-06-01',
  eindDatum: '2025-06-05',
  status: 'Goedgekeurd',
  ...overrides,
});

const maakTaak = (overrides: Partial<PlannerTaak> = {}): PlannerTaak => ({
  id: 1,
  naam: 'Test taak',
  deadline: '2025-06-03',
  locatie: 'Locatie A',
  belangrijk: false,
  afgewerkt: false,
  ...overrides,
});

describe('afwezighedenOpDag', () => {
  it('geeft afwezigheid terug die de dag omvat', () => {
    const a = maakAfwezigheid({
      startDatum: '2025-06-01',
      eindDatum: '2025-06-05',
    });
    const result = afwezighedenOpDag([a], new Date('2025-06-03'));
    expect(result).toHaveLength(1);
  });

  it('geeft afwezigheid terug op startdatum', () => {
    const a = maakAfwezigheid({
      startDatum: '2025-06-01',
      eindDatum: '2025-06-05',
    });
    const result = afwezighedenOpDag([a], new Date('2025-06-01'));
    expect(result).toHaveLength(1);
  });

  it('geeft afwezigheid terug op einddatum', () => {
    const a = maakAfwezigheid({
      startDatum: '2025-06-01',
      eindDatum: '2025-06-05',
    });
    const result = afwezighedenOpDag([a], new Date('2025-06-05'));
    expect(result).toHaveLength(1);
  });

  it('geeft lege array terug voor dag buiten de periode', () => {
    const a = maakAfwezigheid({
      startDatum: '2025-06-01',
      eindDatum: '2025-06-05',
    });
    const result = afwezighedenOpDag([a], new Date('2025-06-10'));
    expect(result).toHaveLength(0);
  });

  it('geeft lege array terug bij lege input', () => {
    expect(afwezighedenOpDag([], new Date('2025-06-03'))).toHaveLength(0);
  });

  it('filtert correct uit meerdere afwezigheden', () => {
    const a1 = maakAfwezigheid({
      startDatum: '2025-06-01',
      eindDatum: '2025-06-05',
    });
    const a2 = maakAfwezigheid({
      startDatum: '2025-06-10',
      eindDatum: '2025-06-15',
    });
    const result = afwezighedenOpDag([a1, a2], new Date('2025-06-03'));
    expect(result).toHaveLength(1);
    expect(result[0]).toBe(a1);
  });
});

describe('isVandaag', () => {
  it('geeft true voor vandaag', () => {
    expect(isVandaag(new Date())).toBe(true);
  });

  it('geeft false voor gisteren', () => {
    const gisteren = new Date();
    gisteren.setDate(gisteren.getDate() - 1);
    expect(isVandaag(gisteren)).toBe(false);
  });

  it('geeft false voor morgen', () => {
    const morgen = new Date();
    morgen.setDate(morgen.getDate() + 1);
    expect(isVandaag(morgen)).toBe(false);
  });

  it('geeft false voor vorig jaar', () => {
    const vorigJaar = new Date();
    vorigJaar.setFullYear(vorigJaar.getFullYear() - 1);
    expect(isVandaag(vorigJaar)).toBe(false);
  });
});

describe('getMaandag', () => {
  it('geeft maandag terug voor een woensdag', () => {
    // 2025-06-04 is een woensdag
    const wo = new Date(2025, 5, 4);
    const ma = getMaandag(wo);
    expect(ma.getDay()).toBe(1);
    expect(ma.getDate()).toBe(2);
  });

  it('geeft zichzelf terug als het al maandag is', () => {
    // 2025-06-02 is een maandag
    const ma = new Date(2025, 5, 2);
    const result = getMaandag(ma);
    expect(result.getDate()).toBe(2);
  });

  it('geeft maandag terug voor een zondag', () => {
    // 2025-06-08 is een zondag
    const zo = new Date(2025, 5, 8);
    const ma = getMaandag(zo);
    expect(ma.getDay()).toBe(1);
    expect(ma.getDate()).toBe(2);
  });

  it('geeft maandag terug voor een zaterdag', () => {
    // 2025-06-07 is een zaterdag
    const za = new Date(2025, 5, 7);
    const ma = getMaandag(za);
    expect(ma.getDay()).toBe(1);
    expect(ma.getDate()).toBe(2);
  });
});

describe('periodeLabel', () => {
  it('geeft maandlabel terug', () => {
    const d = new Date(2025, 5, 15); // juni
    const label = periodeLabel('maand', d);
    expect(label).toBe('juni 2025');
  });

  it('geeft weeklabel terug', () => {
    // 2025-06-02 maandag t/m 2025-06-08 zondag
    const d = new Date(2025, 5, 4); // woensdag
    const label = periodeLabel('week', d);
    expect(label).toContain('2');
    expect(label).toContain('juni');
    expect(label).toContain('8');
  });

  it('geeft daglabel terug', () => {
    const d = new Date(2025, 5, 15);
    const label = periodeLabel('dag', d);
    expect(label).toContain('15');
    expect(label).toContain('juni');
  });
});

describe('badgeKleur', () => {
  it('geeft rood voor Ziekte', () => {
    const a = maakAfwezigheid({ type: 'Ziekte' });
    expect(badgeKleur(a)).toContain('red');
  });

  it('geeft amber voor In afwachting', () => {
    const a = maakAfwezigheid({ type: 'Verlof', status: 'In afwachting' });
    expect(badgeKleur(a)).toContain('amber');
  });

  it('geeft groen voor goedgekeurd verlof', () => {
    const a = maakAfwezigheid({ type: 'Verlof', status: 'Goedgekeurd' });
    expect(badgeKleur(a)).toContain('emerald');
  });
});

describe('rijKleur', () => {
  it('geeft rood voor Ziekte', () => {
    const a = maakAfwezigheid({ type: 'Ziekte' });
    expect(rijKleur(a)).toContain('red');
  });

  it('geeft amber voor In afwachting', () => {
    const a = maakAfwezigheid({ type: 'Verlof', status: 'In afwachting' });
    expect(rijKleur(a)).toContain('amber');
  });

  it('geeft groen voor goedgekeurd', () => {
    const a = maakAfwezigheid({ type: 'Verlof', status: 'Goedgekeurd' });
    expect(rijKleur(a)).toContain('emerald');
  });
});

describe('afwezigheidLabel', () => {
  it('geeft "Ziekte" voor type Ziekte', () => {
    expect(afwezigheidLabel(maakAfwezigheid({ type: 'Ziekte' }))).toBe(
      'Ziekte',
    );
  });

  it('geeft "Verlof (wachten)" voor In afwachting', () => {
    const a = maakAfwezigheid({ type: 'Verlof', status: 'In afwachting' });
    expect(afwezigheidLabel(a)).toBe('Verlof (wachten)');
  });

  it('geeft "Verlof" voor goedgekeurd verlof', () => {
    const a = maakAfwezigheid({ type: 'Verlof', status: 'Goedgekeurd' });
    expect(afwezigheidLabel(a)).toBe('Verlof');
  });
});

describe('getPeriodBounds', () => {
  it('geeft een van en tot datum terug', () => {
    const d = new Date(2025, 5, 15);
    const { van, tot } = getPeriodBounds('maand', d);
    expect(van).toMatch(/^\d{4}-\d{2}-\d{2}$/);
    expect(tot).toMatch(/^\d{4}-\d{2}-\d{2}$/);
  });

  it('van ligt voor tot', () => {
    const d = new Date(2025, 5, 15);
    const { van, tot } = getPeriodBounds('maand', d);
    expect(new Date(van).getTime()).toBeLessThan(new Date(tot).getTime());
  });
});

describe('takenOpDag', () => {
  it('geeft taken terug met deadline op de opgegeven dag', () => {
    const t = maakTaak({ deadline: '2025-06-03' });
    const result = takenOpDag([t], new Date('2025-06-03'));
    expect(result).toHaveLength(1);
  });

  it('geeft lege array als geen taak deadline op die dag heeft', () => {
    const t = maakTaak({ deadline: '2025-06-05' });
    const result = takenOpDag([t], new Date('2025-06-03'));
    expect(result).toHaveLength(0);
  });

  it('geeft lege array bij lege input', () => {
    expect(takenOpDag([], new Date('2025-06-03'))).toHaveLength(0);
  });
});

describe('taakBadgeKleur', () => {
  it('geeft grijs voor afgewerkte taak', () => {
    const t = maakTaak({ afgewerkt: true });
    expect(taakBadgeKleur(t)).toContain('zinc');
  });

  it('geeft rood voor belangrijke niet-afgewerkte taak', () => {
    const t = maakTaak({ afgewerkt: false, belangrijk: true });
    expect(taakBadgeKleur(t)).toContain('rose');
  });

  it('geeft blauw voor gewone taak', () => {
    const t = maakTaak({ afgewerkt: false, belangrijk: false });
    expect(taakBadgeKleur(t)).toContain('blue');
  });

  it('afgewerkt heeft prioriteit boven belangrijk', () => {
    const t = maakTaak({ afgewerkt: true, belangrijk: true });
    expect(taakBadgeKleur(t)).toContain('zinc');
  });
});
