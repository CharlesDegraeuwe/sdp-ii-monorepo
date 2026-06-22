 
'use client';

import { useCallback, useMemo, useRef, useState } from 'react';
import { useSession } from 'next-auth/react';
import { useQueryClient } from '@tanstack/react-query';
import { useDagShiften } from '@/hooks/useDagShiften';
import {
  MdCheckCircle,
  MdDelete,
  MdClose,
  MdRadioButtonUnchecked,
} from 'react-icons/md';
import type { Afwezigheid, PlannerTaak, Shift } from './types';
import type { FilterState } from './PlannerFilter';
import type {
  TeamOptie,
  WerknemerOptie,
  WerknemerMetTeam,
} from '@/hooks/usePlanningFilters';
import {
  afwezighedenOpDag,
  isVrij,
  mapTaakVanBackend,
  takenOpDag,
  taakBadgeKleur,
} from './utils';
import {
  VIS_START,
  UUR_BREEDTE,
  NAAM_B,
  TIMELINE_W,
  HOUR_LABELS,
  type Rij,
  type ModalForm,
  toTimeInput,
  toBackendTime,
} from './dayview/helpers';
import { ShiftModal } from './dayview/ShiftModal';
import { PersonalDayView } from './dayview/PersonalDayView';
import { WerknemerRij } from './dayview/WerknemerRij';

interface DayViewProps {
  huidigeDatum: Date;
  afwezigheden: Afwezigheid[];
  taken: PlannerTaak[];
  filter: FilterState;
  teams: TeamOptie[];
  teamWerknemers: WerknemerOptie[];
  alleWerknemers: WerknemerMetTeam[];
  isManager: boolean;
  tab: 'team' | 'you';
  eigenShiften?: Shift[];
  teamTaken?: Record<number, PlannerTaak[]>;
  onAfgewerkt?: (id: number) => void;
}

