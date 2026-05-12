'use client';

import { useSession } from 'next-auth/react';
import { useEffect, useMemo, useState } from 'react';
import { useSearchParams } from 'next/navigation';
import { AppContainer } from '@/components/design-system/AppContainer';
import MonthView from '../../../../components/app/planner/MonthView';
import WeekView from '../../../../components/app/planner/WeekView';
import DayView from '../../../../components/app/planner/DayView';
import DetailPanel from '../../../../components/app/planner/DetailPanel';
import type {
  Afwezigheid,
  PlannerTaak,
  View,
} from '../../../../components/app/planner/types';
import { getPeriodBounds } from '../../../../components/app/planner/utils';
import { PageContainer } from '@/components/design-system/PageContainer';
import BreadcrumbInit from '@/components/overig/structuur/breadcrumb/BreadCrumbInit';
import {
  PlannerToolbar,
  type Tab,
} from '../../../../components/app/planner/PlannerToolbar';
import { type FilterState } from '../../../../components/app/planner/PlannerFilter';
import { usePlanningFilters } from '@/hooks/usePlanningFilters';

const BASE = process.env.NEXT_PUBLIC_API_URL ?? 'http://localhost:8080/api';

const LEEG_FILTER: FilterState = {
  locatieId: null,
  teamId: null,
  werknemerId: null,
};

export default function PlannerPage() {
  const { data: session } = useSession();
  const token = session?.accessToken;
  const user = session?.user;
  const isManager = ['Manager', 'Admin'].includes(
    ((session?.user as Record<string, unknown>)?.rol as string) ?? '',
  );

  const searchParams = useSearchParams();
  const [view, setView] = useState<View>(() => {
    const v = searchParams.get('view');
    return v === 'week' || v === 'dag' || v === 'maand' ? v : 'maand';
  });
  const [huidigeDatum, setHuidigeDatum] = useState<Date>(() => {
    const d = searchParams.get('datum');
    if (d) {
      const p = new Date(d + 'T00:00:00');
      if (!isNaN(p.getTime())) return p;
    }
    return new Date();
  });
  const [afwezigheden, setAfwezigheden] = useState<Afwezigheid[]>([]);
  const [taken, setTaken] = useState<PlannerTaak[]>([]);
  const [tab, setTab] = useState<Tab>('you');
  const [geselecteerdeDag, setGeselecteerdeDag] = useState<Date | null>(
    new Date(),
  );
  const [filter, setFilter] = useState<FilterState>(LEEG_FILTER);

  const authHeader = useMemo(
    () => ({ Authorization: `Bearer ${token}` }),
    [token],
  );

  const {
    sites,
    teams,
    teamWerknemers,
    alleWerknemers,
    laadWerknemersVanTeam,
    laadAlleWerknemers,
    resetTeamWerknemers,
  } = usePlanningFilters(authHeader);

  useEffect(() => {
    if (filter.teamId) {
      laadWerknemersVanTeam(filter.teamId);
    } else {
      resetTeamWerknemers();
    }
  }, [filter.teamId, laadWerknemersVanTeam, resetTeamWerknemers]);

  useEffect(() => {
    if (tab === 'team' && teams.length > 0) {
      laadAlleWerknemers(teams);
    }
  }, [tab, teams, laadAlleWerknemers]);

  function handleFilterChange(update: Partial<FilterState>) {
    setFilter((prev) => ({ ...prev, ...update }));
  }

  const filteredAfwezigheden = useMemo(() => {
    if (tab !== 'team') return afwezigheden;
    if (filter.werknemerId !== null) {
      return afwezigheden.filter((a) => a.werknemerId === filter.werknemerId);
    }
    if (filter.teamId !== null && teamWerknemers.length > 0) {
      const ids = new Set(teamWerknemers.map((w) => w.id));
      return afwezigheden.filter((a) => ids.has(a.werknemerId));
    }
    return afwezigheden;
  }, [afwezigheden, tab, filter.werknemerId, filter.teamId, teamWerknemers]);

  useEffect(() => {
    if (!user?.id) return;
    const { van, tot } = getPeriodBounds(view, huidigeDatum);
    fetch(`${BASE}/planning/team/${user.id}?van=${van}&tot=${tot}`, {
      headers: authHeader,
    })
      .then((res) => res.json())
      .then(setAfwezigheden)
      .catch(console.error);
  }, [user, authHeader, huidigeDatum, view]);

  useEffect(() => {
    if (!user?.id) return;
    fetch(`/api/taken/werknemer/${user.id}`)
      .then((res) => {
        if (!res.ok) throw new Error('Failed');
        return res.json();
      })
      .then(setTaken)
      .catch(console.error);
  }, [user]);

  function navigeer(richting: 1 | -1) {
    const d = new Date(huidigeDatum);
    if (view === 'maand') d.setMonth(d.getMonth() + richting);
    if (view === 'week') d.setDate(d.getDate() + richting * 7);
    if (view === 'dag') d.setDate(d.getDate() + richting);
    setHuidigeDatum(d);
  }

  function handleVandaag() {
    setHuidigeDatum(new Date());
    setGeselecteerdeDag(new Date());
  }

  return (
    <PageContainer className="h-full">
      <AppContainer>
        <BreadcrumbInit pages={['planner']} />
        <div className="w-full h-full flex flex-col gap-4 relative">
          <PlannerToolbar
            view={view}
            tab={tab}
            huidigeDatum={huidigeDatum}
            filter={filter}
            sites={sites}
            teams={teams}
            teamWerknemers={teamWerknemers}
            onViewChange={setView}
            onTabChange={(t) => {
              setTab(t);
              if (t !== 'team') setFilter(LEEG_FILTER);
            }}
            onNavigate={navigeer}
            onVandaag={handleVandaag}
            onFilterChange={handleFilterChange}
          />

          <div className="flex flex-col lg:flex-row gap-4 w-full flex-1 min-h-0">
            <div className="flex-1 min-w-0 rounded-xl h-full min-h-64 overflow-y-auto scroll-hidden">
              {view === 'maand' && (
                <MonthView
                  huidigeDatum={huidigeDatum}
                  afwezigheden={filteredAfwezigheden}
                  taken={taken}
                  geselecteerdeDag={geselecteerdeDag}
                  onSelectDag={setGeselecteerdeDag}
                />
              )}
              {view === 'week' && (
                <WeekView
                  huidigeDatum={huidigeDatum}
                  afwezigheden={filteredAfwezigheden}
                  taken={taken}
                  geselecteerdeDag={geselecteerdeDag}
                  onSelectDag={setGeselecteerdeDag}
                />
              )}
              {view === 'dag' && (
                <DayView
                  huidigeDatum={huidigeDatum}
                  afwezigheden={filteredAfwezigheden}
                  taken={taken}
                  filter={filter}
                  teams={teams}
                  teamWerknemers={teamWerknemers}
                  alleWerknemers={alleWerknemers}
                  isManager={isManager}
                  tab={tab}
                />
              )}
            </div>

            {view !== 'dag' && geselecteerdeDag && (
              <DetailPanel
                geselecteerdeDag={geselecteerdeDag}
                afwezigheden={filteredAfwezigheden}
                taken={taken}
              />
            )}
          </div>
        </div>
      </AppContainer>
    </PageContainer>
  );
}
