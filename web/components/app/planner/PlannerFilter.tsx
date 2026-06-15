'use client';

import { MdFilterList, MdClose } from 'react-icons/md';
import type {
  SiteOptie,
  TeamOptie,
  WerknemerOptie,
} from '@/hooks/usePlanningFilters';
import Select from '@/components/design-system/Select/Select';
import Button from '@/components/design-system/Button/Button';

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

  const siteOptions = [
    { value: '', label: 'Alle locaties' },
    ...sites.map((s) => ({ value: String(s.id), label: s.naam })),
  ];

  const teamOptions = [
    { value: '', label: 'Alle teams' },
    ...teamsVoorLocatie.map((t) => ({ value: String(t.id), label: t.naam })),
  ];

  const werknemerOptions = [
    {
      value: '',
      label: filter.teamId ? 'Alle werknemers' : 'Kies eerst een team',
    },
    ...werknemers.map((w) => ({
      value: String(w.id),
      label: `${w.voornaam} ${w.naam}`,
    })),
  ];

  return (
    <div className="flex flex-col sm:flex-row w-full items-start sm:items-end gap-3">
      <span className="flex items-center gap-1 text-[11px] font-bold text-zinc-400 pb-0 sm:pb-2 shrink-0">
        <MdFilterList size={14} />
        Filter
      </span>

      <div className="flex flex-col sm:flex-row gap-3 flex-1 w-full">
        <div className={'flex-1 min-w-28'}>
          <Select
            size={'sm'}
            label="Locaties"
            options={siteOptions}
            value={filter.locatieId != null ? String(filter.locatieId) : ''}
            placeholder="Alle locaties"
            onChange={(val) => {
              const id = val ? Number(val) : null;
              onChange({ locatieId: id, teamId: null, werknemerId: null });
            }}
          />
        </div>
        <div className={'flex-1 min-w-28'}>
          <Select
            size={'sm'}
            label="Team"
            options={teamOptions}
            value={filter.teamId != null ? String(filter.teamId) : ''}
            placeholder="Alle teams"
            onChange={(val) => {
              const id = val ? Number(val) : null;
              onChange({ teamId: id, werknemerId: null });
            }}
          />
        </div>
        <div className={'flex-1 min-w-28'}>
          <Select
            size={'sm'}
            label="Werknemer"
            options={werknemerOptions}
            value={filter.werknemerId != null ? String(filter.werknemerId) : ''}
            placeholder={
              filter.teamId ? 'Alle werknemers' : 'Kies eerst een team'
            }
            disabled={!filter.teamId}
            onChange={(val) => {
              const id = val ? Number(val) : null;
              onChange({ werknemerId: id });
            }}
          />
        </div>
      </div>

      {isFiltered && (
        <div className="shrink-0">
          <Button
            size={'sm'}
            iconLeft={<MdClose size={11} />}
            variant={'outline'}
            label={'Wis filters'}
            onClick={() =>
              onChange({ locatieId: null, teamId: null, werknemerId: null })
            }
          />
        </div>
      )}
    </div>
  );
}
