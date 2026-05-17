import { describe, it, expect, beforeEach } from 'vitest';
import { useSidebarStore } from '@/stores/sidebarStore';

const initialState = useSidebarStore.getState();

beforeEach(() => {
  useSidebarStore.setState(initialState);
});

describe('useSidebarStore', () => {
  it('begint gesloten', () => {
    expect(useSidebarStore.getState().isMobileOpen).toBe(false);
  });

  it('toggle opent de sidebar', () => {
    useSidebarStore.getState().toggle();
    expect(useSidebarStore.getState().isMobileOpen).toBe(true);
  });

  it('toggle sluit de sidebar terug', () => {
    useSidebarStore.getState().toggle();
    useSidebarStore.getState().toggle();
    expect(useSidebarStore.getState().isMobileOpen).toBe(false);
  });

  it('close sluit de sidebar', () => {
    useSidebarStore.getState().toggle();
    useSidebarStore.getState().close();
    expect(useSidebarStore.getState().isMobileOpen).toBe(false);
  });

  it('close op al gesloten sidebar blijft false', () => {
    useSidebarStore.getState().close();
    expect(useSidebarStore.getState().isMobileOpen).toBe(false);
  });
});
