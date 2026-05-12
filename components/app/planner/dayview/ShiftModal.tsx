'use client';

import { useState } from 'react';
import type { ModalForm } from './helpers';

export function ShiftModal({
  form,
  onSave,
  onClose,
}: {
  form: ModalForm;
  onSave: (f: ModalForm) => Promise<void>;
  onClose: () => void;
}) {
  const [f, setF] = useState(form);
  const [saving, setSaving] = useState(false);

  const isGeldig = f.startTijd && f.eindTijd && f.startDatum && f.eindDatum;

  async function handleSave() {
    if (!isGeldig) return;
    setSaving(true);
    try {
      await onSave(f);
      onClose();
    } finally {
      setSaving(false);
    }
  }

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/25 backdrop-blur-sm">
      <div className="bg-white rounded-2xl shadow-2xl p-6 w-84 flex flex-col gap-5">
        <div>
          <h3 className="font-bold text-zinc-900 text-sm">
            {form.shiftId ? 'Shift aanpassen' : 'Shift aanmaken'}
          </h3>
          <p className="text-[11px] text-zinc-500 mt-0.5">
            {form.werknemerNaam}
          </p>
        </div>

        <div className="flex gap-3">
          <div className="flex-1 flex flex-col gap-1">
            <label className="text-[9px] font-bold text-zinc-400 uppercase tracking-wide">
              Begintijd
            </label>
            <input
              type="time"
              value={f.startTijd}
              onChange={(e) =>
                setF((p) => ({ ...p, startTijd: e.target.value }))
              }
              className="border border-zinc-200 rounded-xl px-3 py-2 text-sm font-medium focus:outline-none focus:ring-1 focus:ring-zinc-400"
            />
          </div>
          <div className="flex-1 flex flex-col gap-1">
            <label className="text-[9px] font-bold text-zinc-400 uppercase tracking-wide">
              Eindtijd
            </label>
            <input
              type="time"
              value={f.eindTijd}
              onChange={(e) =>
                setF((p) => ({ ...p, eindTijd: e.target.value }))
              }
              className="border border-zinc-200 rounded-xl px-3 py-2 text-sm font-medium focus:outline-none focus:ring-1 focus:ring-zinc-400"
            />
          </div>
        </div>

        <div className="flex gap-3">
          <div className="flex-1 flex flex-col gap-1">
            <label className="text-[9px] font-bold text-zinc-400 uppercase tracking-wide">
              Pauze van
            </label>
            <input
              type="time"
              value={f.pauzeStart}
              onChange={(e) =>
                setF((p) => ({ ...p, pauzeStart: e.target.value }))
              }
              className="border border-zinc-200 rounded-xl px-3 py-2 text-sm font-medium focus:outline-none focus:ring-1 focus:ring-zinc-400"
            />
          </div>
          <div className="flex-1 flex flex-col gap-1">
            <label className="text-[9px] font-bold text-zinc-400 uppercase tracking-wide">
              Pauze tot
            </label>
            <input
              type="time"
              value={f.pauzeEind}
              onChange={(e) =>
                setF((p) => ({ ...p, pauzeEind: e.target.value }))
              }
              className="border border-zinc-200 rounded-xl px-3 py-2 text-sm font-medium focus:outline-none focus:ring-1 focus:ring-zinc-400"
            />
          </div>
        </div>

        <div className="flex gap-3">
          <div className="flex-1 flex flex-col gap-1">
            <label className="text-[9px] font-bold text-zinc-400 uppercase tracking-wide">
              Geldig van
            </label>
            <input
              type="date"
              value={f.startDatum}
              onChange={(e) =>
                setF((p) => ({ ...p, startDatum: e.target.value }))
              }
              className="border border-zinc-200 rounded-xl px-3 py-2 text-sm font-medium focus:outline-none focus:ring-1 focus:ring-zinc-400"
            />
          </div>
          <div className="flex-1 flex flex-col gap-1">
            <label className="text-[9px] font-bold text-zinc-400 uppercase tracking-wide">
              Geldig tot
            </label>
            <input
              type="date"
              value={f.eindDatum}
              onChange={(e) =>
                setF((p) => ({ ...p, eindDatum: e.target.value }))
              }
              className="border border-zinc-200 rounded-xl px-3 py-2 text-sm font-medium focus:outline-none focus:ring-1 focus:ring-zinc-400"
            />
          </div>
        </div>

        <div className="flex gap-2 justify-end pt-1">
          <button
            onClick={onClose}
            className="px-4 py-2 text-sm font-medium text-zinc-600 border border-zinc-200 rounded-xl hover:bg-zinc-50 transition-colors"
          >
            Annuleren
          </button>
          <button
            onClick={handleSave}
            disabled={saving || !isGeldig}
            className="px-4 py-2 text-sm font-bold text-white bg-zinc-900 rounded-xl hover:bg-zinc-700 disabled:opacity-40 transition-colors"
          >
            {saving ? 'Bezig...' : 'Opslaan'}
          </button>
        </div>
      </div>
    </div>
  );
}
