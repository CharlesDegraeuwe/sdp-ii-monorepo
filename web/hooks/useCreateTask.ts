import { useTaakStore, Task } from '@/stores/taakStore';
import { mapTaskToBackend, mapBackendTask } from '@/lib/taakMapper';

export const useCreateTask = () => {
  const addTask = useTaakStore((s) => s.addTask);
  const removeTask = useTaakStore((s) => s.removeTask);
  const setTasks = useTaakStore((s) => s.setTasks);
  const setLastSynced = useTaakStore((s) => s.setLastSynced);

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
      removeTask(tempId);

      // Refetch taken direct zodat de data meteen klaarstaat
      const takenRes = await fetch('/api/taken/alle');
      if (takenRes.ok) {
        const takenData = await takenRes.json();
        if (Array.isArray(takenData)) {
          const tasks = takenData.map(mapBackendTask);
          setTasks(tasks);
          setLastSynced(Date.now());
        }
      }
    } catch (e) {
      removeTask(tempId);
      throw e;
    }
  };
};
