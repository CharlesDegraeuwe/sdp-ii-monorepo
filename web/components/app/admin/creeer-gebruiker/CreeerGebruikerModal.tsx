import React, { useState } from 'react';
import { IoCloseOutline } from 'react-icons/io5';

interface CreeerGebruikerModalProps {
  isOpen: boolean;
  onClose: () => void;
  onSuccess: () => void;
  jwtToken: string;
}

export default function CreeerGebruikerModal({
  isOpen,
  onClose,
  onSuccess,
  jwtToken,
}: CreeerGebruikerModalProps) {
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [formData, setFormData] = useState({
    voornaam: '',
    naam: '',
    email: '',
    wachtwoord: 'Wachtwoord123',
    telefoonnummer: '',
    geboortedatum: '',
    rol: 'Werknemer',
  });

  if (!isOpen) return null;

  const handleCreateUser = async (e: React.FormEvent) => {
    e.preventDefault();
    setIsSubmitting(true);

    try {
      const res = await fetch('http://localhost:8080/api/werknemers', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          Authorization: `Bearer ${jwtToken}`,
        },
        body: JSON.stringify(formData),
      });

      if (res.ok) {
        setFormData({
          voornaam: '',
          naam: '',
          email: '',
          wachtwoord: 'Wachtwoord123',
          telefoonnummer: '',
          geboortedatum: '',
          rol: 'Werknemer',
        });
        onSuccess();
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
    <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/40 backdrop-blur-sm p-4">
      <div className="bg-white w-full max-w-lg rounded-2xl shadow-xl overflow-hidden flex flex-col">
        <div className="p-5 border-b border-gray-100 flex justify-between items-center bg-slate-50">
          <h3 className="font-bold text-slate-800 text-lg">Nieuwe Gebruiker</h3>
          <button
            onClick={onClose}
            className="text-gray-400 hover:text-red-500 transition-colors"
          >
            <IoCloseOutline className="w-6 h-6" />
          </button>
        </div>

        <form onSubmit={handleCreateUser} className="p-6 flex flex-col gap-4">
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
                onChange={(e) =>
                  setFormData({ ...formData, naam: e.target.value })
                }
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
              onChange={(e) =>
                setFormData({ ...formData, email: e.target.value })
              }
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

          <div>
            <label className="block text-xs font-semibold text-gray-600 mb-1">
              Rol
            </label>
            <select
              value={formData.rol}
              onChange={(e) =>
                setFormData({ ...formData, rol: e.target.value })
              }
              className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 text-sm bg-white"
            >
              <option value="Werknemer">Werknemer</option>
              <option value="Manager">Manager</option>
              <option value="Admin">Admin</option>
            </select>
            <p className="text-[10px] text-gray-400 mt-1.5 italic">
              Nieuwe accounts krijgen standaard het wachtwoord: Wachtwoord123
            </p>
          </div>

          <button
            type="submit"
            disabled={isSubmitting}
            className="mt-4 w-full bg-blue-600 hover:bg-blue-700 text-white font-medium py-2.5 rounded-lg transition-colors disabled:bg-blue-400"
          >
            {isSubmitting ? 'Bezig met aanmaken...' : 'Gebruiker Aanmaken'}
          </button>
        </form>
      </div>
    </div>
  );
}
