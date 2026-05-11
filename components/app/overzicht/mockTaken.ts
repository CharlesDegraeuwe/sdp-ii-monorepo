// Mock taken voor de overzichtspagina.
// TODO: Verwijder de import van deze file in useOverzichtData.ts zodra de backend taken levert.

import type { Task } from '@/stores/taakStore';

export const mockTaken: Task[] = [
  {
    id: '1',
    name: 'Maandrapport opstellen',
    specifications: 'Rapport met KPIs en afwezigheidsoverzicht voor mei 2026.',
    dueDate: '2026-05-12T17:00:00',
    location: 'Kantoor Gent',
    important: true,
    finished: false,
  },
  {
    id: '2',
    name: 'Onboardingsgesprek Thomas Janssen',
    specifications: 'Introductiemeeting met nieuwe collega Thomas Janssen.',
    dueDate: '2026-05-10T10:00:00',
    location: 'Vergaderzaal B',
    important: true,
    finished: false,
  },
  {
    id: '3',
    name: 'Verlofaanvragen verwerken',
    specifications: 'Openstaande verlofaanvragen nakijken en verwerken.',
    dueDate: '2026-05-13T12:00:00',
    location: 'Remote',
    important: false,
    finished: false,
  },
  {
    id: '4',
    name: 'Teamvergadering voorbereiden',
    specifications: 'Agenda opmaken voor wekelijkse teamvergadering.',
    dueDate: '2026-05-14T09:00:00',
    location: 'Kantoor Gent',
    important: false,
    finished: false,
  },
  {
    id: '5',
    name: 'Prestatiegesprekken inplannen',
    specifications:
      'Evaluatiegesprekken voor Q2 inplannen voor alle teamleden.',
    dueDate: '2026-05-16T17:00:00',
    location: 'Kantoor Gent',
    important: true,
    finished: false,
  },
];
