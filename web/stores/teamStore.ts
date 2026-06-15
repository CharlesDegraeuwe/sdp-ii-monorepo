import { create } from 'zustand';

export type Role = 'Werknemer' | 'Supervisor' | 'Manager';
export type Status = 'Actief' | 'Geblokkeerd' | 'Inactief';

export interface Werknemer {
  id: number;
  voornaam: string;
  naam: string;
  email: string;
  telefoon: string;
  beschikbaarheid: string;
  siteId: number;
  siteNaam: string;
  role: Role;
  status: Status;
  color?: 'blue' | 'green' | 'yellow' | 'red';
}

export interface TeamLid {
  werknemerId: number;
  voornaam: string;
  naam: string;
  isSupervisor: boolean;
}

export interface Team {
  id: number;
  naam: string;
  beschrijving: string;
  managerId: number;
  managerNaam: string;
  siteId: number;
  siteNaam: string;
}

export interface Site {
  id: number;
  naam: string;
}

interface TeamsStore {
  teams: Record<number, Team>;
  werknemers: Record<number, Werknemer>;
  sites: Record<number, Site>;
  teamLeden: Record<number, TeamLid[]>;

  selectedTeamId: number | null;
  selectedWerknemerId: number | null;
  filterSiteId: number | null;

  loaded: boolean;
  lastSynced: number | null;

  setTeams: (t: Team[]) => void;
  setWerknemers: (w: Werknemer[]) => void;
  setSites: (s: Site[]) => void;
  setTeamLeden: (teamId: number, leden: TeamLid[]) => void;

  addTeam: (t: Team) => void;
  removeTeam: (id: number) => void;

  addWerknemer: (w: Werknemer) => void;
  updateWerknemer: (id: number, data: Partial<Werknemer>) => void;
  removeWerknemer: (id: number) => void;

  setSupervisor: (teamId: number, werknemerId: number) => void;

  selectTeam: (id: number | null) => void;
  selectWerknemer: (id: number | null) => void;
  setFilterSite: (id: number | null) => void;

  setLoaded: (b: boolean) => void;
  setLastSynced: (ts: number) => void;
}

export const useTeamsStore = create<TeamsStore>((set) => ({
  teams: {},
  werknemers: {},
  sites: {},
  teamLeden: {},

  selectedTeamId: null,
  selectedWerknemerId: null,
  filterSiteId: null,

  loaded: false,
  lastSynced: null,

  setTeams: (t) => set({ teams: Object.fromEntries(t.map((x) => [x.id, x])) }),
  setWerknemers: (w) =>
    set({ werknemers: Object.fromEntries(w.map((x) => [x.id, x])) }),
  setSites: (s) => set({ sites: Object.fromEntries(s.map((x) => [x.id, x])) }),
  setTeamLeden: (teamId, leden) =>
    set((s) => ({ teamLeden: { ...s.teamLeden, [teamId]: leden } })),

  addTeam: (t) => set((s) => ({ teams: { ...s.teams, [t.id]: t } })),
  removeTeam: (id) =>
    set((s) => {
      const { [id]: _, ...rest } = s.teams;
      return { teams: rest };
    }),

  addWerknemer: (w) =>
    set((s) => ({ werknemers: { ...s.werknemers, [w.id]: w } })),
  updateWerknemer: (id, data) =>
    set((s) => ({
      werknemers: { ...s.werknemers, [id]: { ...s.werknemers[id], ...data } },
    })),
  removeWerknemer: (id) =>
    set((s) => {
      const { [id]: _, ...rest } = s.werknemers;
      return { werknemers: rest };
    }),

  setSupervisor: (teamId, werknemerId) =>
    set((s) => ({
      teamLeden: {
        ...s.teamLeden,
        [teamId]: (s.teamLeden[teamId] ?? []).map((l) =>
          l.werknemerId === werknemerId ? { ...l, isSupervisor: true } : l,
        ),
      },
    })),

  selectTeam: (id) => set({ selectedTeamId: id }),
  selectWerknemer: (id) => set({ selectedWerknemerId: id }),
  setFilterSite: (id) => set({ filterSiteId: id }),

  setLoaded: (b) => set({ loaded: b }),
  setLastSynced: (ts) => set({ lastSynced: ts }),
}));
