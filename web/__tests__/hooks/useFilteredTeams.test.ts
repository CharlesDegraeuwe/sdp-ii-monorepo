import { describe, it, expect, beforeEach } from 'vitest';
import { renderHook } from '@testing-library/react';
import { useFilteredTeams } from '@/hooks/useFilteredTeams';
import { useTeamsStore } from '@/stores/teamStore';
import type { Team } from '@/stores/teamStore';

const initialState = useTeamsStore.getState();

beforeEach(() => {
  useTeamsStore.setState(initialState);
});

const maakTeam = (overrides: Partial<Team>): Team => ({
  id: 1,
  naam: 'Team A',
  beschrijving: '',
  managerId: 1,
  managerNaam: 'Jan',
  siteId: 100,
  siteNaam: 'Site Alpha',
  ...overrides,
});

describe('useFilteredTeams', () => {
  it('geeft alle teams terug bij lege zoekterm', () => {
    useTeamsStore
      .getState()
      .setTeams([
        maakTeam({ id: 1, naam: 'Alpha' }),
        maakTeam({ id: 2, naam: 'Beta' }),
      ]);
    const { result } = renderHook(() => useFilteredTeams(''));
    expect(result.current).toHaveLength(2);
  });

  it('filtert op naam (hoofdletterongevoelig)', () => {
    useTeamsStore
      .getState()
      .setTeams([
        maakTeam({ id: 1, naam: 'Team Alpha' }),
        maakTeam({ id: 2, naam: 'Team Beta' }),
      ]);
    const { result } = renderHook(() => useFilteredTeams('alpha'));
    expect(result.current).toHaveLength(1);
    expect(result.current[0].naam).toBe('Team Alpha');
  });

  it('geeft lege array bij geen match', () => {
    useTeamsStore
      .getState()
      .setTeams([maakTeam({ id: 1, naam: 'Team Alpha' })]);
    const { result } = renderHook(() => useFilteredTeams('xyz'));
    expect(result.current).toHaveLength(0);
  });

  it('filtert op siteId via filterSiteId in store', () => {
    useTeamsStore
      .getState()
      .setTeams([
        maakTeam({ id: 1, naam: 'Team Alpha', siteId: 100 }),
        maakTeam({ id: 2, naam: 'Team Beta', siteId: 200 }),
      ]);
    useTeamsStore.getState().setFilterSite(100);
    const { result } = renderHook(() => useFilteredTeams(''));
    expect(result.current).toHaveLength(1);
    expect(result.current[0].siteId).toBe(100);
  });

  it('combineert site-filter en zoekterm', () => {
    useTeamsStore
      .getState()
      .setTeams([
        maakTeam({ id: 1, naam: 'Team Alpha', siteId: 100 }),
        maakTeam({ id: 2, naam: 'Team Beta', siteId: 100 }),
        maakTeam({ id: 3, naam: 'Team Gamma', siteId: 200 }),
      ]);
    useTeamsStore.getState().setFilterSite(100);
    const { result } = renderHook(() => useFilteredTeams('alpha'));
    expect(result.current).toHaveLength(1);
    expect(result.current[0].naam).toBe('Team Alpha');
  });

  it('geeft alle teams terug als filterSiteId null is', () => {
    useTeamsStore
      .getState()
      .setTeams([
        maakTeam({ id: 1, siteId: 100 }),
        maakTeam({ id: 2, siteId: 200 }),
      ]);
    useTeamsStore.getState().setFilterSite(null);
    const { result } = renderHook(() => useFilteredTeams(''));
    expect(result.current).toHaveLength(2);
  });
});
