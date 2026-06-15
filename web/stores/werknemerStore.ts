import { create } from 'zustand';
import { WerknemerUser } from '@/types/types';

interface UpdateWerknemerPayload {
  naam?: string;
  voornaam?: string;
  email?: string;
  telefoonnummer?: string;
  geboortedatum?: string;
  status?: string;
  rol?: string;
}

interface WerknemerStore {
  werknemers: WerknemerUser[];
  setWerknemers: (w: WerknemerUser[]) => void;
  updateWerknemer: (
    id: string,
    patch: UpdateWerknemerPayload,
    accessToken: string,
  ) => Promise<void>;
}

export const useWerknemerStore = create<WerknemerStore>((set, get) => ({
  werknemers: [],
  setWerknemers: (werknemers) => set({ werknemers }),

  updateWerknemer: async (id, patch, accessToken) => {
    const previous = get().werknemers;
    const target = previous.find((w) => w.id === id);
    if (!target) return;

    set({
      werknemers: previous.map((w) => (w.id === id ? { ...w, ...patch } : w)),
    });

    try {
      const res = await fetch(`http://localhost:8080/api/werknemers/${id}`, {
        method: 'PUT',

        headers: {
          'Content-Type': 'application/json',
          Authorization: `Bearer ${accessToken}`,
        },
        body: JSON.stringify({
          naam: patch.naam ?? target.naam,
          voornaam: patch.voornaam ?? target.voornaam,
          email: patch.email ?? target.email,
          telefoonnummer: patch.telefoonnummer ?? target.telefoonnummer,
          geboortedatum: patch.geboortedatum ?? target.geboortedatum,
          status: patch.status ?? target.status,
          rol: patch.rol ?? target.rol,
        }),
      });

      if (!res.ok) throw new Error(`PUT failed: ${res.status}`);

      const updated: WerknemerUser = await res.json();
      set({
        werknemers: get().werknemers.map((w) =>
          w.id === id ? { ...w, ...updated } : w,
        ),
      });
    } catch (err) {
      console.error('updateWerknemer failed, rolling back', err);
      set({ werknemers: previous });
      throw err;
    }
  },
}));
