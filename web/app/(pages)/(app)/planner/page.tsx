'use client';

import { Suspense } from 'react';
import { useSession } from 'next-auth/react';
import { useEffect, useMemo, useState } from 'react';
import { useSearchParams } from 'next/navigation';
import { useQueryClient } from '@tanstack/react-query';
import { AppContainer } from '@/components/design-system/AppContainer';
import MonthView from '../../../../components/app/planner/MonthView';
import WeekView from '../../../../components/app/planner/WeekView';
import DayView from '../../../../components/app/planner/DayView';
import DetailPanel from '../../../../components/app/planner/DetailPanel';
import type {
  PlannerTaak,
  View,
} from '../../../../components/app/planner/types';
import {
  getPeriodBounds,
  getMaandag,
} from '../../../../components/app/planner/utils';
import { PlannerAanvraagModal } from '../../../../components/app/planner/PlannerAanvraagModal';
import { ShiftAanmakenModal } from '../../../../components/app/planner/ShiftAanmakenModal';
import { PageContainer } from '@/components/design-system/PageContainer';
import BreadcrumbInit from '@/components/overig/structuur/breadcrumb/BreadCrumbInit';
import {
  PlannerToolbar,
  type Tab,
} from '../../../../components/app/planner/PlannerToolbar';
import { type FilterState } from '../../../../components/app/planner/PlannerFilter';
import { usePlanningFilters } from '@/hooks/usePlanningFilters';
import { usePlannerAfwezigheid } from '@/hooks/usePlannerAfwezigheid';
import { useEigenPlannerShiften } from '@/hooks/useEigenPlannerShiften';
import { usePlannerEigenTaken } from '@/hooks/usePlannerEigenTaken';
import { useTeamPlannerShiften } from '@/hooks/useTeamPlannerShiften';
import { useTeamPlannerTaken } from '@/hooks/useTeamPlannerTaken';

const LEEG_FILTER: FilterState = {
  locatieId: null,
  teamId: null,
  werknemerId: null,
};

