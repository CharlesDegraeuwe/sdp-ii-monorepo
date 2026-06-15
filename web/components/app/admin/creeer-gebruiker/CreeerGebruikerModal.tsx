import React, { useState } from 'react';
import { IoCloseOutline } from 'react-icons/io5';
import Modal from '@/components/design-system/Modal/Modal';
import Input from '@/components/design-system/Input/Input';
import Select from '@/components/design-system/Select/Select';
import Button from '@/components/design-system/Button/Button';

const rolOptions = [
  { label: 'Werknemer', value: 'Werknemer' },
  { label: 'Manager', value: 'Manager' },
  { label: 'Admin', value: 'Admin' },
];

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
  const [error, setError] = useState('');
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
    setError('');

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
        setError(`Er is iets misgegaan: ${res.status} - ${errorText}`);
      }
    } catch (err) {
      console.error(err);
      setError('Er is een onverwachte fout opgetreden.');
    } finally {
      setIsSubmitting(false);
    }
  };

  return (
    <Modal onClose={onClose}>
      <div className="bg-white w-full max-w-lg rounded-4xl shadow-2xl overflow-hidden flex flex-col mx-4">
        <div className="px-6 pt-5 pb-4 border-b border-gray-100 flex justify-between items-center">
          <span className="font-bold text-slate-800 text-lg">
            Nieuwe Gebruiker
          </span>
          <button
            onClick={onClose}
            className="text-gray-400 hover:text-red-500 transition-colors p-1"
          >
            <IoCloseOutline className="w-6 h-6" />
          </button>
        </div>

        <form onSubmit={handleCreateUser} className="p-6 flex flex-col gap-4">
          <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
            <Input
              label="Voornaam"
              type="text"
              value={formData.voornaam}
              onChange={(e) =>
                setFormData({ ...formData, voornaam: e.target.value })
              }
              placeholder="Jan"
              required
            />
            <Input
              label="Achternaam"
              type="text"
              value={formData.naam}
              onChange={(e) =>
                setFormData({ ...formData, naam: e.target.value })
              }
              placeholder="Peeters"
              required
            />
          </div>

          <Input
            label="E-mailadres"
            type="email"
            value={formData.email}
            onChange={(e) =>
              setFormData({ ...formData, email: e.target.value })
            }
            placeholder="jan.peeters@bedrijf.be"
            required
          />

          <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
            <Input
              label="Telefoonnummer"
              type="tel"
              value={formData.telefoonnummer}
              onChange={(e) =>
                setFormData({ ...formData, telefoonnummer: e.target.value })
              }
              placeholder="0470 12 34 56"
              required
            />
            <Input
              label="Geboortedatum"
              type="date"
              value={formData.geboortedatum}
              onChange={(e) =>
                setFormData({ ...formData, geboortedatum: e.target.value })
              }
              required
            />
          </div>

          <Select
            label="Rol"
            options={rolOptions}
            value={formData.rol}
            onChange={(v) => setFormData({ ...formData, rol: String(v) })}
            size="md"
          />

          <p className="text-[10px] text-gray-400 italic px-3">
            Nieuwe accounts krijgen standaard het wachtwoord: Wachtwoord123
          </p>

          {error && <p className="text-red-500 text-sm px-3">{error}</p>}

          <Button
            label="Gebruiker aanmaken"
            variant="secondary"
            type="submit"
            loading={isSubmitting}
          />
        </form>
      </div>
    </Modal>
  );
}
