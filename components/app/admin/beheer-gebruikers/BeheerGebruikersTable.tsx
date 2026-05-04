'use client';

import { useEffect } from 'react';
import { useSession } from 'next-auth/react';
import { useWerknemerStore } from '@/stores/werknemerStore';
import EditableCell from '@/components/app/admin/beheer-gebruikers/EditableCell';
import { WerknemerUser } from '@/types/types';
import { useBeheerGebruikers } from '@/components/app/admin/beheer-gebruikers/useBeheerGebruikers';
import { Container } from '@/components/design system/Container';
import { User } from 'next-auth';

const BeheerGebruikersTable = () => {
  const fetched = useBeheerGebruikers();
  const { data: session } = useSession();
  const { werknemers, setWerknemers, updateWerknemer } = useWerknemerStore();

  useEffect(() => {
    if (fetched && fetched.length > 0) setWerknemers(fetched);
  }, [fetched, setWerknemers]);

  const handleUpdate = (id: string, patch: Partial<WerknemerUser>) => {
    const token = (session?.user as User)?.accessToken;
    if (!token) return;
    updateWerknemer(id, patch, token);
  };

  if (!werknemers || werknemers.length === 0) {
    return (
      <Container label="Werknemers" padding="8">
        <div className="text-center text-slate-500">
          Geen werknemers gevonden
        </div>
      </Container>
    );
  }

  return (
    <Container label="Werknemers" padding="0">
      <div className="overflow-x-auto overflow-y-scroll scroll-hidden w-full h-full">
        <table className="relative w-full text-sm scroll-hidden">
          <thead className="text-slate-600 sticky  top-0 backdrop-blur-2xl z-100 bg-gray-300/30">
            <tr className="border-b border-gray-300/40">
              <th className="text-left font-semibold px-5 py-4">Voornaam</th>
              <th className="text-left font-semibold px-5 py-4">Naam</th>
              <th className="text-left font-semibold px-5 py-4">Email</th>
              <th className="text-left font-semibold px-5 py-4">Telefoon</th>
              <th className="text-left font-semibold px-5 py-4">
                Geboortedatum
              </th>
              <th className="text-left font-semibold px-5 py-4">Status</th>
            </tr>
          </thead>
          <tbody>
            {werknemers.map((w, i) => (
              <tr
                key={w.id}
                className={`hover:bg-white/20 transition-colors ${
                  i !== werknemers.length - 1
                    ? 'border-b border-gray-300/20'
                    : ''
                }`}
              >
                <td className="px-5 py-4 font-medium text-slate-900">
                  <EditableCell
                    value={w.voornaam}
                    onSave={(v) => handleUpdate(w.id, { voornaam: v })}
                  />
                </td>
                <td className="px-5 py-4 font-medium text-slate-900">
                  <EditableCell
                    value={w.naam}
                    onSave={(v) => handleUpdate(w.id, { naam: v })}
                  />
                </td>
                <td className="px-5 py-4 text-slate-600">
                  <EditableCell
                    value={w.email}
                    type="email"
                    onSave={(v) => handleUpdate(w.id, { email: v })}
                  />
                </td>
                <td className="px-5 py-4 text-slate-600">
                  <EditableCell
                    value={w.telefoonnummer}
                    onSave={(v) => handleUpdate(w.id, { telefoonnummer: v })}
                  />
                </td>
                <td className="px-5 py-4 text-slate-600">
                  <EditableCell
                    value={w.geboortedatum.split('T')[0]}
                    type="date"
                    onSave={(v) => handleUpdate(w.id, { geboortedatum: v })}
                  />
                </td>
                <td className="px-5 py-4">
                  <select
                    value={w.status}
                    onChange={(e) =>
                      handleUpdate(w.id, { status: e.target.value })
                    }
                    className="bg-white/60 backdrop-blur-sm border border-gray-300/40 rounded-full px-3 py-1 text-xs font-medium text-slate-700 outline-none cursor-pointer hover:border-gray-400/60"
                  >
                    <option value="ACTIEF">Actief</option>
                    <option value="INACTIEF">Inactief</option>
                  </select>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </Container>
  );
};

export default BeheerGebruikersTable;
