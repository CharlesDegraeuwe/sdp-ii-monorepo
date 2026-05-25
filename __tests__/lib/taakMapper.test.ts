import { describe, it, expect } from 'vitest';
import { mapBackendTask, mapTaskToBackend } from '@/lib/taakMapper';

describe('mapBackendTask', () => {
  it('mapt een volledig DTO correct', () => {
    const dto = {
      id: 1,
      titel: 'Test taak',
      beschrijving: 'Beschrijving',
      afgewerkt: 'ja',
      deadline: '2025-12-31',
      siteId: 5,
      werknemer: { id: 99, naam: 'Janssen', voornaam: 'Jan', email: 'j@j.be' },
    };
    const task = mapBackendTask(dto);
    expect(task.id).toBe('1');
    expect(task.name).toBe('Test taak');
    expect(task.description).toBe('Beschrijving');
    expect(task.specifications).toBe('Beschrijving');
    expect(task.finished).toBe(true);
    expect(task.dueDate).toBe('2025-12-31');
    expect(task.location).toBe('5');
    expect(task.assigneeId).toBe('99');
    expect(task.important).toBe(false);
  });

  it('zet finished op false als afgewerkt niet "ja" is', () => {
    const task = mapBackendTask({ id: 2, afgewerkt: 'nee' });
    expect(task.finished).toBe(false);
  });

  it('zet finished op false bij ontbrekend afgewerkt veld', () => {
    const task = mapBackendTask({ id: 3 });
    expect(task.finished).toBe(false);
  });

  it('geeft lege string voor naam als titel ontbreekt', () => {
    const task = mapBackendTask({ id: 4 });
    expect(task.name).toBe('');
  });

  it('geeft lege string voor location als siteId null is', () => {
    const task = mapBackendTask({ id: 5, siteId: null });
    expect(task.location).toBe('');
  });

  it('geeft undefined voor assigneeId als werknemer ontbreekt', () => {
    const task = mapBackendTask({ id: 6 });
    expect(task.assigneeId).toBeUndefined();
  });

  it('werkt met Record<string, unknown> als input type', () => {
    const dto: Record<string, unknown> = { id: 7, titel: 'Via record' };
    const task = mapBackendTask(dto);
    expect(task.id).toBe('7');
    expect(task.name).toBe('Via record');
  });
});

describe('mapTaskToBackend', () => {
  it('mapt een taak correct naar backend DTO', () => {
    const task = {
      name: 'Backend taak',
      description: 'Omschrijving',
      specifications: 'Specs',
      dueDate: '2025-06-01',
      location: '3',
      important: true,
      assigneeId: '42',
    };
    const dto = mapTaskToBackend(task);
    expect(dto.titel).toBe('Backend taak');
    expect(dto.beschrijving).toBe('Omschrijving');
    expect(dto.deadline).toBe('2025-06-01');
    expect(dto.werknemerId).toBe(42);
  });

  it('gebruikt specifications als description ontbreekt', () => {
    const task = {
      name: 'Taak',
      specifications: 'Alleen specs',
      dueDate: '2025-01-01',
      location: '',
      important: false,
    };
    const dto = mapTaskToBackend(task);
    expect(dto.beschrijving).toBe('Alleen specs');
  });

  it('laat werknemerId weg als assigneeId ontbreekt', () => {
    const task = {
      name: 'Geen assignee',
      dueDate: '2025-01-01',
      location: '',
      important: false,
    };
    const dto = mapTaskToBackend(task);
    expect(dto.werknemerId).toBeUndefined();
  });

  it('geeft lege beschrijving als zowel description als specifications ontbreken', () => {
    const task = {
      name: 'Leeg',
      dueDate: '2025-01-01',
      location: '',
      important: false,
    };
    const dto = mapTaskToBackend(task);
    expect(dto.beschrijving).toBe('');
  });
});
