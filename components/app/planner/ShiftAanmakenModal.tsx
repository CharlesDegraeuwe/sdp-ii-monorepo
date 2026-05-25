'use client';

import { useState } from 'react';
import Modal from '@/components/design-system/Modal/Modal';
import { Button } from '@/components/design-system/Button';
import { Label } from '@/components/design-system/Label';
import Select from '@/components/design-system/Select/Select';
import { useTaken } from '@/hooks/useTaken';
import { useQueryClient } from '@tanstack/react-query';
import { TAKEN_KEY } from '@/hooks/useTaken';
import type { TeamOptie, WerknemerMetTeam } from '@/hooks/usePlanningFilters';
import { STANDAARD_TIJDEN, toBackendTime } from './dayview/helpers';
import { HiOutlineClipboardDocumentList } from 'react-icons/hi2';

type Stap = 1 | 2 | 3;
type WieKeuze = 'eigen' | 'teamlid';

interface Props {
  eigenId: number;
  eigenVoornaam: string;
  eigenNaam: string;
  werknemers: WerknemerMetTeam[];
  teams: TeamOptie[];
  isManager: boolean;
  huidigeDatum: Date;
  onClose: () => void;
  onSuccess: (werknemerId: number) => void;
}

export function ShiftAanmakenModal({
  eigenId,
  eigenVoornaam,
  eigenNaam,
  werknemers,
  teams,
  isManager,
  huidigeDatum,
  onClose,
  onSuccess,
}: Props) {
  const { data: alleTaken = [] } = useTaken();
  const queryClient = useQueryClient();

  const vandaag = huidigeDatum.toISOString().split('T')[0];

  const [stap, setStap] = useState<Stap>(1);
  const [wieKeuze, setWieKeuze] = useState<WieKeuze>('eigen');
  const [geselecteerdeTeamId, setGeselecteerdeTeamId] = useState<number | null>(null);
  const [geselecteerdeWerknemerId, setGeselecteerdeWerknemerId] = useState<number | null>(null);

  const beschikbareWerknemers = isManager && geselecteerdeTeamId !== null
    ? werknemers.filter((w) => w.teamId === geselecteerdeTeamId)
    : werknemers;

  const [startDatum, setStartDatum] = useState(vandaag);
  const [eindDatum, setEindDatum] = useState(vandaag);
  const [startTijd, setStartTijd] = useState<string>(STANDAARD_TIJDEN.startTijd);
  const [eindTijd, setEindTijd] = useState<string>(STANDAARD_TIJDEN.eindTijd);
  const [pauzeStart, setPauzeStart] = useState<string>(STANDAARD_TIJDEN.pauzeStart);
  const [pauzeEind, setPauzeEind] = useState<string>(STANDAARD_TIJDEN.pauzeEind);

  const [geselecteerdeTaken, setGeselecteerdeTaken] = useState<Set<string>>(new Set());
  const [bezig, setBezig] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const werknemerId = wieKeuze === 'eigen' ? eigenId : geselecteerdeWerknemerId;
  const geselecteerdeWerknemer = werknemers.find((w) => w.id === geselecteerdeWerknemerId);
  const werknemerLabel =
    wieKeuze === 'eigen'
      ? `${eigenVoornaam} ${eigenNaam}`
      : geselecteerdeWerknemer
        ? `${geselecteerdeWerknemer.voornaam} ${geselecteerdeWerknemer.naam}`
        : '';

  const onbezetteTaken = alleTaken.filter((t) => !t.assigneeId && !t.finished);

  const kanNaarStap2 =
    wieKeuze === 'eigen' ||
    (isManager
      ? geselecteerdeTeamId !== null && geselecteerdeWerknemerId !== null
      : geselecteerdeWerknemerId !== null);
  const kanNaarStap3 = startDatum && eindDatum && startTijd && eindTijd;

  function toggleTaak(taakId: string) {
    setGeselecteerdeTaken((prev) => {
      const next = new Set(prev);
      if (next.has(taakId)) next.delete(taakId);
      else next.add(taakId);
      return next;
    });
  }

  async function handleAanmaken(metTaken: boolean) {
    if (!werknemerId || !kanNaarStap3) return;
    setBezig(true);
    setError(null);

    try {
      const res = await fetch('/api/shifts', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          werknemerId,
          startDatum,
          eindDatum,
          startTijd: toBackendTime(startTijd),
          eindTijd: toBackendTime(eindTijd),
          pauzeStart: pauzeStart ? toBackendTime(pauzeStart) : null,
          pauzeEind: pauzeEind ? toBackendTime(pauzeEind) : null,
        }),
      });
      if (!res.ok) throw new Error('Shift aanmaken mislukt');

      if (metTaken && geselecteerdeTaken.size > 0) {
        await Promise.all(
          [...geselecteerdeTaken].map((taakId) =>
            fetch(`/api/taken/${taakId}/toewijzen`, {
              method: 'PUT',
              headers: { 'Content-Type': 'application/json' },
              body: JSON.stringify({ werknemerId }),
            }),
          ),
        );
        void queryClient.invalidateQueries({ queryKey: TAKEN_KEY });
      }

      onSuccess(werknemerId);
      onClose();
    } catch (e) {
      setError(e instanceof Error ? e.message : 'Er ging iets mis');
    } finally {
      setBezig(false);
    }
  }

  const stapLabel = stap === 1 ? 'Voor wie?' : stap === 2 ? 'Shift details' : 'Taken toevoegen';

  return (
    <Modal onClose={onClose}>
      <div className="bg-white rounded-3xl shadow-2xl w-full max-w-md mx-4 flex flex-col overflow-hidden">
        {/* Header */}
        <div className="flex items-start justify-between px-6 pt-6 pb-4 border-b border-zinc-100">
          <div>
            <h2 className="font-bold text-zinc-900 text-base">Shift aanmaken</h2>
            <p className="text-xs text-zinc-400 mt-0.5">
              Stap {stap} van 3 — {stapLabel}
            </p>
          </div>
          <div className="flex gap-1.5 mt-1">
            {([1, 2, 3] as Stap[]).map((s) => (
              <div
                key={s}
                className={`w-2 h-2 rounded-full transition-colors ${stap >= s ? 'bg-zinc-800' : 'bg-zinc-200'}`}
              />
            ))}
          </div>
        </div>

        {/* Stap 1: Voor wie */}
        {stap === 1 && (
          <div className="px-6 py-5 flex flex-col gap-4">
            <p className="text-sm text-zinc-500">
              Kies voor wie je een shift wil aanmaken.
            </p>
            <div className="flex flex-col gap-2">
              {(
                [
                  { key: 'eigen', label: 'Eigen planning', sub: `${eigenVoornaam} ${eigenNaam}` },
                  { key: 'teamlid', label: 'Teamlid', sub: 'Kies een werknemer' },
                ] as { key: WieKeuze; label: string; sub: string }[]
              ).map(({ key, label, sub }) => (
                <button
                  key={key}
                  onClick={() => setWieKeuze(key)}
                  className={`flex items-center gap-3 px-4 py-3 rounded-2xl text-sm text-left transition border ${
                    wieKeuze === key
                      ? 'border-zinc-800 bg-zinc-50'
                      : 'border-zinc-200 hover:bg-zinc-50'
                  }`}
                >
                  <span
                    className={`w-3 h-3 rounded-full border-2 flex-shrink-0 transition ${
                      wieKeuze === key ? 'bg-zinc-800 border-zinc-800' : 'border-zinc-300'
                    }`}
                  />
                  <div>
                    <p className={wieKeuze === key ? 'font-medium' : ''}>{label}</p>
                    <p className="text-[11px] text-zinc-400 mt-0.5">{sub}</p>
                  </div>
                </button>
              ))}
            </div>
            {wieKeuze === 'teamlid' && isManager && (
              <Select
                label="Team"
                placeholder="Selecteer een team"
                options={teams.map((t) => ({ value: String(t.id), label: t.naam }))}
                value={geselecteerdeTeamId !== null ? String(geselecteerdeTeamId) : undefined}
                onChange={(v) => {
                  setGeselecteerdeTeamId(Number(v));
                  setGeselecteerdeWerknemerId(null);
                }}
              />
            )}
            {wieKeuze === 'teamlid' && (!isManager || geselecteerdeTeamId !== null) && (
              <Select
                label="Teamlid"
                placeholder="Selecteer een teamlid"
                options={beschikbareWerknemers.map((w) => ({
                  value: String(w.id),
                  label: isManager
                    ? `${w.voornaam} ${w.naam}`
                    : `${w.voornaam} ${w.naam} — ${w.teamNaam}`,
                }))}
                value={geselecteerdeWerknemerId !== null ? String(geselecteerdeWerknemerId) : undefined}
                onChange={(v) => setGeselecteerdeWerknemerId(Number(v))}
              />
            )}
          </div>
        )}

        {/* Stap 2: Shift details */}
        {stap === 2 && (
          <div className="px-6 py-5 flex flex-col gap-4">
            <div className="text-xs bg-zinc-100 rounded-full px-3 py-1 font-medium text-zinc-700 self-start">
              {werknemerLabel}
            </div>
            <div className="flex gap-3">
              <div className="flex-1 flex flex-col gap-1">
                <label className="text-[9px] font-bold text-zinc-400 uppercase tracking-wide">
                  Begintijd
                </label>
                <input
                  type="time"
                  value={startTijd}
                  onChange={(e) => setStartTijd(e.target.value)}
                  className="border border-zinc-200 rounded-xl px-3 py-2 text-sm font-medium focus:outline-none focus:ring-1 focus:ring-zinc-400"
                />
              </div>
              <div className="flex-1 flex flex-col gap-1">
                <label className="text-[9px] font-bold text-zinc-400 uppercase tracking-wide">
                  Eindtijd
                </label>
                <input
                  type="time"
                  value={eindTijd}
                  onChange={(e) => setEindTijd(e.target.value)}
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
                  value={pauzeStart}
                  onChange={(e) => setPauzeStart(e.target.value)}
                  className="border border-zinc-200 rounded-xl px-3 py-2 text-sm font-medium focus:outline-none focus:ring-1 focus:ring-zinc-400"
                />
              </div>
              <div className="flex-1 flex flex-col gap-1">
                <label className="text-[9px] font-bold text-zinc-400 uppercase tracking-wide">
                  Pauze tot
                </label>
                <input
                  type="time"
                  value={pauzeEind}
                  onChange={(e) => setPauzeEind(e.target.value)}
                  className="border border-zinc-200 rounded-xl px-3 py-2 text-sm font-medium focus:outline-none focus:ring-1 focus:ring-zinc-400"
                />
              </div>
            </div>
            <div className="flex gap-3">
              <div className="flex-1 flex flex-col gap-1">
                <label className="text-[9px] font-bold text-zinc-400 uppercase tracking-wide">
                  Datum van
                </label>
                <input
                  type="date"
                  value={startDatum}
                  onChange={(e) => setStartDatum(e.target.value)}
                  className="border border-zinc-200 rounded-xl px-3 py-2 text-sm font-medium focus:outline-none focus:ring-1 focus:ring-zinc-400"
                />
              </div>
              <div className="flex-1 flex flex-col gap-1">
                <label className="text-[9px] font-bold text-zinc-400 uppercase tracking-wide">
                  Datum tot
                </label>
                <input
                  type="date"
                  value={eindDatum}
                  onChange={(e) => setEindDatum(e.target.value)}
                  className="border border-zinc-200 rounded-xl px-3 py-2 text-sm font-medium focus:outline-none focus:ring-1 focus:ring-zinc-400"
                />
              </div>
            </div>
          </div>
        )}

        {/* Stap 3: Taken */}
        {stap === 3 && (
          <div className="px-6 py-5 flex flex-col gap-3">
            <p className="text-sm text-zinc-500">
              Wijs direct taken toe aan{' '}
              <span className="font-medium text-zinc-800">{werknemerLabel}</span>.
              Dit is optioneel.
            </p>
            {onbezetteTaken.length === 0 ? (
              <div className="flex items-center justify-center py-10">
                <Label text="Geen onbezette taken beschikbaar" variant="emptystate" />
              </div>
            ) : (
              <div className="flex flex-col gap-1.5 max-h-56 overflow-y-auto pr-1">
                {onbezetteTaken.map((t) => {
                  const selected = geselecteerdeTaken.has(t.id);
                  return (
                    <button
                      key={t.id}
                      onClick={() => toggleTaak(t.id)}
                      className={`flex items-center gap-3 px-4 py-2.5 rounded-2xl text-sm text-left transition border ${
                        selected
                          ? 'border-zinc-800 bg-zinc-50'
                          : 'border-zinc-200 hover:bg-zinc-50'
                      }`}
                    >
                      <span
                        className={`w-3.5 h-3.5 rounded border-2 flex-shrink-0 transition ${
                          selected ? 'bg-zinc-800 border-zinc-800' : 'border-zinc-300'
                        }`}
                      />
                      <HiOutlineClipboardDocumentList className="w-4 h-4 text-zinc-400 flex-shrink-0" />
                      <div className="flex-1 min-w-0">
                        <p className={`truncate ${selected ? 'font-medium' : ''}`}>{t.name}</p>
                        {t.dueDate && (
                          <p className="text-[10px] text-zinc-400 mt-0.5">
                            Deadline: {new Date(t.dueDate).toLocaleDateString('nl-BE')}
                          </p>
                        )}
                      </div>
                    </button>
                  );
                })}
              </div>
            )}
            {error && <p className="text-xs text-red-500">{error}</p>}
          </div>
        )}

        {/* Footer */}
        <div className="px-6 pb-6 pt-4 border-t border-zinc-100 flex items-center justify-between gap-3">
          {stap === 1 ? (
            <Button variant="ghost" label="Annuleren" onClick={onClose} />
          ) : (
            <Button
              variant="outline"
              label="Terug"
              onClick={() => setStap((s) => (s - 1) as Stap)}
              disabled={bezig}
            />
          )}

          <div className="flex gap-2">
            {stap === 1 && (
              <Button
                variant="primary"
                label="Volgende"
                disabled={!kanNaarStap2}
                onClick={() => setStap(2)}
              />
            )}
            {stap === 2 && (
              <Button
                variant="primary"
                label="Volgende"
                disabled={!kanNaarStap3}
                onClick={() => setStap(3)}
              />
            )}
            {stap === 3 && (
              <>
                <Button
                  variant="outline"
                  label="Overslaan"
                  onClick={() => handleAanmaken(false)}
                  disabled={bezig}
                />
                <Button
                  variant="primary"
                  label={
                    bezig
                      ? 'Bezig...'
                      : geselecteerdeTaken.size > 0
                        ? `Aanmaken (${geselecteerdeTaken.size})`
                        : 'Aanmaken'
                  }
                  onClick={() => handleAanmaken(true)}
                  disabled={bezig}
                  loading={bezig}
                />
              </>
            )}
          </div>
        </div>
      </div>
    </Modal>
  );
}
