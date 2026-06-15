'use client';

import { MdFilterList, MdClose } from 'react-icons/md';
import type {
  SiteOptie,
  TeamOptie,
  WerknemerOptie,
} from '@/hooks/usePlanningFilters';

export interface FilterState {
  locatieId: number | null;
  teamId: number | null;
  werknemerId: number | null;
}

interface PlannerFilterProps {
  filter: FilterState;
  onChange: (update: Partial<FilterState>) => void;
  sites: SiteOptie[];
  teams: TeamOptie[];
  werknemers: WerknemerOptie[];
}

const SELECT_CLASS =
  'text-xs font-semibold text-zinc-700 border border-gray-300/70 bg-white/80 backdrop-blur-sm rounded-xl px-3 py-1.5 focus:outline-none focus:ring-1 focus:ring-zinc-400 cursor-pointer hover:border-zinc-400 transition-colors duration-150 disabled:opacity-40 disabled:cursor-not-allowed';

export function PlannerFilter({
  filter,
  onChange,
  sites,
  teams,
  werknemers,
}: PlannerFilterProps) {
  const isFiltered =
    filter.locatieId !== null ||
    filter.teamId !== null ||
    filter.werknemerId !== null;

  const teamsVoorLocatie = filter.locatieId
    ? teams.filter((t) => t.siteId === filter.locatieId)
    : teams;

  return (
    <div className="flex flex-wrap items-end gap-3">
      <span className="flex items-center gap-1 text-[11px] font-bold text-zinc-400 pb-2 shrink-0">
        <MdFilterList size={14} />
        Filter
      </span>

      <div className="flex flex-col gap-0.5">
        <label className="text-[9px] font-bold text-zinc-400 uppercase tracking-wide px-1">
          Locatie
        </label>
        <select
          value={filter.locatieId ?? ''}
          onChange={(e) => {
            const id = e.target.value ? Number(e.target.value) : null;
            onChange({ locatieId: id, teamId: null, werknemerId: null });
          }}
          className={SELECT_CLASS}
        >
          <option value="">Alle locaties</option>
          {sites.map((s) => (
            <option key={s.id} value={s.id}>
              {s.naam}
            </option>
          ))}
        </select>
      </div>

      <div className="flex flex-col gap-0.5">
        <label className="text-[9px] font-bold text-zinc-400 uppercase tracking-wide px-1">
          Team
        </label>
        <select
          value={filter.teamId ?? ''}
          onChange={(e) => {
            const id = e.target.value ? Number(e.target.value) : null;
            onChange({ teamId: id, werknemerId: null });
          }}
          className={SELECT_CLASS}
        >
          <option value="">Alle teams</option>
          {teamsVoorLocatie.map((t) => (
            <option key={t.id} value={t.id}>
              {t.naam}
            </option>
          ))}
        </select>
      </div>

      <div className="flex flex-col gap-0.5">
        <label className="text-[9px] font-bold text-zinc-400 uppercase tracking-wide px-1">
          Werknemer
        </label>
        <select
          value={filter.werknemerId ?? ''}
          onChange={(e) => {
            const id = e.target.value ? Number(e.target.value) : null;
            onChange({ werknemerId: id });
          }}
          disabled={!filter.teamId}
          className={SELECT_CLASS}
        >
          <option value="">
            {filter.teamId ? 'Alle werknemers' : 'Kies eerst een team'}
          </option>
          {werknemers.map((w) => (
            <option key={w.id} value={w.id}>
              {w.voornaam} {w.naam}
            </option>
          ))}
        </select>
      </div>

      {isFiltered && (
        <button
          onClick={() =>
            onChange({ locatieId: null, teamId: null, werknemerId: null })
          }
          className="flex items-center gap-1 mb-0.5 text-[11px] font-bold text-zinc-500 hover:text-zinc-800 px-2.5 py-1.5 rounded-xl border border-gray-300/60 hover:border-zinc-400 bg-white/60 transition-all duration-150"
        >
          <MdClose size={11} />
          Wis filters
        </button>
      )}
    </div>
  );
}
