import { describe, it, expect, beforeEach } from 'vitest';
import { renderHook } from '@testing-library/react';
import { useFilteredWerknemers } from '@/hooks/useFilteredWerknemers';
import { useTeamsStore } from '@/stores/teamStore';
import type { Werknemer } from '@/stores/teamStore';

const initialState = useTeamsStore.getState();

beforeEach(() => {
  useTeamsStore.setState(initialState);
});

const maakWerknemer = (overrides: Partial<Werknemer>): Werknemer => ({
  id: 1,
  voornaam: 'Jan',
  naam: 'Janssen',
  email: 'jan@test.be',
  telefoon: '0499000000',
  beschikbaarheid: 'Voltijds',
  siteId: 1,
  siteNaam: 'Site A',
  role: 'Werknemer',
  status: 'Actief',
  ...overrides,
});

describe('useFilteredWerknemers', () => {
  it('geeft alle werknemers terug bij lege zoekterm', () => {
    useTeamsStore
      .getState()
      .setWerknemers([maakWerknemer({ id: 1 }), maakWerknemer({ id: 2 })]);
    const { result } = renderHook(() => useFilteredWerknemers(''));
    expect(result.current).toHaveLength(2);
  });

  it('filtert op voornaam', () => {
    useTeamsStore
      .getState()
      .setWerknemers([
        maakWerknemer({
          id: 1,
          voornaam: 'Jan',
          naam: 'Janssen',
          email: 'jan@test.be',
        }),
        maakWerknemer({
          id: 2,
          voornaam: 'Piet',
          naam: 'Pieters',
          email: 'piet@test.be',
        }),
      ]);
    const { result } = renderHook(() => useFilteredWerknemers('jan'));
    expect(result.current).toHaveLength(1);
    expect(result.current[0].voornaam).toBe('Jan');
  });

  it('filtert op achternaam', () => {
    useTeamsStore
      .getState()
      .setWerknemers([
        maakWerknemer({ id: 1, naam: 'Janssen' }),
        maakWerknemer({ id: 2, naam: 'Pieters' }),
      ]);
    const { result } = renderHook(() => useFilteredWerknemers('pieters'));
    expect(result.current).toHaveLength(1);
    expect(result.current[0].naam).toBe('Pieters');
  });

  it('filtert op email', () => {
    useTeamsStore
      .getState()
      .setWerknemers([
        maakWerknemer({ id: 1, email: 'jan@test.be' }),
        maakWerknemer({ id: 2, email: 'piet@other.be' }),
      ]);
    const { result } = renderHook(() => useFilteredWerknemers('other'));
    expect(result.current).toHaveLength(1);
    expect(result.current[0].email).toBe('piet@other.be');
  });

  it('is hoofdletterongevoelig', () => {
    useTeamsStore
      .getState()
      .setWerknemers([maakWerknemer({ id: 1, voornaam: 'JAN' })]);
    const { result } = renderHook(() => useFilteredWerknemers('jan'));
    expect(result.current).toHaveLength(1);
  });

  it('geeft lege array bij geen match', () => {
    useTeamsStore
      .getState()
      .setWerknemers([maakWerknemer({ id: 1, voornaam: 'Jan' })]);
    const { result } = renderHook(() => useFilteredWerknemers('xyz'));
    expect(result.current).toHaveLength(0);
  });

  it('geeft lege array bij lege store', () => {
    useTeamsStore.getState().setWerknemers([]);
    const { result } = renderHook(() => useFilteredWerknemers('jan'));
    expect(result.current).toHaveLength(0);
  });
});
