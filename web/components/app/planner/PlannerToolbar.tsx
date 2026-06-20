'use client';

import { FaChevronLeft, FaChevronRight } from 'react-icons/fa';
import { Button } from '@/components/design-system/Button';
import { TabSwitcher } from '@/components/design-system/TabSwitcher/TabSwitcher';
import { PlannerFilter, type FilterState } from './PlannerFilter';
import { periodeLabel } from './utils';
import type { View } from './types';
import type {
  SiteOptie,
  TeamOptie,
  WerknemerOptie,
} from '@/hooks/usePlanningFilters';

export type Tab = 'team' | 'you';

const views: { key: View; label: string }[] = [
  { key: 'maand', label: 'maand' },
  { key: 'week', label: 'week' },
  { key: 'dag', label: 'dag' },
];

const tabs: { key: Tab; label: string }[] = [
  { key: 'you', label: 'Jouw planning' },
  { key: 'team', label: 'Teamplanning' },
];

interface PlannerToolbarProps {
  view: View;
  tab: Tab;
  huidigeDatum: Date;
  filter: FilterState;
  sites: SiteOptie[];
  teams: TeamOptie[];
  teamWerknemers: WerknemerOptie[];
  kanTeamZien: boolean;
  kanShiftAanmaken: boolean;
  onViewChange: (v: View) => void;
  onTabChange: (t: Tab) => void;
  onNavigate: (d: 1 | -1) => void;
  onVandaag: () => void;
  onFilterChange: (update: Partial<FilterState>) => void;
  onShiftAanmaken: () => void;
}

export function PlannerToolbar({
  view,
  tab,
  huidigeDatum,
  filter,
  sites,
  teams,
  teamWerknemers,
  kanTeamZien,
  kanShiftAanmaken,
  onViewChange,
  onTabChange,
  onNavigate,
  onVandaag,
  onFilterChange,
  onShiftAanmaken,
}: PlannerToolbarProps) {
  const zichtbareTabs = kanTeamZien
    ? tabs
    : tabs.filter((t) => t.key !== 'team');
  return (
    <div className="flex flex-col gap-2.5 w-full">
      {/* Row 1: navigation + view switcher + today button */}
      <div className="flex flex-col sm:flex-row items-start sm:items-center gap-3 w-full">
        <div className="flex flex-row w-full sm:w-auto gap-3 items-center">
          <Button
            variant={'outline'}
            icon={<FaChevronLeft />}
            onClick={() => onNavigate(-1)}
          />
          <span className="flex-1 sm:flex-none text-sm font-bold text-zinc-800 min-w-28 sm:min-w-52 text-center capitalize">
            {periodeLabel(view, huidigeDatum)}
          </span>
          <Button
            variant={'outline'}
            icon={<FaChevronRight />}
            onClick={() => onNavigate(1)}
          />
        </div>
        <div className="flex flex-row w-full sm:w-auto justify-between sm:justify-start gap-2 sm:gap-3 items-center">
          <TabSwitcher
            tabs={views}
            value={view}
            onChange={(key) => onViewChange(key as View)}
          />
          <Button onClick={onVandaag} variant="primary" label="Vandaag" />
          {kanShiftAanmaken && (
            <Button
              onClick={onShiftAanmaken}
              variant="secondary"
              label="+ Shift"
            />
          )}
          {/* Team switcher inline on desktop, in its own row slot on mobile */}
          {kanTeamZien && (
            <div className={'hidden sm:flex min-w-fit lg:min-w-90 justify-end'}>
              <TabSwitcher
                tabs={zichtbareTabs}
                value={tab}
                onChange={(key) => onTabChange(key as Tab)}
              />
            </div>
          )}
        </div>
        <div className={'flex-1 w-full'} />
        {/* Team switcher visible on mobile only (below view controls) */}
        {kanTeamZien && (
          <div className={'flex sm:hidden w-full'}>
            <TabSwitcher
              tabs={zichtbareTabs}
              value={tab}
              onChange={(key) => onTabChange(key as Tab)}
            />
          </div>
        )}
      </div>

      <div className={'min-w-full flex justify-end'}>
        {tab === 'team' && (
          <PlannerFilter
            filter={filter}
            onChange={onFilterChange}
            sites={sites}
            teams={teams}
            werknemers={teamWerknemers}
          />
        )}
      </div>
    </div>
  );
}
