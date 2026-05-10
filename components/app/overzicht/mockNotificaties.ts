// Mock notificaties voor de overzichtspagina.
// Vervang dit bestand NIET — wissel enkel de import in useOverzichtData.ts
// van deze mock naar echte API-data zodra de backend klaar is.
// Zie het TODO-commentaar in useOverzichtData.ts.

import type { Notificatie } from './types';

export const mockNotificaties: Notificatie[] = [
  {
    id: 1,
    werknemerId: 1,
    titel: 'Nieuwe verlofaanvraag',
    bericht:
      'Thomas Janssen heeft een verlofaanvraag ingediend voor 15–16 mei.',
    gelezen: 'Nee',
    datum: '2026-05-09T08:30:00',
    referentieId: 42,
  },
  {
    id: 2,
    werknemerId: 1,
    titel: 'Verlof goedgekeurd',
    bericht: 'Je verlofaanvraag voor 20–21 mei is goedgekeurd door je manager.',
    gelezen: 'Nee',
    datum: '2026-05-08T14:15:00',
    referentieId: 38,
  },
  {
    id: 3,
    werknemerId: 1,
    titel: 'Teamlid afwezig',
    bericht: 'Sara Declercq is vandaag ziek gemeld.',
    gelezen: 'Nee',
    datum: '2026-05-09T07:00:00',
    referentieId: 7,
  },
  {
    id: 4,
    werknemerId: 1,
    titel: 'Taak toegewezen',
    bericht: 'Je bent toegewezen aan "Klantenpresentatie voorbereiding".',
    gelezen: 'Ja',
    datum: '2026-05-07T09:00:00',
    referentieId: 15,
  },
  {
    id: 5,
    werknemerId: 1,
    titel: 'Verlof afgewezen',
    bericht: 'Je verlofaanvraag voor 1–2 mei werd niet goedgekeurd.',
    gelezen: 'Ja',
    datum: '2026-05-06T11:30:00',
    referentieId: 35,
  },
  {
    id: 6,
    werknemerId: 1,
    titel: 'Nieuwe verlofaanvraag',
    bericht: 'Lena Pieters heeft een verlofaanvraag ingediend voor 25 mei.',
    gelezen: 'Ja',
    datum: '2026-05-05T10:00:00',
    referentieId: 40,
  },
  {
    id: 7,
    werknemerId: 1,
    titel: 'Teamlid afwezig',
    bericht: 'Pieter Van den Berg is afwezig wegens persoonlijke redenen.',
    gelezen: 'Ja',
    datum: '2026-05-04T08:00:00',
    referentieId: null,
  },
  {
    id: 8,
    werknemerId: 1,
    titel: 'Taak afgerond',
    bericht: '"Maandrapport mei" is als afgerond gemarkeerd door je collega.',
    gelezen: 'Ja',
    datum: '2026-05-03T16:00:00',
    referentieId: 12,
  },
];
