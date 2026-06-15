import { describe, it, expect, beforeEach } from 'vitest';
import { useTaakStore } from '@/stores/taakStore';

const initialState = useTaakStore.getState();

beforeEach(() => {
  useTaakStore.setState(initialState);
});

describe('useTaakStore - selectie', () => {
  it('selecteert een taak', () => {
    useTaakStore.getState().selectTask('1');
    expect(useTaakStore.getState().selectedTaskId).toBe('1');
  });

  it('kan selectie op null zetten', () => {
    useTaakStore.getState().selectTask('1');
    useTaakStore.getState().selectTask(null);
    expect(useTaakStore.getState().selectedTaskId).toBeNull();
  });

  it('begint zonder geselecteerde taak', () => {
    expect(useTaakStore.getState().selectedTaskId).toBeNull();
  });
});
