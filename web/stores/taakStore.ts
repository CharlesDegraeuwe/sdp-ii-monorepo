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
  selectedTaskId: string | null;
  selectTask: (id: string | null) => void;
}

export const useTaakStore = create<TaakStore>((set) => ({
  selectedTaskId: null,
  selectTask: (id) => set({ selectedTaskId: id }),
}));
