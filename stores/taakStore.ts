import { create } from 'zustand';

export interface TeamMember {
  id: string;
  firstName: string;
  lastName: string;
  email: string;
  color?: string;
}

export interface Team {
  id: string;
  name: string;
  plant: string;
  members: TeamMember[];
}

export interface Task {
  id: string;
  name: string;
  description?: string;
  specifications?: string;
  dueDate: string;
  duration?: string;
  location: string;
  important: boolean;
  finished: boolean;
  finishedAt?: string;
  assigneeId?: string;
}

interface TaakStore {
  teams: Record<string, Team>;
  members: Record<string, TeamMember>;
  tasks: Record<string, Task>;

  selectedTeamId: string | null;
  selectedMemberId: string | null;
  selectedTaskId: string | null;

  loaded: boolean;
  lastSynced: number | null;

  setTeams: (teams: Team[]) => void;
  setMembers: (members: TeamMember[]) => void;
  setTasks: (tasks: Task[]) => void;

  addTask: (task: Task) => void;
  updateTask: (id: string, data: Partial<Task>) => void;
  removeTask: (id: string) => void;
  assignTask: (taskId: string, memberId: string) => void;

  selectTeam: (id: string | null) => void;
  selectMember: (id: string | null) => void;
  selectTask: (id: string | null) => void;

  setLoaded: (loaded: boolean) => void;
  setLastSynced: (ts: number) => void;
}

export const useTaakStore = create<TaakStore>((set) => ({
  teams: {},
  members: {},
  tasks: {},

  selectedTeamId: null,
  selectedMemberId: null,
  selectedTaskId: null,

  loaded: false,
  lastSynced: null,

  setTeams: (teams) =>
    set(() => ({
      teams: Object.fromEntries(teams.map((t) => [t.id, t])),
    })),

  setMembers: (members) =>
    set(() => ({
      members: Object.fromEntries(members.map((m) => [m.id, m])),
    })),

  setTasks: (tasks) =>
    set(() => ({
      tasks: Object.fromEntries(tasks.map((t) => [t.id, t])),
    })),

  addTask: (task) => set((s) => ({ tasks: { ...s.tasks, [task.id]: task } })),

  updateTask: (id, data) =>
    set((s) => ({
      tasks: { ...s.tasks, [id]: { ...s.tasks[id], ...data } },
    })),

  removeTask: (id) =>
    set((s) => {
      const { [id]: _, ...rest } = s.tasks;
      return { tasks: rest };
    }),

  assignTask: (taskId, memberId) =>
    set((s) => ({
      tasks: {
        ...s.tasks,
        [taskId]: { ...s.tasks[taskId], assigneeId: memberId },
      },
    })),

  selectTeam: (id) => set({ selectedTeamId: id }),
  selectMember: (id) => set({ selectedMemberId: id }),
  selectTask: (id) => set({ selectedTaskId: id }),

  setLoaded: (loaded) => set({ loaded }),
  setLastSynced: (ts) => set({ lastSynced: ts }),
}));
