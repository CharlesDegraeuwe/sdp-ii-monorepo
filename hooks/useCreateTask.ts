import { useTaakStore, Task } from '@/stores/taakStore';
import { mapTaskToBackend, mapBackendTask } from '@/lib/taakMapper';

export const useCreateTask = () => {
  const addTask = useTaakStore((s) => s.addTask);
  const removeTask = useTaakStore((s) => s.removeTask);

  return async (input: Omit<Task, 'id' | 'finished'>) => {
    const tempId = `temp-${Date.now()}`;
    const optimistic: Task = { ...input, id: tempId, finished: false };
    addTask(optimistic);

    try {
      const res = await fetch('/api/taken', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(mapTaskToBackend(input)),
      });
      if (!res.ok) throw new Error('Create failed');
      const data = await res.json();
      removeTask(tempId);
      if (typeof data === 'object' && data.id) {
        addTask(mapBackendTask(data));
      }
      return data;
    } catch (e) {
      removeTask(tempId);
      throw e;
    }
  };
};
