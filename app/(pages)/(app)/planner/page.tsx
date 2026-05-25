'use client';

import { Suspense } from 'react';
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
  Shift,
  View,
} from '../../../../components/app/planner/types';
import { getPeriodBounds, getMaandag, mapTaakVanBackend } from '../../../../components/app/planner/utils';
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

const BASE = process.env.NEXT_PUBLIC_API_URL ?? 'http://localhost:8080/api';

const LEEG_FILTER: FilterState = {
  locatieId: null,
  teamId: null,
  werknemerId: null,
};

function PlannerPageInner() {
  const { data: session } = useSession();
  const token = session?.accessToken;
  const user = session?.user;
  const rol = ((session?.user as Record<string, unknown>)?.rol as string) ?? '';
  const isManager = ['Manager', 'Admin'].includes(rol);
  const kanTeamZien = ['Manager', 'Admin', 'Supervisor'].includes(rol);
  const kanShiftAanmaken = kanTeamZien;
  const eigenId = Number((session?.user as Record<string, unknown>)?.id ?? 0);
  const eigenVoornaam = ((session?.user as Record<string, unknown>)?.voornaam as string) ?? '';
  const eigenNaamStr = ((session?.user as Record<string, unknown>)?.naam as string) ?? '';

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
  const [geselecteerdeDag, setGeselecteerdeDag] = useState<Date | null>(new Date());
  const [filter, setFilter] = useState<FilterState>(LEEG_FILTER);
  const [eigenShiften, setEigenShiften] = useState<Shift[]>([]);
  const [plannerModal, setPlannerModal] = useState<{
    type: 'verlof' | 'afwezigheid';
    datum: Date;
  } | null>(null);

  const [teamShiften, setTeamShiften] = useState<Shift[]>([]);
  const [teamTaken, setTeamTaken] = useState<Record<number, PlannerTaak[]>>({});
  const [shiftAanmakenOpen, setShiftAanmakenOpen] = useState(false);
  const [shiftenRefresh, setShiftenRefresh] = useState(0);
  const [geselecteerdeTeamWerknemer, setGeselecteerdeTeamWerknemer] = useState<number | null>(null);

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

  // Fetch afwezigheid voor de huidige periode
  useEffect(() => {
    if (!user?.id) return;
    const { van, tot } = getPeriodBounds(view, huidigeDatum);
    fetch(`${BASE}/planning/team/${user.id}?van=${van}&tot=${tot}`, {
      headers: authHeader,
    })
      .then((res) => res.json())
      .then(setAfwezigheden)
      .catch(console.error);
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [user?.id, authHeader, huidigeDatum, view]);

  // Fetch eigen shiften via bereik endpoint
  useEffect(() => {
    if (!user?.id) return;
    let van: string;
    let tot: string;

    if (view === 'maand') {
      van = new Date(huidigeDatum.getFullYear(), huidigeDatum.getMonth(), 1)
        .toISOString()
        .split('T')[0];
      tot = new Date(huidigeDatum.getFullYear(), huidigeDatum.getMonth() + 1, 0)
        .toISOString()
        .split('T')[0];
    } else if (view === 'week') {
      const ma = getMaandag(huidigeDatum);
      van = ma.toISOString().split('T')[0];
      const zo = new Date(ma);
      zo.setDate(zo.getDate() + 6);
      tot = zo.toISOString().split('T')[0];
    } else {
      van = huidigeDatum.toISOString().split('T')[0];
      tot = van;
    }

    fetch(`/api/shifts/werknemer/${user.id}/bereik?van=${van}&tot=${tot}`)
      .then((r) => (r.ok ? (r.json() as Promise<Shift[]>) : []))
      .then(setEigenShiften)
      .catch(() => setEigenShiften([]));
  }, [user?.id, view, huidigeDatum, shiftenRefresh]);

  useEffect(() => {
    if (!user?.id) return;
    fetch(`/api/taken/werknemer/${user.id}`)
      .then((res) => {
        if (!res.ok) throw new Error('Failed');
        return res.json();
      })
      .then((data: Record<string, unknown>[]) =>
        setTaken(data.map(mapTaakVanBackend)),
      )
      .catch(console.error);
  }, [user?.id]);

  useEffect(() => {
    if (tab !== 'team' || view === 'dag' || teamWerknemers.length === 0) {
      setTeamShiften([]);
      return;
    }
    let van: string;
    let tot: string;
    if (view === 'maand') {
      van = new Date(huidigeDatum.getFullYear(), huidigeDatum.getMonth(), 1)
        .toISOString().split('T')[0];
      tot = new Date(huidigeDatum.getFullYear(), huidigeDatum.getMonth() + 1, 0)
        .toISOString().split('T')[0];
    } else {
      const ma = getMaandag(huidigeDatum);
      van = ma.toISOString().split('T')[0];
      const zo = new Date(ma);
      zo.setDate(zo.getDate() + 6);
      tot = zo.toISOString().split('T')[0];
    }
    Promise.all(
      teamWerknemers.map((w) =>
        fetch(`/api/shifts/werknemer/${w.id}/bereik?van=${van}&tot=${tot}`)
          .then((r) => (r.ok ? (r.json() as Promise<Shift[]>) : []))
          .catch(() => [] as Shift[]),
      ),
    )
      .then((results) => setTeamShiften(results.flat()))
      .catch(() => setTeamShiften([]));
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [tab, view, huidigeDatum, teamWerknemers]);

  // Fetch taken voor alle teamleden
  useEffect(() => {
    if (tab !== 'team' || teamWerknemers.length === 0) {
      setTeamTaken({});
      return;
    }
    Promise.all(
      teamWerknemers.map((w) =>
        fetch(`/api/taken/werknemer/${w.id}`)
          .then((r) => (r.ok ? r.json() : []))
          .then((data: Record<string, unknown>[]) => [w.id, data.map(mapTaakVanBackend)] as [number, PlannerTaak[]])
          .catch(() => [w.id, []] as [number, PlannerTaak[]]),
      ),
    )
      .then((results) => setTeamTaken(Object.fromEntries(results)))
      .catch(() => setTeamTaken({}));
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [tab, teamWerknemers]);

  // Reset team member selection on tab/datum/filter changes
  useEffect(() => {
    setGeselecteerdeTeamWerknemer(null);
  }, [tab, huidigeDatum, filter]);

  async function handleTaakAfgewerkt(taakId: number) {
    const res = await fetch(`/api/taken/${taakId}/afgewerkt`, { method: 'PUT' });
    if (res.ok) {
      setTaken((prev) =>
        prev.map((t) => (t.id === taakId ? { ...t, afgewerkt: true } : t)),
      );
    }
  }

  async function handleTaakVerwijder(taakId: number) {
    const res = await fetch(`/api/taken/${taakId}`, { method: 'DELETE' });
    if (res.ok) {
      setTaken((prev) => prev.filter((t) => t.id !== taakId));
    }
  }

  async function handleTeamTaakAfgewerkt(taakId: number, werknemerId: number) {
    const res = await fetch(`/api/taken/${taakId}/afgewerkt`, { method: 'PUT' });
    if (res.ok) {
      setTeamTaken((prev) => ({
        ...prev,
        [werknemerId]: (prev[werknemerId] ?? []).map((t) =>
          t.id === taakId ? { ...t, afgewerkt: true } : t,
        ),
      }));
    }
  }

  async function handleTeamTaakVerwijder(taakId: number, werknemerId: number) {
    const res = await fetch(`/api/taken/${taakId}`, { method: 'DELETE' });
    if (res.ok) {
      setTeamTaken((prev) => ({
        ...prev,
        [werknemerId]: (prev[werknemerId] ?? []).filter((t) => t.id !== taakId),
      }));
    }
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
                      afwezigheden={tab === 'team' ? filteredAfwezigheden : eigenAfwezigheid}
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
                      Schakel naar week- of dagweergave via de knoppen hierboven.
                    </span>
                  </div>
                </>
              )}
              {view === 'week' && (
                <WeekView
                  huidigeDatum={huidigeDatum}
                  afwezigheden={tab === 'team' ? filteredAfwezigheden : eigenAfwezigheid}
                  taken={taken}
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
                  taken={taken}
                  filter={filter}
                  teams={teams}
                  teamWerknemers={teamWerknemers}
                  alleWerknemers={alleWerknemers}
                  isManager={isManager}
                  tab={tab}
                  eigenShiften={eigenShiften}
                  onAfgewerkt={handleTaakAfgewerkt}
                />
              )}
            </div>

            {view !== 'dag' && geselecteerdeDag && (
              <DetailPanel
                geselecteerdeDag={geselecteerdeDag}
                afwezigheden={tab === 'team' ? filteredAfwezigheden : eigenAfwezigheid}
                eigenShiften={eigenShiften}
                taken={taken}
                isManager={isManager}
                onAfgewerkt={handleTaakAfgewerkt}
                onVerwijder={isManager ? handleTaakVerwijder : undefined}
                tab={tab}
                teamWerknemers={tab === 'team' ? teamWerknemers : []}
                teamShiften={teamShiften}
                teamTaken={teamTaken}
                geselecteerdeTeamWerknemer={geselecteerdeTeamWerknemer}
                onTeamTaakAfgewerkt={(taakId, werknemerId) => handleTeamTaakAfgewerkt(taakId, werknemerId)}
                onTeamTaakVerwijder={isManager ? (taakId, werknemerId) => handleTeamTaakVerwijder(taakId, werknemerId) : undefined}
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
          huidigeDatum={huidigeDatum}
          onClose={() => setShiftAanmakenOpen(false)}
          onSuccess={() => setShiftenRefresh((n) => n + 1)}
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
