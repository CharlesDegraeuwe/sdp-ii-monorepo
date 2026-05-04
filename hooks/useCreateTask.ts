import { useTaakStore, Task } from '@/stores/taakStore';

export const useCreateTask = () => {
  const addTask = useTaakStore((s) => s.addTask);
  const removeTask = useTaakStore((s) => s.removeTask);

  return async (input: Omit<Task, 'id' | 'finished'>) => {
    const tempId = `temp-${Date.now()}`;
    const optimistic: Task = { ...input, id: tempId, finished: false };
    addTask(optimistic);

    try {
      const res = await fetch('/api/tasks', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(input),
      });
      if (!res.ok) throw new Error('Create failed');
      const saved: Task = await res.json();
      removeTask(tempId);
      addTask(saved);
      return saved;
    } catch (e) {
      removeTask(tempId);
      throw e;
    }
  };
};
