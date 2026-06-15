import { create } from 'zustand';

interface SidebarStore {
  isMobileOpen: boolean;
  toggle: () => void;
  close: () => void;
}

export const useSidebarStore = create<SidebarStore>((set) => ({
  isMobileOpen: false,
  toggle: () => set((state) => ({ isMobileOpen: !state.isMobileOpen })),
  close: () => set({ isMobileOpen: false }),
}));
