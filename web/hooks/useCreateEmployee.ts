import { useTeamsStore, Werknemer } from '@/stores/teamStore';

interface CreateEmployeeInput {
  voornaam: string;
  naam: string;
  email: string;
  telefoonnummer: string;
  geboortedatum?: string;
}

export const useCreateEmployee = () => {
  const addWerknemer = useTeamsStore((s) => s.addWerknemer);
  const removeWerknemer = useTeamsStore((s) => s.removeWerknemer);
  const setLastSynced = useTeamsStore((s) => s.setLastSynced);

  return async (input: CreateEmployeeInput) => {
    const tempId = -Date.now();
    const optimistic: Werknemer = {
      id: tempId,
      voornaam: input.voornaam,
      naam: input.naam,
      email: input.email,
      telefoon: input.telefoonnummer,
      beschikbaarheid: '',
      siteId: 0,
      siteNaam: '',
      role: 'Werknemer',
      status: 'Actief',
    };
    addWerknemer(optimistic);

    try {
      const res = await fetch('/api/werknemers', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          naam: input.naam,
          voornaam: input.voornaam,
          email: input.email,
          telefoonnummer: input.telefoonnummer,
          geboortedatum: input.geboortedatum || null,
          rol: 'Werknemer',
        }),
      });
      if (!res.ok) throw new Error('Create failed');

      // Backend returns a string, not a werknemer object.
      // Invalidate cache so werknemers are refetched.
      removeWerknemer(tempId);
      setLastSynced(0);
    } catch (e) {
      removeWerknemer(tempId);
      throw e;
    }
  };
};
