'use client';

import { useSession } from 'next-auth/react';
import { useEffect, useMemo, useState } from 'react';
import { FaChevronLeft, FaChevronRight } from 'react-icons/fa';
import PageHeader from '@/components/design system/PageHeader/PageHeader';
import { AppContainer } from '@/components/design system/AppContainer';
import { Button } from '@/components/design system/Button';
import MonthView from './MonthView';
import WeekView from './WeekView';
import DayView from './DayView';
import DetailPanel from './components/DetailPanel';
import type { Afwezigheid, View } from './components/types';
import { periodeLabel, getPeriodBounds } from './components/utils';
import { PageContainer } from '@/components/design system/PageContainer';
import BreadcrumbInit from '@/components/app/structuur/breadcrumb/BreadCrumbInit';

const BASE = process.env.NEXT_PUBLIC_API_URL ?? 'http://localhost:8080/api';

export default function PlannerPage() {
  const { data: session } = useSession();

  const token = session?.accessToken;
  const user = session?.user;

  const [view, setView] = useState<View>('maand');
  const [huidigeDatum, setHuidigeDatum] = useState(new Date());
  const [afwezigheden, setAfwezigheden] = useState<Afwezigheid[]>([]);
  const [geselecteerdeDag, setGeselecteerdeDag] = useState<Date | null>(
    new Date(),
  );

  const authHeader = useMemo(
    () => ({ Authorization: `Bearer ${token}` }),
    [token],
  );

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

  function navigeer(richting: 1 | -1) {
    const d = new Date(huidigeDatum);
    if (view === 'maand') d.setMonth(d.getMonth() + richting);
    if (view === 'week') d.setDate(d.getDate() + richting * 7);
    if (view === 'dag') d.setDate(d.getDate() + richting);
    setHuidigeDatum(d);
  }

  return (
    <PageContainer className="h-full">
      <AppContainer>
        <BreadcrumbInit pages={['planner']} />
        <div className="w-full h-full flex flex-col gap-6">
          <div className="flex items-center gap-4 w-full">
            <div className="w-full flex flex-row gap-3 items-center">
              <Button icon={<FaChevronLeft />} onClick={() => navigeer(-1)} />
              <span className="text-sm font-bold text-zinc-800 min-w-52 text-center capitalize">
                {periodeLabel(view, huidigeDatum)}
              </span>
              <Button icon={<FaChevronRight />} onClick={() => navigeer(1)} />
            </div>

            <div className="w-fit flex flex-row gap-3 items-center">
              <div className="flex gap-1 bg-gray-300/30 border border-gray-300/30 rounded-full p-1 shadow-sm">
                {(['maand', 'week', 'dag'] as View[]).map((v) => (
                  <button
                    key={v}
                    onClick={() => setView(v)}
                    className={`px-5 py-2 rounded-full text-sm font-bold capitalize transition-all duration-300 ${
                      view === v
                        ? 'bg-zinc-900 text-white shadow'
                        : 'text-zinc-500 hover:text-zinc-800 hover:bg-zinc-200/50'
                    }`}
                  >
                    {v}
                  </button>
                ))}
              </div>

              <Button
                onClick={() => {
                  setHuidigeDatum(new Date());
                  setGeselecteerdeDag(new Date());
                }}
                textColor="white"
                color="delaware_red"
                label="Vandaag"
              />
              <div className="flex items-center gap-4 w-80" />
            </div>
          </div>

          <div className="flex gap-4 w-full h-full">
            <div className="w-full min-h-full">
              {view === 'maand' && (
                <MonthView
                  huidigeDatum={huidigeDatum}
                  afwezigheden={afwezigheden}
                  geselecteerdeDag={geselecteerdeDag}
                  onSelectDag={setGeselecteerdeDag}
                />
              )}
              {view === 'week' && (
                <WeekView
                  huidigeDatum={huidigeDatum}
                  afwezigheden={afwezigheden}
                  geselecteerdeDag={geselecteerdeDag}
                  onSelectDag={setGeselecteerdeDag}
                />
              )}
              {view === 'dag' && (
                <DayView
                  huidigeDatum={huidigeDatum}
                  afwezigheden={afwezigheden}
                />
              )}
            </div>

            {view !== 'dag' && geselecteerdeDag && (
              <DetailPanel
                geselecteerdeDag={geselecteerdeDag}
                afwezigheden={afwezigheden}
              />
            )}
          </div>
        </div>
      </AppContainer>
    </PageContainer>
  );
}
