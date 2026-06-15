import { describe, it, expect } from 'vitest';
import {
  isBelgischeFestdag,
  isWeekend,
  isVrij,
  vrijReden,
} from '@/components/app/planner/belgischCalendar';

describe('isWeekend', () => {
  it('zaterdag is weekend', () => {
    expect(isWeekend(new Date(2025, 5, 7))).toBe(true); // za
  });

  it('zondag is weekend', () => {
    expect(isWeekend(new Date(2025, 5, 8))).toBe(true); // zo
  });

  it('maandag is geen weekend', () => {
    expect(isWeekend(new Date(2025, 5, 2))).toBe(false);
  });

  it('vrijdag is geen weekend', () => {
    expect(isWeekend(new Date(2025, 5, 6))).toBe(false);
  });
});

describe('isBelgischeFestdag - vaste feestdagen', () => {
  it('1 januari is Nieuwjaar', () => {
    expect(isBelgischeFestdag(new Date(2025, 0, 1))).toBe(true);
  });

  it('1 mei is Dag van de Arbeid', () => {
    expect(isBelgischeFestdag(new Date(2025, 4, 1))).toBe(true);
  });

  it('21 juli is Nationale Feestdag', () => {
    expect(isBelgischeFestdag(new Date(2025, 6, 21))).toBe(true);
  });

  it('15 augustus is O.-L.-V. Hemelvaart', () => {
    expect(isBelgischeFestdag(new Date(2025, 7, 15))).toBe(true);
  });

  it('1 november is Allerheiligen', () => {
    expect(isBelgischeFestdag(new Date(2025, 10, 1))).toBe(true);
  });

  it('11 november is Wapenstilstand', () => {
    expect(isBelgischeFestdag(new Date(2025, 10, 11))).toBe(true);
  });

  it('25 december is Kerstmis', () => {
    expect(isBelgischeFestdag(new Date(2025, 11, 25))).toBe(true);
  });

  it('gewone dag is geen feestdag', () => {
    expect(isBelgischeFestdag(new Date(2025, 5, 4))).toBe(false); // woensdag 4 juni
  });
});

describe('isBelgischeFestdag - variabele feestdagen (2025)', () => {
  // Pasen 2025 = 20 april
  it('paasmaandag 2025 is feestdag', () => {
    expect(isBelgischeFestdag(new Date(2025, 3, 21))).toBe(true);
  });

  // Hemelvaartsdag 2025 = 29 mei
  it('hemelvaartsdag 2025 is feestdag', () => {
    expect(isBelgischeFestdag(new Date(2025, 4, 29))).toBe(true);
  });

  // Pinkstermaandag 2025 = 9 juni
  it('pinkstermaandag 2025 is feestdag', () => {
    expect(isBelgischeFestdag(new Date(2025, 5, 9))).toBe(true);
  });

  // Paaszondag 2025 = 20 april
  it('paaszondag 2025 is feestdag', () => {
    expect(isBelgischeFestdag(new Date(2025, 3, 20))).toBe(true);
  });
});

describe('isVrij', () => {
  it('zaterdag is vrij', () => {
    expect(isVrij(new Date(2025, 5, 7))).toBe(true);
  });

  it('feestdag in de week is vrij', () => {
    expect(isVrij(new Date(2025, 4, 1))).toBe(true); // 1 mei donderdag
  });

  it('gewone werkdag is niet vrij', () => {
    expect(isVrij(new Date(2025, 5, 4))).toBe(false); // woensdag
  });
});

describe('vrijReden', () => {
  it('geeft "Weekend" voor zaterdag', () => {
    expect(vrijReden(new Date(2025, 5, 7))).toBe('Weekend');
  });

  it('geeft "Weekend" voor zondag', () => {
    expect(vrijReden(new Date(2025, 5, 8))).toBe('Weekend');
  });

  it('geeft feestdagnaam voor 1 mei', () => {
    expect(vrijReden(new Date(2025, 4, 1))).toBe('Dag van de Arbeid');
  });

  it('geeft feestdagnaam voor 21 juli', () => {
    expect(vrijReden(new Date(2025, 6, 21))).toBe('Nationale Feestdag');
  });

  it('geeft lege string voor gewone werkdag', () => {
    expect(vrijReden(new Date(2025, 5, 4))).toBe(''); // woensdag geen feestdag
  });
});
