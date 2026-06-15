import { describe, it, expect, beforeEach } from 'vitest';
import { useTaakStore } from '@/stores/taakStore';
import type { Task, Team, TeamMember } from '@/stores/taakStore';

const initialState = useTaakStore.getState();

beforeEach(() => {
  useTaakStore.setState(initialState);
});

const maakTaak = (overrides: Partial<Task> = {}): Task => ({
  id: '1',
  name: 'Taak 1',
  dueDate: '2025-12-01',
  location: 'Locatie A',
  important: false,
  finished: false,
  ...overrides,
});

const maakTeam = (overrides: Partial<Team> = {}): Team => ({
  id: 't1',
  name: 'Team Alpha',
  plant: 'Plant 1',
  members: [],
  ...overrides,
});

const maakLid = (overrides: Partial<TeamMember> = {}): TeamMember => ({
  id: 'm1',
  firstName: 'Jan',
  lastName: 'Janssen',
  email: 'jan@test.be',
  ...overrides,
});

describe('useTaakStore - taken', () => {
  it('voegt een taak toe', () => {
    useTaakStore.getState().addTask(maakTaak());
    expect(useTaakStore.getState().tasks['1']).toBeDefined();
  });

  it('slaat meerdere taken op via setTasks', () => {
    useTaakStore
      .getState()
      .setTasks([maakTaak({ id: '1' }), maakTaak({ id: '2' })]);
    const tasks = useTaakStore.getState().tasks;
    expect(Object.keys(tasks)).toHaveLength(2);
    expect(tasks['1']).toBeDefined();
    expect(tasks['2']).toBeDefined();
  });

  it('update een bestaande taak', () => {
    useTaakStore.getState().addTask(maakTaak({ id: '1', name: 'Oud' }));
    useTaakStore.getState().updateTask('1', { name: 'Nieuw' });
    expect(useTaakStore.getState().tasks['1'].name).toBe('Nieuw');
  });

  it('verwijdert een taak', () => {
    useTaakStore.getState().addTask(maakTaak({ id: '1' }));
    useTaakStore.getState().removeTask('1');
    expect(useTaakStore.getState().tasks['1']).toBeUndefined();
  });

  it('wijst een taak toe aan een member', () => {
    useTaakStore.getState().addTask(maakTaak({ id: '1' }));
    useTaakStore.getState().assignTask('1', 'm42');
    expect(useTaakStore.getState().tasks['1'].assigneeId).toBe('m42');
  });

  it('laat andere taken ongewijzigd bij removeTask', () => {
    useTaakStore
      .getState()
      .setTasks([maakTaak({ id: '1' }), maakTaak({ id: '2' })]);
    useTaakStore.getState().removeTask('1');
    expect(useTaakStore.getState().tasks['2']).toBeDefined();
  });
});

describe('useTaakStore - teams en members', () => {
  it('slaat teams op via setTeams', () => {
    useTaakStore
      .getState()
      .setTeams([maakTeam({ id: 't1' }), maakTeam({ id: 't2' })]);
    expect(Object.keys(useTaakStore.getState().teams)).toHaveLength(2);
  });

  it('slaat members op via setMembers', () => {
    useTaakStore
      .getState()
      .setMembers([maakLid({ id: 'm1' }), maakLid({ id: 'm2' })]);
    expect(Object.keys(useTaakStore.getState().members)).toHaveLength(2);
  });
});

describe('useTaakStore - selectie', () => {
  it('selecteert een team', () => {
    useTaakStore.getState().selectTeam('t1');
    expect(useTaakStore.getState().selectedTeamId).toBe('t1');
  });

  it('selecteert een member', () => {
    useTaakStore.getState().selectMember('m1');
    expect(useTaakStore.getState().selectedMemberId).toBe('m1');
  });

  it('selecteert een taak', () => {
    useTaakStore.getState().selectTask('1');
    expect(useTaakStore.getState().selectedTaskId).toBe('1');
  });

  it('kan selectie op null zetten', () => {
    useTaakStore.getState().selectTeam('t1');
    useTaakStore.getState().selectTeam(null);
    expect(useTaakStore.getState().selectedTeamId).toBeNull();
  });

  it('kan taakselectie op null zetten', () => {
    useTaakStore.getState().selectTask('1');
    useTaakStore.getState().selectTask(null);
    expect(useTaakStore.getState().selectedTaskId).toBeNull();
  });

  it('begint zonder geselecteerde taak', () => {
    expect(useTaakStore.getState().selectedTaskId).toBeNull();
  });
});

describe('useTaakStore - loaded & sync', () => {
  it('zet loaded op true', () => {
    useTaakStore.getState().setLoaded(true);
    expect(useTaakStore.getState().loaded).toBe(true);
  });

  it('zet lastSynced timestamp', () => {
    const now = Date.now();
    useTaakStore.getState().setLastSynced(now);
    expect(useTaakStore.getState().lastSynced).toBe(now);
  });
});
