import { describe, it, expect, beforeEach } from 'vitest';
import { useTeamsStore } from '@/stores/teamStore';
import type { Team, Werknemer, Site, TeamLid } from '@/stores/teamStore';

const initialState = useTeamsStore.getState();

beforeEach(() => {
  useTeamsStore.setState(initialState);
});

const maakTeam = (overrides: Partial<Team> = {}): Team => ({
  id: 1,
  naam: 'Team A',
  beschrijving: 'Beschrijving',
  managerId: 10,
  managerNaam: 'Jan Janssen',
  siteId: 100,
  siteNaam: 'Site Alpha',
  ...overrides,
});

const maakWerknemer = (overrides: Partial<Werknemer> = {}): Werknemer => ({
  id: 1,
  voornaam: 'Piet',
  naam: 'Pieters',
  email: 'piet@test.be',
  telefoon: '0499000000',
  beschikbaarheid: 'Voltijds',
  siteId: 100,
  siteNaam: 'Site Alpha',
  role: 'Werknemer',
  status: 'Actief',
  ...overrides,
});

const maakSite = (overrides: Partial<Site> = {}): Site => ({
  id: 100,
  naam: 'Site Alpha',
  ...overrides,
});

const maakTeamLid = (overrides: Partial<TeamLid> = {}): TeamLid => ({
  werknemerId: 1,
  voornaam: 'Piet',
  naam: 'Pieters',
  isSupervisor: false,
  ...overrides,
});

describe('useTeamsStore - teams', () => {
  it('slaat teams op via setTeams', () => {
    useTeamsStore
      .getState()
      .setTeams([maakTeam({ id: 1 }), maakTeam({ id: 2 })]);
    expect(Object.keys(useTeamsStore.getState().teams)).toHaveLength(2);
  });

  it('voegt een team toe via addTeam', () => {
    useTeamsStore.getState().addTeam(maakTeam({ id: 5 }));
    expect(useTeamsStore.getState().teams[5]).toBeDefined();
    expect(useTeamsStore.getState().teams[5].naam).toBe('Team A');
  });

  it('verwijdert een team via removeTeam', () => {
    useTeamsStore
      .getState()
      .setTeams([maakTeam({ id: 1 }), maakTeam({ id: 2 })]);
    useTeamsStore.getState().removeTeam(1);
    expect(useTeamsStore.getState().teams[1]).toBeUndefined();
    expect(useTeamsStore.getState().teams[2]).toBeDefined();
  });
});

describe('useTeamsStore - werknemers', () => {
  it('slaat werknemers op via setWerknemers', () => {
    useTeamsStore
      .getState()
      .setWerknemers([maakWerknemer({ id: 1 }), maakWerknemer({ id: 2 })]);
    expect(Object.keys(useTeamsStore.getState().werknemers)).toHaveLength(2);
  });

  it('voegt een werknemer toe via addWerknemer', () => {
    useTeamsStore.getState().addWerknemer(maakWerknemer({ id: 7 }));
    expect(useTeamsStore.getState().werknemers[7]).toBeDefined();
  });

  it('update een werknemer via updateWerknemer', () => {
    useTeamsStore
      .getState()
      .addWerknemer(maakWerknemer({ id: 1, voornaam: 'Oud' }));
    useTeamsStore.getState().updateWerknemer(1, { voornaam: 'Nieuw' });
    expect(useTeamsStore.getState().werknemers[1].voornaam).toBe('Nieuw');
  });

  it('laat andere velden ongewijzigd bij updateWerknemer', () => {
    useTeamsStore
      .getState()
      .addWerknemer(maakWerknemer({ id: 1, naam: 'Pieters', voornaam: 'Oud' }));
    useTeamsStore.getState().updateWerknemer(1, { voornaam: 'Nieuw' });
    expect(useTeamsStore.getState().werknemers[1].naam).toBe('Pieters');
  });

  it('verwijdert een werknemer via removeWerknemer', () => {
    useTeamsStore
      .getState()
      .setWerknemers([maakWerknemer({ id: 1 }), maakWerknemer({ id: 2 })]);
    useTeamsStore.getState().removeWerknemer(1);
    expect(useTeamsStore.getState().werknemers[1]).toBeUndefined();
    expect(useTeamsStore.getState().werknemers[2]).toBeDefined();
  });
});

describe('useTeamsStore - sites', () => {
  it('slaat sites op via setSites', () => {
    useTeamsStore
      .getState()
      .setSites([maakSite({ id: 100 }), maakSite({ id: 200 })]);
    expect(Object.keys(useTeamsStore.getState().sites)).toHaveLength(2);
  });
});

describe('useTeamsStore - teamLeden', () => {
  it('slaat teamleden op via setTeamLeden', () => {
    const leden: TeamLid[] = [
      maakTeamLid({ werknemerId: 1 }),
      maakTeamLid({ werknemerId: 2 }),
    ];
    useTeamsStore.getState().setTeamLeden(1, leden);
    expect(useTeamsStore.getState().teamLeden[1]).toHaveLength(2);
  });

  it('overschrijft bestaande teamleden', () => {
    useTeamsStore.getState().setTeamLeden(1, [maakTeamLid({ werknemerId: 1 })]);
    useTeamsStore
      .getState()
      .setTeamLeden(1, [
        maakTeamLid({ werknemerId: 2 }),
        maakTeamLid({ werknemerId: 3 }),
      ]);
    expect(useTeamsStore.getState().teamLeden[1]).toHaveLength(2);
  });

  it('promoot een teamlid tot supervisor via setSupervisor', () => {
    useTeamsStore
      .getState()
      .setTeamLeden(1, [
        maakTeamLid({ werknemerId: 1, isSupervisor: false }),
        maakTeamLid({ werknemerId: 2, isSupervisor: false }),
      ]);
    useTeamsStore.getState().setSupervisor(1, 1);
    const leden = useTeamsStore.getState().teamLeden[1];
    expect(leden.find((l) => l.werknemerId === 1)?.isSupervisor).toBe(true);
    expect(leden.find((l) => l.werknemerId === 2)?.isSupervisor).toBe(false);
  });

  it('setSupervisor op leeg team doet niks', () => {
    useTeamsStore.getState().setSupervisor(999, 1);
    expect(useTeamsStore.getState().teamLeden[999]).toEqual([]);
  });
});

describe('useTeamsStore - selectie & filter', () => {
  it('selecteert een team', () => {
    useTeamsStore.getState().selectTeam(5);
    expect(useTeamsStore.getState().selectedTeamId).toBe(5);
  });

  it('selecteert een werknemer', () => {
    useTeamsStore.getState().selectWerknemer(3);
    expect(useTeamsStore.getState().selectedWerknemerId).toBe(3);
  });

  it('filtert op site', () => {
    useTeamsStore.getState().setFilterSite(100);
    expect(useTeamsStore.getState().filterSiteId).toBe(100);
  });

  it('reset filter naar null', () => {
    useTeamsStore.getState().setFilterSite(100);
    useTeamsStore.getState().setFilterSite(null);
    expect(useTeamsStore.getState().filterSiteId).toBeNull();
  });
});

describe('useTeamsStore - loaded & sync', () => {
  it('zet loaded op true', () => {
    useTeamsStore.getState().setLoaded(true);
    expect(useTeamsStore.getState().loaded).toBe(true);
  });

  it('zet lastSynced timestamp', () => {
    const now = Date.now();
    useTeamsStore.getState().setLastSynced(now);
    expect(useTeamsStore.getState().lastSynced).toBe(now);
  });
});