export default function DayView({
  huidigeDatum,
  afwezigheden,
  taken,
  filter,
  teams,
  teamWerknemers,
  alleWerknemers,
  isManager,
  tab,
  eigenShiften = [],
  teamTaken = {},
  onAfgewerkt,
}: DayViewProps) {
  const { data: session } = useSession();
  const eigenId = Number((session?.user as Record<string, unknown>)?.id ?? 0);
  const queryClient = useQueryClient();
  const [modal, setModal] = useState<ModalForm | null>(null);
  const [geselecteerdeRij, setGeselecteerdeRij] = useState<Rij | null>(null);
  const [werknemerTaken, setWerknemerTaken] = useState<PlannerTaak[]>([]);
  const [laadtTaken, setLaadtTaken] = useState(false);

  // Reset geselecteerde rij wanneer datum/filter/tab wijzigt
  const resetKey = `${huidigeDatum.toISOString()}-${JSON.stringify(filter)}-${tab}`;
  const prevResetKey = useRef(resetKey);
  if (prevResetKey.current !== resetKey) {
    prevResetKey.current = resetKey;
    setGeselecteerdeRij(null);
    setWerknemerTaken([]);
  }

  const werknemersToLoad = useMemo(() => {
    if (tab === 'you') return eigenId ? [eigenId] : [];
    if (filter.werknemerId) return [filter.werknemerId];
    if (filter.teamId) return teamWerknemers.map((w) => w.id);
    if (filter.locatieId) {
      const siteTeamIds = new Set(
        teams.filter((t) => t.siteId === filter.locatieId).map((t) => t.id),
      );
      return alleWerknemers
        .filter((w) => siteTeamIds.has(w.teamId))
        .map((w) => w.id);
    }
    if (tab === 'team') return alleWerknemers.map((w) => w.id);
    return [];
  }, [eigenId, filter, tab, teamWerknemers, alleWerknemers, teams]);

  const datum = huidigeDatum.toISOString().split('T')[0];
  const { data: shifts } = useDagShiften(werknemersToLoad, datum);

  // Laad taken voor geselecteerde werknemer (team view)
  const laadTakenVoorWerknemer = useCallback(async (rij: Rij) => {
    setLaadtTaken(true);
    try {
      const r = await fetch(`/api/taken/werknemer/${rij.werknemerId}`);
      const data: Record<string, unknown>[] = r.ok ? await r.json() : [];
      setWerknemerTaken(data.map(mapTaakVanBackend));
    } catch {
      setWerknemerTaken([]);
    } finally {
      setLaadtTaken(false);
    }
  }, []);

  async function handleWerknemerTaakAfgewerkt(taakId: number) {
    const res = await fetch(`/api/taken/${taakId}/afgewerkt`, {
      method: 'PUT',
    });
    if (res.ok) {
      setWerknemerTaken((prev) =>
        prev.map((t) => (t.id === taakId ? { ...t, afgewerkt: true } : t)),
      );
    }
  }

  async function handleWerknemerTaakVerwijder(taakId: number) {
    const res = await fetch(`/api/taken/${taakId}`, { method: 'DELETE' });
    if (res.ok) {
      setWerknemerTaken((prev) => prev.filter((t) => t.id !== taakId));
    }
  }

  if (tab === 'you') {
    const dagAfwezigheid = afwezighedenOpDag(afwezigheden, huidigeDatum).filter(
      (a) => !eigenId || a.werknemerId === eigenId,
    );
    const eigenShift =
      eigenShiften.find((s) => {
        const ds = huidigeDatum.toISOString().split('T')[0];
        return s.startDatum <= ds && s.eindDatum >= ds;
      }) ?? shifts.find((s) => s.werknemerId === eigenId);
    return (
      <PersonalDayView
        datum={huidigeDatum}
        afwezigheid={dagAfwezigheid}
        taken={taken}
        eigenShift={eigenShift}
        onAfgewerkt={onAfgewerkt}
      />
    );
  }

  let rijen: Rij[];
  if (filter.werknemerId !== null) {
    const w =
      teamWerknemers.find((w) => w.id === filter.werknemerId) ??
      alleWerknemers.find((w) => w.id === filter.werknemerId);
    const teamNaam =
      (w as WerknemerMetTeam | undefined)?.teamNaam ??
      teams.find((t) => t.id === filter.teamId)?.naam ??
      '';
    rijen = w
      ? [{ werknemerId: w.id, label: `${w.voornaam} ${w.naam}`, teamNaam }]
      : [];
  } else if (filter.teamId !== null) {
    const teamNaam = teams.find((t) => t.id === filter.teamId)?.naam ?? '';
    rijen = teamWerknemers.map((w) => ({
      werknemerId: w.id,
      label: `${w.voornaam} ${w.naam}`,
      teamNaam,
    }));
  } else if (filter.locatieId !== null) {
    const siteTeamIds = new Set(
      teams.filter((t) => t.siteId === filter.locatieId).map((t) => t.id),
    );
    rijen = alleWerknemers
      .filter((w) => siteTeamIds.has(w.teamId))
      .map((w) => ({
        werknemerId: w.id,
        label: `${w.voornaam} ${w.naam}`,
        teamNaam: w.teamNaam,
      }));
  } else {
    rijen = alleWerknemers.map((w) => ({
      werknemerId: w.id,
      label: `${w.voornaam} ${w.naam}`,
      teamNaam: w.teamNaam,
    }));
  }

  const dagAfwezigheden = afwezighedenOpDag(afwezigheden, huidigeDatum);

  const now = new Date();
  const isVandaag = huidigeDatum.toDateString() === now.toDateString();
  const huidigUurX = isVandaag
    ? (now.getHours() + now.getMinutes() / 60 - VIS_START) * UUR_BREEDTE
    : null;

  function openCreate(rij: Rij) {
    const datum = huidigeDatum.toISOString().split('T')[0];
    setModal({
      shiftId: null,
      werknemerId: rij.werknemerId,
      werknemerNaam: rij.label,
      startDatum: datum,
      eindDatum: datum,
      startTijd: '09:00',
      eindTijd: '17:00',
      pauzeStart: '12:00',
      pauzeEind: '12:30',
    });
  }

  function openEdit(shift: Shift, rij: Rij) {
    setModal({
      shiftId: shift.id,
      werknemerId: shift.werknemerId,
      werknemerNaam: rij.label,
      startDatum: shift.startDatum,
      eindDatum: shift.eindDatum,
      startTijd: toTimeInput(shift.startTijd),
      eindTijd: toTimeInput(shift.eindTijd),
      pauzeStart: toTimeInput(shift.pauzeStart),
      pauzeEind: toTimeInput(shift.pauzeEind),
    });
  }

  async function handleSave(f: ModalForm) {
    const url = f.shiftId ? `/api/shifts/${f.shiftId}` : `/api/shifts`;
    const method = f.shiftId ? 'PUT' : 'POST';
    const body: Record<string, unknown> = {
      startDatum: f.startDatum,
      eindDatum: f.eindDatum,
      startTijd: toBackendTime(f.startTijd),
      eindTijd: toBackendTime(f.eindTijd),
      pauzeStart: f.pauzeStart ? toBackendTime(f.pauzeStart) : null,
      pauzeEind: f.pauzeEind ? toBackendTime(f.pauzeEind) : null,
    };
    if (!f.shiftId) body.werknemerId = f.werknemerId;

    const res = await fetch(url, {
      method,
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(body),
    });
    if (res.ok) {
      void queryClient.invalidateQueries({ queryKey: ['dag-shiften'] });
    }
  }

  const dagIsVrij = isVrij(huidigeDatum);
  const dagTakenVoorGeselecteerde = geselecteerdeRij
    ? takenOpDag(werknemerTaken, huidigeDatum)
    : [];

  return (
    <div className="flex flex-col gap-3 h-full">
      {/* Timeline */}
      <div className="w-full overflow-x-auto scroll-hidden">
        <div style={{ minWidth: NAAM_B + TIMELINE_W }}>
          {/* Hour header */}
          <div className="flex border-b border-zinc-200 bg-white sticky top-0 z-10">
            <div
              style={{ width: NAAM_B, minWidth: NAAM_B }}
              className="shrink-0"
            />
            <div className="flex">
              {HOUR_LABELS.map((u) => (
                <div
                  key={u}
                  style={{ width: UUR_BREEDTE, minWidth: UUR_BREEDTE }}
                  className="text-center text-[9px] font-bold text-zinc-400 py-1.5 border-l border-zinc-100 first:border-l-0"
                >
                  {String(u).padStart(2, '0')}:00
                </div>
              ))}
            </div>
          </div>

          {rijen.map((rij) => {
            const rijShift = shifts.find(
              (s) => s.werknemerId === rij.werknemerId,
            );
            const rijTaken = (teamTaken[rij.werknemerId] ?? []).filter(
              (t) => !t.afgewerkt,
            );
            return (
              <WerknemerRij
                key={`w${rij.werknemerId}`}
                rij={rij}
                shift={rijShift}
                taken={rijShift ? rijTaken : []}
                afwezig={dagAfwezigheden.filter(
                  (a) => a.werknemerId === rij.werknemerId,
                )}
                isManager={isManager}
                dagIsVrij={dagIsVrij}
                huidigUurX={huidigUurX}
                isGeselecteerd={
                  geselecteerdeRij?.werknemerId === rij.werknemerId
                }
                onSelecteer={() => {
                  const next =
                    geselecteerdeRij?.werknemerId === rij.werknemerId
                      ? null
                      : rij;
                  setGeselecteerdeRij(next);
                  if (next) laadTakenVoorWerknemer(next);
                  else setWerknemerTaken([]);
                }}
                onEdit={() => {
                  if (rijShift) openEdit(rijShift, rij);
                }}
                onCreate={() => openCreate(rij)}
              />
            );
          })}

          {rijen.length === 0 && (
            <div className="flex items-center justify-center py-12 text-xs text-zinc-400">
              Geen teamleden gevonden voor dit filter.
            </div>
          )}
        </div>
      </div>

      {/* Task panel voor geselecteerde werknemer */}
      {geselecteerdeRij && (
        <div className="rounded-xl border border-zinc-200 bg-white p-4">
          <div className="flex items-center justify-between mb-3">
            <span className="text-xs font-bold text-zinc-800">
              Taken van {geselecteerdeRij.label} vandaag
            </span>
            <button
              onClick={() => setGeselecteerdeRij(null)}
              className="text-zinc-400 hover:text-zinc-600 transition-colors"
            >
              <MdClose size={14} />
            </button>
          </div>

          {laadtTaken ? (
            <p className="text-xs text-zinc-400 italic">Laden...</p>
          ) : dagTakenVoorGeselecteerde.length === 0 &&
            werknemerTaken.length === 0 ? (
            <p className="text-xs text-zinc-400 italic">Geen taken gevonden.</p>
          ) : werknemerTaken.length === 0 ? (
            <p className="text-xs text-zinc-400 italic">
              Geen taken voor vandaag.
            </p>
          ) : (
            <div className="flex flex-col gap-2">
              {werknemerTaken.map((t) => (
                <div
                  key={t.id}
                  className={`flex items-center gap-2 px-3 py-2 rounded-xl border ${
                    t.afgewerkt
                      ? 'bg-zinc-50 border-zinc-100 opacity-60'
                      : t.belangrijk
                        ? 'bg-rose-50 border-rose-100'
                        : 'bg-blue-50 border-blue-100'
                  }`}
                >
                  {isManager && (
                    <button
                      onClick={() =>
                        !t.afgewerkt && handleWerknemerTaakAfgewerkt(t.id)
                      }
                      disabled={t.afgewerkt}
                      className={`shrink-0 transition-colors ${
                        t.afgewerkt
                          ? 'text-emerald-500 cursor-default'
                          : 'text-zinc-300 hover:text-emerald-500 cursor-pointer'
                      }`}
                      title={
                        t.afgewerkt ? 'Afgewerkt' : 'Markeer als afgewerkt'
                      }
                    >
                      {t.afgewerkt ? (
                        <MdCheckCircle size={16} />
                      ) : (
                        <MdRadioButtonUnchecked size={16} />
                      )}
                    </button>
                  )}
                  {!isManager && t.afgewerkt && (
                    <MdCheckCircle
                      size={16}
                      className="text-emerald-500 shrink-0"
                    />
                  )}
                  <span
                    className={`text-[10px] font-bold px-1.5 py-0.5 rounded-full shrink-0 ${taakBadgeKleur(t)}`}
                  >
                    {t.afgewerkt
                      ? 'Afgewerkt'
                      : t.belangrijk
                        ? 'Belangrijk'
                        : 'Taak'}
                  </span>
                  <span
                    className={`text-xs font-semibold flex-1 truncate ${t.afgewerkt ? 'line-through text-zinc-400' : 'text-zinc-800'}`}
                  >
                    {t.naam}
                  </span>
                  {t.deadline && (
                    <span className="text-[10px] text-zinc-400 shrink-0">
                      {t.deadline}
                    </span>
                  )}
                  {isManager && (
                    <button
                      onClick={() => handleWerknemerTaakVerwijder(t.id)}
                      className="text-zinc-300 hover:text-red-500 transition-colors shrink-0"
                      title="Verwijder taak"
                    >
                      <MdDelete size={14} />
                    </button>
                  )}
                </div>
              ))}
            </div>
          )}
        </div>
      )}

      {modal && isManager && (
        <ShiftModal
          form={modal}
          onSave={handleSave}
          onClose={() => setModal(null)}
        />
      )}
    </div>
  );
}
