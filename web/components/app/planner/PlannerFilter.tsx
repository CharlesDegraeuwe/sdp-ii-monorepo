'use client';

import * as React from 'react';
import { FilterChip } from './filter/FilterChip';
import { FilterMenu, type IFilterCategory } from './filter/FilterMenu';
import type { IFilterOption } from './filter/types';
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

export function PlannerFilter({
  filter,
  onChange,
  sites,
  teams,
  werknemers,
}: PlannerFilterProps) {
  const teamsVoorLocatie = filter.locatieId
    ? teams.filter((t) => t.siteId === filter.locatieId)
    : teams;

  const siteOptions: IFilterOption[] = sites.map((s) => ({
    value: String(s.id),
    label: s.naam,
  }));

  const teamOptions: IFilterOption[] = teamsVoorLocatie.map((t) => ({
    value: String(t.id),
    label: t.naam,
  }));

  const werknemerOptions: IFilterOption[] = werknemers.map((w) => ({
    value: String(w.id),
    label: `${w.voornaam} ${w.naam}`,
  }));

  const categories: IFilterCategory[] = [
    {
      key: 'locatie',
      label: 'Locatie',
      options: siteOptions,
      selectedValues:
        filter.locatieId != null ? [String(filter.locatieId)] : [],
    },
    {
      key: 'team',
      label: 'Team',
      options: teamOptions,
      selectedValues: filter.teamId != null ? [String(filter.teamId)] : [],
    },
    {
      key: 'werknemer',
      label: 'Werknemer',
      options: werknemerOptions,
      selectedValues:
        filter.werknemerId != null ? [String(filter.werknemerId)] : [],
    },
  ];

  function handleToggle(categoryKey: string, value: string) {
    if (categoryKey === 'locatie') {
      const id = Number(value);
      const alSelected = filter.locatieId === id;
      onChange({
        locatieId: alSelected ? null : id,
        teamId: null,
        werknemerId: null,
      });
    } else if (categoryKey === 'team') {
      const id = Number(value);
      const alSelected = filter.teamId === id;
      onChange({ teamId: alSelected ? null : id, werknemerId: null });
    } else if (categoryKey === 'werknemer') {
      const id = Number(value);
      const alSelected = filter.werknemerId === id;
      onChange({ werknemerId: alSelected ? null : id });
    }
  }

  function handleDelete(categoryKey: string) {
    if (categoryKey === 'locatie')
      onChange({ locatieId: null, teamId: null, werknemerId: null });
    else if (categoryKey === 'team')
      onChange({ teamId: null, werknemerId: null });
    else if (categoryKey === 'werknemer') onChange({ werknemerId: null });
  }

  function handleClearAll() {
    onChange({ locatieId: null, teamId: null, werknemerId: null });
  }

  const activeFilters = categories.filter((c) => c.selectedValues.length > 0);
  const hasActive = activeFilters.length > 0;

  return (
    <div className="flex flex-wrap items-center gap-2 min-h-9">
      <FilterMenu
        categories={categories}
        onToggleFilter={handleToggle}
        showSearch={sites.length + teams.length > 8}
      />

      {activeFilters.map((f) => (
        <FilterChip
          key={f.key}
          label={f.label}
          options={f.options}
          selectedValues={f.selectedValues}
          onSelectionChange={(values) => {
            const current = new Set(f.selectedValues);
            const next = new Set(values);
            for (const v of current) {
              if (!next.has(v)) handleToggle(f.key, v);
            }
            for (const v of next) {
              if (!current.has(v)) handleToggle(f.key, v);
            }
          }}
          onDelete={() => handleDelete(f.key)}
        />
      ))}

      {hasActive && (
        <button
          type="button"
          className="h-8 px-3 rounded-full inline-flex items-center text-sm text-zinc-500 hover:bg-zinc-100 transition-colors cursor-pointer"
          onClick={handleClearAll}
        >
          Wis filters
        </button>
      )}
    </div>
  );
}
