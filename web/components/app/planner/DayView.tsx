'use client';

import { useEffect, useMemo, useState } from 'react';
import { useSession } from 'next-auth/react';
import type { Afwezigheid, PlannerTaak, Shift } from './types';
import type { FilterState } from './PlannerFilter';
import type {
  TeamOptie,
  WerknemerOptie,
  WerknemerMetTeam,
} from '@/hooks/usePlanningFilters';
import { afwezighedenOpDag, isVrij } from './utils';
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
}: DayViewProps) {
  const { data: session } = useSession();
  const eigenId = Number((session?.user as Record<string, unknown>)?.id ?? 0);
  const [shifts, setShifts] = useState<Shift[]>([]);
  const [modal, setModal] = useState<ModalForm | null>(null);

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

  useEffect(() => {
    const datum = huidigeDatum.toISOString().split('T')[0];
    const fetches =
      werknemersToLoad.length === 0
        ? Promise.resolve<Shift[]>([])
        : Promise.all(
            werknemersToLoad.map((wid) =>
              fetch(`/api/shifts/werknemer/${wid}?datum=${datum}`).then((r) =>
                r.ok ? (r.json() as Promise<Shift[]>) : ([] as Shift[]),
              ),
            ),
          ).then((results) => results.flat());

    fetches.then(setShifts).catch(() => setShifts([]));
  }, [werknemersToLoad, huidigeDatum]);

  if (tab === 'you') {
    const dagAfwezigheid = afwezighedenOpDag(afwezigheden, huidigeDatum).filter(
      (a) => !eigenId || a.werknemerId === eigenId,
    );
    const eigenShift = shifts.find((s) => s.werknemerId === eigenId);
    return (
      <PersonalDayView
        datum={huidigeDatum}
        afwezigheid={dagAfwezigheid}
        taken={taken}
        eigenShift={eigenShift}
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
      const saved: Shift = await res.json();
      setShifts((prev) => [...prev.filter((s) => s.id !== saved.id), saved]);
    }
  }

  const dagIsVrij = isVrij(huidigeDatum);

  return (
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

        {rijen.map((rij) => (
          <WerknemerRij
            key={`w${rij.werknemerId}`}
            rij={rij}
            shift={shifts.find((s) => s.werknemerId === rij.werknemerId)}
            afwezig={dagAfwezigheden.filter(
              (a) => a.werknemerId === rij.werknemerId,
            )}
            isManager={isManager}
            dagIsVrij={dagIsVrij}
            huidigUurX={huidigUurX}
            onEdit={() => {
              const s = shifts.find((s) => s.werknemerId === rij.werknemerId);
              if (s) openEdit(s, rij);
            }}
            onCreate={() => openCreate(rij)}
          />
        ))}

        {rijen.length === 0 && (
          <div className="flex items-center justify-center py-12 text-xs text-zinc-400">
            Geen teamleden gevonden voor dit filter.
          </div>
        )}
      </div>

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
