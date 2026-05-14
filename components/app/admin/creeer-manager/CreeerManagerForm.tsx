'use client';

import React, { useState } from 'react';
import { useSession } from 'next-auth/react';
import { useRouter } from 'next/navigation';

export default function CreeerManagerForm() {
  const { data: session } = useSession();
  const router = useRouter();
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [formData, setFormData] = useState({
    voornaam: '',
    naam: '',
    email: '',
    wachtwoord: 'Wachtwoord123',
    telefoonnummer: '',
    geboortedatum: '',
    rol: 'Manager',
  });

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setIsSubmitting(true);

    const token = (session as unknown as { accessToken?: string })?.accessToken;

    try {
      const res = await fetch('http://localhost:8080/api/werknemers', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          Authorization: `Bearer ${token}`,
        },
        body: JSON.stringify(formData),
      });

      if (res.ok) {
        router.push('/admin/beheer-gebruikers');
      } else {
        const errorText = await res.text();
        alert(`Er is iets misgegaan: ${res.status} - ${errorText}`);
      }
    } catch (error) {
      console.error(error);
    } finally {
      setIsSubmitting(false);
    }
  };

  return (
    <form onSubmit={handleSubmit} className="flex flex-col gap-4 max-w-lg">
      <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
        <div>
          <label className="block text-xs font-semibold text-gray-600 mb-1">
            Voornaam
          </label>
          <input
            required
            type="text"
            value={formData.voornaam}
            onChange={(e) =>
              setFormData({ ...formData, voornaam: e.target.value })
            }
            className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 text-sm"
            placeholder="Jan"
          />
        </div>
        <div>
          <label className="block text-xs font-semibold text-gray-600 mb-1">
            Achternaam
          </label>
          <input
            required
            type="text"
            value={formData.naam}
            onChange={(e) => setFormData({ ...formData, naam: e.target.value })}
            className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 text-sm"
            placeholder="Peeters"
          />
        </div>
      </div>

      <div>
        <label className="block text-xs font-semibold text-gray-600 mb-1">
          E-mailadres
        </label>
        <input
          required
          type="email"
          value={formData.email}
          onChange={(e) => setFormData({ ...formData, email: e.target.value })}
          className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 text-sm"
          placeholder="jan.peeters@bedrijf.be"
        />
      </div>

      <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
        <div>
          <label className="block text-xs font-semibold text-gray-600 mb-1">
            Telefoonnummer
          </label>
          <input
            required
            type="tel"
            value={formData.telefoonnummer}
            onChange={(e) =>
              setFormData({ ...formData, telefoonnummer: e.target.value })
            }
            className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 text-sm"
            placeholder="0470 12 34 56"
          />
        </div>
        <div>
          <label className="block text-xs font-semibold text-gray-600 mb-1">
            Geboortedatum
          </label>
          <input
            required
            type="date"
            value={formData.geboortedatum}
            onChange={(e) =>
              setFormData({ ...formData, geboortedatum: e.target.value })
            }
            className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 text-sm text-gray-700"
          />
        </div>
      </div>

      <p className="text-[10px] text-gray-400 italic">
        Nieuwe accounts krijgen standaard het wachtwoord: Wachtwoord123
      </p>

      <button
        type="submit"
        disabled={isSubmitting}
        className="w-full bg-blue-600 hover:bg-blue-700 text-white font-medium py-2.5 rounded-lg transition-colors disabled:bg-blue-400"
      >
        {isSubmitting ? 'Bezig met aanmaken...' : 'Manager aanmaken'}
      </button>
    </form>
  );
}
