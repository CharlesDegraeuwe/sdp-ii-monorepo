'use client';

import { useState } from 'react';
import { IoCloseOutline } from 'react-icons/io5';
import Modal from '@/components/design-system/Modal/Modal';
import Input from '@/components/design-system/Input/Input';
import Select from '@/components/design-system/Select/Select';
import Button from '@/components/design-system/Button/Button';

type ModalType = 'verlof' | 'afwezigheid';

interface PlannerAanvraagModalProps {
  type: ModalType;
  datum: Date;
  werknemerId: number;
  onClose: () => void;
  onSuccess?: () => void;
}

const VERLOF_TYPE_OPTIONS = [
  { value: 'Jaarlijks verlof', label: 'Jaarlijks verlof' },
  { value: 'Onbetaald verlof', label: 'Onbetaald verlof' },
  { value: 'Bijzonder verlof', label: 'Bijzonder verlof' },
  { value: 'Omstandigheidsverlof', label: 'Omstandigheidsverlof' },
];

export function PlannerAanvraagModal({
  type,
  datum,
  werknemerId,
  onClose,
  onSuccess,
}: PlannerAanvraagModalProps) {
  const datumStr = `${datum.getFullYear()}-${String(datum.getMonth() + 1).padStart(2, '0')}-${String(datum.getDate()).padStart(2, '0')}`;
  const [startDatum, setStartDatum] = useState(datumStr);
  const [eindDatum, setEindDatum] = useState(datumStr);
  const [verlofType, setVerlofType] = useState(VERLOF_TYPE_OPTIONS[0].value);
  const [reden, setReden] = useState('');
  const [bezig, setBezig] = useState(false);
  const [fout, setFout] = useState<string | null>(null);

  async function handleSubmit(e: React.FormEvent) {
    e.preventDefault();
    setBezig(true);
    setFout(null);

    try {
      const url = type === 'verlof' ? '/api/verlof' : '/api/afwezigheid';
      const body =
        type === 'verlof'
          ? { werknemerId, startDatum, eindDatum, type: verlofType }
          : { werknemerId, startDatum, eindDatum, reden, certificaat: null };

      const res = await fetch(url, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(body),
      });

      if (!res.ok) throw new Error('Mislukt');
      onSuccess?.();
      onClose();
    } catch {
      setFout('Er is iets misgegaan. Probeer opnieuw.');
    } finally {
      setBezig(false);
    }
  }

  return (
    <Modal onClose={onClose}>
      <div className="bg-white w-full max-w-md rounded-4xl shadow-2xl overflow-hidden flex flex-col mx-4">
        <div className="px-6 pt-5 pb-4 border-b border-gray-100 flex justify-between items-center">
          <span className="font-bold text-slate-800 text-lg">
            {type === 'verlof' ? 'Verlof aanvragen' : 'Afwezigheid melden'}
          </span>
          <button
            onClick={onClose}
            className="text-gray-400 hover:text-red-500 transition-colors p-1"
          >
            <IoCloseOutline className="w-6 h-6" />
          </button>
        </div>

        <form onSubmit={handleSubmit} className="p-6 flex flex-col gap-4">
          <div className="grid grid-cols-2 gap-4">
            <Input
              label="Van"
              type="date"
              value={startDatum}
              onChange={(e) => setStartDatum(e.target.value)}
              required
            />
            <Input
              label="Tot"
              type="date"
              value={eindDatum}
              onChange={(e) => setEindDatum(e.target.value)}
              min={startDatum}
              required
            />
          </div>

          {type === 'verlof' ? (
            <Select
              label="Type verlof"
              options={VERLOF_TYPE_OPTIONS}
              value={verlofType}
              onChange={(v) => setVerlofType(String(v))}
              size="md"
            />
          ) : (
            <Input
              label="Reden"
              type="text"
              value={reden}
              onChange={(e) => setReden(e.target.value)}
              placeholder="Bijv. griep, doktersbezoek..."
            />
          )}

          {fout && <p className="text-red-500 text-sm px-3">{fout}</p>}

          <div className="flex gap-3">
            <Button
              label="Annuleren"
              variant="outline"
              size="md"
              type="button"
              onClick={onClose}
            />
            <Button
              label={type === 'verlof' ? 'Aanvragen' : 'Melden'}
              variant="primary"
              size="md"
              type="submit"
              loading={bezig}
            />
          </div>
        </form>
      </div>
    </Modal>
  );
}
