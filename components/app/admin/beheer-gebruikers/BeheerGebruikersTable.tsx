'use client';

import { useEffect, useState, useMemo } from 'react';
import { useSession } from 'next-auth/react';
import { useWerknemerStore } from '@/stores/werknemerStore';
import EditableCell from '@/components/app/admin/beheer-gebruikers/EditableCell';
import { WerknemerUser } from '@/types/types';
import { Container } from '@/components/design-system/Container';
import { useBeheerGebruikers } from '@/hooks/useBeheerGebruikers';
import { IoTrashOutline } from 'react-icons/io5';

const BeheerGebruikersTable = () => {
  const fetched = useBeheerGebruikers();
  const { data: session } = useSession();
  const { werknemers, setWerknemers, updateWerknemer } = useWerknemerStore();

  const [searchQuery, setSearchQuery] = useState('');
  const [sortConfig, setSortConfig] = useState<{
    key: keyof WerknemerUser;
    direction: 'asc' | 'desc';
  } | null>(null);

  useEffect(() => {
    if (fetched && fetched.length > 0) setWerknemers(fetched);
  }, [fetched, setWerknemers]);

  const handleUpdate = (id: string, patch: Partial<WerknemerUser>) => {
    const token = (session as unknown as { accessToken?: string })?.accessToken;
    if (!token) return;
    updateWerknemer(id, patch, token);
  };

  const handleDelete = async (id: string) => {
    const token = (session as unknown as { accessToken?: string })?.accessToken;
    if (!token) return;

    if (
      !window.confirm('Weet je zeker dat je deze gebruiker wilt verwijderen?')
    )
      return;

    try {
      const res = await fetch(`http://localhost:8080/api/werknemers/${id}`, {
        method: 'DELETE',
        headers: {
          Authorization: `Bearer ${token}`,
        },
      });

      if (res.ok) {
        setWerknemers(werknemers.filter((w) => String(w.id) !== String(id)));
      } else {
        const errorText = await res.text();
        alert(`Fout bij verwijderen: ${res.status} - ${errorText}`);
      }
    } catch (error) {
      console.error(error);
    }
  };

  const handleSort = (key: keyof WerknemerUser) => {
    let direction: 'asc' | 'desc' = 'asc';
    if (
      sortConfig &&
      sortConfig.key === key &&
      sortConfig.direction === 'asc'
    ) {
      direction = 'desc';
    }
    setSortConfig({ key, direction });
  };

  const filteredAndSortedWerknemers = useMemo(() => {
    let result = [...werknemers];

    if (searchQuery) {
      const lowerQuery = searchQuery.toLowerCase();
      result = result.filter(
        (w) =>
          (w.voornaam && w.voornaam.toLowerCase().includes(lowerQuery)) ||
          (w.naam && w.naam.toLowerCase().includes(lowerQuery)) ||
          (w.email && w.email.toLowerCase().includes(lowerQuery)),
      );
    }

    if (sortConfig !== null) {
      result.sort((a, b) => {
        const aValue = a[sortConfig.key]
          ? String(a[sortConfig.key]).toLowerCase()
          : '';
        const bValue = b[sortConfig.key]
          ? String(b[sortConfig.key]).toLowerCase()
          : '';

        if (aValue < bValue) return sortConfig.direction === 'asc' ? -1 : 1;
        if (aValue > bValue) return sortConfig.direction === 'asc' ? 1 : -1;
        return 0;
      });
    }

    return result;
  }, [werknemers, searchQuery, sortConfig]);

  // HIER ZIT DE FIX: We geven echte HEX kleuren terug voor alles!
  const getRoleConfig = (rol: string) => {
    const safeRol = (rol || '').toLowerCase();
    switch (safeRol) {
      case 'admin':
        return { bg: '#f3e8ff', border: '#e9d5ff', text: '#7e22ce' }; // Paars
      case 'manager':
        return { bg: '#dbeafe', border: '#bfdbfe', text: '#1d4ed8' }; // Blauw
      case 'werknemer':
      default:
        return { bg: '#f1f5f9', border: '#e2e8f0', text: '#334155' }; // Grijs
    }
  };

  const getStatusConfig = (status: string) => {
    const safeStatus = (status || '').toLowerCase();
    switch (safeStatus) {
      case 'actief':
        return { bg: '#d1fae5', border: '#a7f3d0', text: '#047857' }; // Groen
      case 'geblokkeerd':
        return { bg: '#fee2e2', border: '#fecaca', text: '#b91c1c' }; // Rood
      case 'inactief':
      default:
        return { bg: '#fef3c7', border: '#fde68a', text: '#b45309' }; // Oranje/Geel
    }
  };

  const renderSortableHeader = (label: string, key: keyof WerknemerUser) => {
    return (
      <th
        className="text-left font-semibold px-5 py-4 cursor-pointer hover:bg-gray-300/50 transition-colors select-none"
        onClick={() => handleSort(key)}
      >
        <div className="flex items-center gap-2">
          {label}
          {sortConfig?.key === key ? (
            <span className="text-xs text-gray-500">
              {sortConfig.direction === 'asc' ? '▲' : '▼'}
            </span>
          ) : (
            <span className="text-xs text-gray-400 opacity-50">↕</span>
          )}
        </div>
      </th>
    );
  };

  if (!werknemers || werknemers.length === 0) {
    return (
      <Container label="Werknemers" padding="8">
        <div className="text-center text-slate-500">
          Geen werknemers gevonden in de database.
        </div>
      </Container>
    );
  }

  return (
    <Container label="Werknemers" padding="0">
      <div className="flex flex-col h-full w-full overflow-hidden">
        <div className="pt-6 pb-4 shrink-0 z-10 flex justify-center items-center w-full">
          <input
            type="text"
            placeholder="Zoek op voornaam, achternaam of email..."
            value={searchQuery}
            onChange={(e) => setSearchQuery(e.target.value)}
            className="w-full max-w-3xl px-0 py-2 bg-transparent border-0 border-b-2 border-gray-300/60 focus:border-slate-600 focus:ring-0 outline-none transition-all text-base text-gray-700 text-center focus:text-left placeholder-gray-400"
          />
        </div>

        <div className="overflow-x-auto overflow-y-auto flex-1 w-full scroll-hidden relative">
          <table className="w-full text-sm">
            <thead className="text-slate-600 sticky top-0 z-20 bg-gray-200 shadow-sm">
              <tr className="border-b border-gray-300/40">
                {renderSortableHeader('Voornaam', 'voornaam')}
                {renderSortableHeader('Naam', 'naam')}
                {renderSortableHeader('Email', 'email')}
                {renderSortableHeader('Telefoon', 'telefoonnummer')}
                {renderSortableHeader('Geboortedatum', 'geboortedatum')}
                {renderSortableHeader('Rol', 'rol')}
                {renderSortableHeader('Status', 'status')}
                <th className="px-5 py-4"></th>
              </tr>
            </thead>
            <tbody>
              {filteredAndSortedWerknemers.length > 0 ? (
                filteredAndSortedWerknemers.map((w, i) => {
                  const roleConfig = getRoleConfig(w.rol || 'Werknemer');
                  const statusConfig = getStatusConfig(w.status || 'Inactief');

                  return (
                    <tr
                      key={w.id}
                      className={`hover:bg-gray-50 transition-colors ${
                        i !== filteredAndSortedWerknemers.length - 1
                          ? 'border-b border-gray-100'
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
                          onSave={(v) =>
                            handleUpdate(w.id, { telefoonnummer: v })
                          }
                        />
                      </td>
                      <td className="px-5 py-4 text-slate-600">
                        <EditableCell
                          value={w.geboortedatum?.split('T')[0] || ''}
                          type="date"
                          onSave={(v) =>
                            handleUpdate(w.id, { geboortedatum: v })
                          }
                        />
                      </td>
                      <td className="px-5 py-4">
                        {/* Inline styling forceert alle properties over de browser defaults heen! */}
                        <select
                          value={w.rol || 'Werknemer'}
                          onChange={(e) =>
                            handleUpdate(w.id, { rol: e.target.value })
                          }
                          className="rounded-full px-3 py-1.5 text-xs font-bold outline-none cursor-pointer shadow-sm transition-opacity hover:opacity-80"
                          style={{
                            backgroundColor: roleConfig.bg,
                            color: roleConfig.text,
                            borderColor: roleConfig.border,
                            borderWidth: '1px',
                          }}
                        >
                          <option value="Werknemer">Werknemer</option>
                          <option value="Manager">Manager</option>
                          <option value="Admin">Admin</option>
                        </select>
                      </td>
                      <td className="px-5 py-4">
                        <select
                          value={w.status || 'Inactief'}
                          onChange={(e) =>
                            handleUpdate(w.id, { status: e.target.value })
                          }
                          className="rounded-full px-3 py-1.5 text-xs font-bold outline-none cursor-pointer shadow-sm transition-opacity hover:opacity-80"
                          style={{
                            backgroundColor: statusConfig.bg,
                            color: statusConfig.text,
                            borderColor: statusConfig.border,
                            borderWidth: '1px',
                          }}
                        >
                          <option value="Actief">Actief</option>
                          <option value="Inactief">Inactief</option>
                          <option value="Geblokkeerd">Geblokkeerd</option>
                        </select>
                      </td>
                      <td className="px-5 py-4 text-center">
                        {session?.user?.email !== w.email && (
                          <button
                            onClick={() => handleDelete(w.id)}
                            className="p-2 text-gray-400 hover:text-red-600 hover:bg-red-50 rounded-full transition-all"
                            title="Verwijder gebruiker"
                          >
                            <IoTrashOutline className="w-5 h-5" />
                          </button>
                        )}
                      </td>
                    </tr>
                  );
                })
              ) : (
                <tr>
                  <td
                    colSpan={8}
                    className="px-5 py-8 text-center text-gray-500"
                  >
                    Geen resultaten gevonden voor &quot;{searchQuery}&quot;
                  </td>
                </tr>
              )}
            </tbody>
          </table>
        </div>
      </div>
    </Container>
  );
};

export default BeheerGebruikersTable;
