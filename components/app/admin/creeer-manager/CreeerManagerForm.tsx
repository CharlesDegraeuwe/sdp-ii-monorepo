'use client';

import React, { useState } from 'react';
import { useSession } from 'next-auth/react';
import { useRouter } from 'next/navigation';
import Input from '@/components/design-system/Input/Input';
import Button from '@/components/design-system/Button/Button';
import { Container } from '@/components/design-system/Container';

export default function CreeerManagerForm() {
  const { data: session } = useSession();
  const router = useRouter();
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [error, setError] = useState('');
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
    setError('');

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
    <Container label="Manager aanmaken" height="fit">
      <form
        onSubmit={handleSubmit}
        className="flex flex-col gap-4 max-w-lg pt-2"
      >
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
            onChange={(e) => setFormData({ ...formData, naam: e.target.value })}
            placeholder="Peeters"
            required
          />
        </div>

        <Input
          label="E-mailadres"
          type="email"
          value={formData.email}
          onChange={(e) => setFormData({ ...formData, email: e.target.value })}
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

        <p className="text-[10px] text-gray-400 italic px-3">
          Nieuwe accounts krijgen standaard het wachtwoord: Wachtwoord123
        </p>

        {error && <p className="text-red-500 text-sm px-3">{error}</p>}

        <Button
          label="Manager aanmaken"
          variant="secondary"
          type="submit"
          loading={isSubmitting}
        />
      </form>
    </Container>
  );
}
