import { Werknemer, useTeamsStore } from '@/stores/teamStore';

export const useCreateEmployee = () => {
  const addWerknemer = useTeamsStore((s) => s.addWerknemer);
  const removeWerknemer = useTeamsStore((s) => s.removeWerknemer);

  return async (
    input: Omit<
      Werknemer,
      'id' | 'role' | 'status' | 'siteId' | 'siteNaam' | 'color'
    >,
  ) => {
    const tempId = -Date.now();
    const optimistic: Werknemer = {
      ...input,
      id: tempId,
      siteId: 0,
      siteNaam: '',
      role: 'employee',
      status: 'Actief',
    };
    addWerknemer(optimistic);

    try {
      const res = await fetch('/api/employees', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(input),
      });
      if (!res.ok) throw new Error();
      const saved: Werknemer = await res.json();
      removeWerknemer(tempId);
      addWerknemer(saved);
      return saved;
    } catch (e) {
      removeWerknemer(tempId);
      throw e;
    }
  };
};