function PlannerPageInner() {
  const { data: session } = useSession();
  const user = session?.user;
  const rol = ((session?.user as Record<string, unknown>)?.rol as string) ?? '';
  const isManager = ['Manager', 'Admin'].includes(rol);
  const isSupervisor = rol === 'Supervisor';
  const kanTeamZien = ['Manager', 'Admin', 'Supervisor'].includes(rol);
  const kanShiftAanmaken = kanTeamZien;
  const eigenId = Number((session?.user as Record<string, unknown>)?.id ?? 0);
  const eigenVoornaam =
    ((session?.user as Record<string, unknown>)?.voornaam as string) ?? '';
  const eigenNaamStr =
    ((session?.user as Record<string, unknown>)?.naam as string) ?? '';

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
  const [tab, setTab] = useState<Tab>('you');
  const [geselecteerdeDag, setGeselecteerdeDag] = useState<Date | null>(
    new Date(),
  );
  const [filter, setFilter] = useState<FilterState>(LEEG_FILTER);
  const [plannerModal, setPlannerModal] = useState<{
    type: 'verlof' | 'afwezigheid';
    datum: Date;
  } | null>(null);
  const [shiftAanmakenOpen, setShiftAanmakenOpen] = useState(false);
  const [geselecteerdeTeamWerknemer, setGeselecteerdeTeamWerknemer] = useState<
    number | null
  >(null);

  const queryClient = useQueryClient();

  const { sites, teams, teamWerknemers, alleWerknemers } = usePlanningFilters(
    eigenId || undefined,
    isSupervisor,
    filter.teamId,
    tab === 'team',
  );

  // Auto-selecteer eerste team bij switchen naar teamtab
  useEffect(() => {
    if (tab === 'team' && teams.length > 0 && filter.teamId === null) {
      handleFilterChange({ teamId: teams[0].id });
    }
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [tab, teams]);

  function handleFilterChange(update: Partial<FilterState>) {
    setFilter((prev) => ({ ...prev, ...update }));
  }

  // Compute period bounds
  const { van: afwezigheidVan, tot: afwezigheidTot } = useMemo(
    () => getPeriodBounds(view, huidigeDatum),
    [view, huidigeDatum],
  );

  const { van: eigenShiftenVan, tot: eigenShiftenTot } = useMemo(() => {
    if (view === 'maand') {
      return {
        van: new Date(huidigeDatum.getFullYear(), huidigeDatum.getMonth(), 1)
          .toISOString()
          .split('T')[0],
        tot: new Date(
          huidigeDatum.getFullYear(),
          huidigeDatum.getMonth() + 1,
          0,
        )
          .toISOString()
          .split('T')[0],
      };
    } else if (view === 'week') {
      const ma = getMaandag(huidigeDatum);
      const zo = new Date(ma);
      zo.setDate(zo.getDate() + 6);
      return {
        van: ma.toISOString().split('T')[0],
        tot: zo.toISOString().split('T')[0],
      };
    } else {
      const dag = huidigeDatum.toISOString().split('T')[0];
      return { van: dag, tot: dag };
    }
  }, [view, huidigeDatum]);

  const teamShiftenVan = eigenShiftenVan;
  const teamShiftenTot = eigenShiftenTot;

  // React Query hooks
  const { data: afwezigheden } = usePlannerAfwezigheid(
    eigenId || undefined,
    afwezigheidVan,
    afwezigheidTot,
  );

  const { data: eigenShiften } = useEigenPlannerShiften(
    eigenId || undefined,
    eigenShiftenVan,
    eigenShiftenTot,
  );

  const { data: taken } = usePlannerEigenTaken(eigenId || undefined);

  const teamWerknemerIds = useMemo(
    () => teamWerknemers.map((w) => w.id),
    [teamWerknemers],
  );

  const { data: teamShiften } = useTeamPlannerShiften(
    teamWerknemerIds,
    teamShiftenVan,
    teamShiftenTot,
    tab === 'team' && view !== 'dag',
  );

  const { data: teamTaken } = useTeamPlannerTaken(
    teamWerknemerIds,
    tab === 'team',
  );

  // Reset team member selection on tab/datum/filter changes
  useEffect(() => {
    setGeselecteerdeTeamWerknemer(null);
  }, [tab, huidigeDatum, filter]);

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

  async function handleTaakAfgewerkt(taakId: number) {
    await fetch(`/api/taken/${taakId}/afgewerkt`, { method: 'PUT' });
    void queryClient.invalidateQueries({
      queryKey: ['planner-taken-werknemer'],
    });
  }

  async function handleTaakVerwijder(taakId: number) {
    await fetch(`/api/taken/${taakId}`, { method: 'DELETE' });
    void queryClient.invalidateQueries({
      queryKey: ['planner-taken-werknemer'],
    });
  }

  async function handleTeamTaakAfgewerkt(taakId: number, werknemerId: number) {
    const res = await fetch(`/api/taken/${taakId}/afgewerkt`, {
      method: 'PUT',
    });
    if (res.ok) {
      void queryClient.invalidateQueries({ queryKey: ['team-planner-taken'] });
    }
    void werknemerId;
  }

  async function handleTeamTaakVerwijder(taakId: number, werknemerId: number) {
    const res = await fetch(`/api/taken/${taakId}`, { method: 'DELETE' });
    if (res.ok) {
      void queryClient.invalidateQueries({ queryKey: ['team-planner-taken'] });
    }
    void werknemerId;
  }

  function navigeerNaarDag(datum: Date) {
    setHuidigeDatum(datum);
    setGeselecteerdeDag(datum);
    setView('dag');
  }

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

  // Eigen afwezigheid (gefilterd op eigenId voor personal views)
  const eigenAfwezigheid = useMemo(
    () => afwezigheden.filter((a) => a.werknemerId === eigenId),
    [afwezigheden, eigenId],
  );

  // taken as mutable for local optimistic updates on task handlers
  const takenLocal: PlannerTaak[] = taken;

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
            kanTeamZien={kanTeamZien}
            kanShiftAanmaken={kanShiftAanmaken}
            onViewChange={setView}
            onTabChange={(t) => {
              setTab(t);
              if (t !== 'team') setFilter(LEEG_FILTER);
            }}
            onNavigate={navigeer}
            onVandaag={handleVandaag}
            onFilterChange={handleFilterChange}
            onShiftAanmaken={() => setShiftAanmakenOpen(true)}
          />

          <div className="flex flex-col lg:flex-row gap-4 w-full flex-1 min-h-0">
            <div className="flex-1 min-w-0 rounded-xl h-full min-h-64 overflow-y-auto scroll-hidden">
              {view === 'maand' && (
                <>
                  <div className="hidden sm:block h-full">
                    <MonthView
                      huidigeDatum={huidigeDatum}
                      afwezigheden={
                        tab === 'team' ? filteredAfwezigheden : eigenAfwezigheid
                      }
                      eigenShiften={eigenShiften}
                      geselecteerdeDag={geselecteerdeDag}
                      onSelectDag={(datum) => {
                        setGeselecteerdeDag(datum);
                        setGeselecteerdeTeamWerknemer(null);
                      }}
                      onNavigeerNaarDag={navigeerNaarDag}
                      onVerlofAanvragen={(datum) =>
                        setPlannerModal({ type: 'verlof', datum })
                      }
                      onAfwezigheidMelden={(datum) =>
                        setPlannerModal({ type: 'afwezigheid', datum })
                      }
                      tab={tab}
                      teamWerknemers={tab === 'team' ? teamWerknemers : []}
                      teamShiften={teamShiften}
                      geselecteerdeTeamWerknemer={geselecteerdeTeamWerknemer}
                      onSelectTeamWerknemer={(werknemerId, datum) => {
                        setGeselecteerdeDag(datum);
                        setGeselecteerdeTeamWerknemer(werknemerId);
                      }}
                    />
                  </div>
                  <div className="sm:hidden flex flex-col items-center justify-center gap-2 h-full min-h-48 text-zinc-400 text-sm text-center px-4">
                    <span>Maandoverzicht is niet beschikbaar op mobiel.</span>
                    <span className="text-xs">
                      Schakel naar week- of dagweergave via de knoppen
                      hierboven.
                    </span>
                  </div>
                </>
              )}
              {view === 'week' && (
                <WeekView
                  huidigeDatum={huidigeDatum}
                  afwezigheden={
                    tab === 'team' ? filteredAfwezigheden : eigenAfwezigheid
                  }
                  taken={takenLocal}
                  eigenShiften={eigenShiften}
                  geselecteerdeDag={geselecteerdeDag}
                  onSelectDag={(datum) => {
                    setGeselecteerdeDag(datum);
                    setGeselecteerdeTeamWerknemer(null);
                  }}
                  onNavigeerNaarDag={navigeerNaarDag}
                  tab={tab}
                  teamWerknemers={tab === 'team' ? teamWerknemers : []}
                  teamShiften={teamShiften}
                  geselecteerdeTeamWerknemer={geselecteerdeTeamWerknemer}
                  onSelectTeamWerknemer={(werknemerId, datum) => {
                    setGeselecteerdeDag(datum);
                    setGeselecteerdeTeamWerknemer(werknemerId);
                  }}
                />
              )}
              {view === 'dag' && (
                <DayView
                  huidigeDatum={huidigeDatum}
                  afwezigheden={filteredAfwezigheden}
                  taken={takenLocal}
                  filter={filter}
                  teams={teams}
                  teamWerknemers={teamWerknemers}
                  alleWerknemers={alleWerknemers}
                  isManager={isManager}
                  tab={tab}
                  eigenShiften={eigenShiften}
                  teamTaken={teamTaken}
                  onAfgewerkt={handleTaakAfgewerkt}
                />
              )}
            </div>

            {view !== 'dag' && geselecteerdeDag && (
              <DetailPanel
                geselecteerdeDag={geselecteerdeDag}
                afwezigheden={
                  tab === 'team' ? filteredAfwezigheden : eigenAfwezigheid
                }
                eigenShiften={eigenShiften}
                taken={takenLocal}
                isManager={isManager}
                onAfgewerkt={handleTaakAfgewerkt}
                onVerwijder={isManager ? handleTaakVerwijder : undefined}
                tab={tab}
                teamWerknemers={tab === 'team' ? teamWerknemers : []}
                teamShiften={teamShiften}
                teamTaken={teamTaken}
                geselecteerdeTeamWerknemer={geselecteerdeTeamWerknemer}
                onTeamTaakAfgewerkt={(taakId, werknemerId) =>
                  handleTeamTaakAfgewerkt(taakId, werknemerId)
                }
                onTeamTaakVerwijder={
                  isManager
                    ? (taakId, werknemerId) =>
                        handleTeamTaakVerwijder(taakId, werknemerId)
                    : undefined
                }
              />
            )}
          </div>
        </div>
      </AppContainer>

      {plannerModal && user?.id && (
        <PlannerAanvraagModal
          type={plannerModal.type}
          datum={plannerModal.datum}
          werknemerId={Number(user.id)}
          onClose={() => setPlannerModal(null)}
        />
      )}

      {shiftAanmakenOpen && (
        <ShiftAanmakenModal
          eigenId={eigenId}
          eigenVoornaam={eigenVoornaam}
          eigenNaam={eigenNaamStr}
          werknemers={alleWerknemers}
          teams={teams}
          isManager={isManager}
          isSupervisor={isSupervisor}
          huidigeDatum={huidigeDatum}
          onClose={() => setShiftAanmakenOpen(false)}
          onSuccess={() => {
            void queryClient.invalidateQueries({
              queryKey: ['eigen-planner-shiften'],
            });
          }}
        />
      )}
    </PageContainer>
  );
}

export default function PlannerPage() {
  return (
    <Suspense>
      <PlannerPageInner />
    </Suspense>
  );
}
