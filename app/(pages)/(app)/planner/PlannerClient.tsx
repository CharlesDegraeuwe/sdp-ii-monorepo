'use client';

import { useState } from 'react';
import type { View } from './types';
import { getPeriodLabel } from './utils';
import { usePlanning } from './usePlanning';
import { MonthView } from './MonthView';
import { WeekView } from './WeekView';
import { DagView } from './DagView';
import { DetailPanel } from './DetailPanel';

const VIEWS: View[] = ['maand', 'week', 'dag'];

export default function PlannerClient() {
  const [view, setView] = useState<View>('maand');
  const [currentDate, setCurrentDate] = useState(new Date());
  const [selectedDate, setSelectedDate] = useState<Date>(new Date());
  const { afwezigheden, loading } = usePlanning(view, currentDate);

  const navigate = (dir: 1 | -1) => {
    setCurrentDate((prev) => {
      const next = new Date(prev);
      if (view === 'maand') next.setMonth(next.getMonth() + dir);
      else if (view === 'week') next.setDate(next.getDate() + dir * 7);
      else {
        next.setDate(next.getDate() + dir);
        setSelectedDate(next);
      }
      return next;
    });
  };

  const goToday = () => {
    const now = new Date();
    setCurrentDate(now);
    setSelectedDate(now);
  };

  const today = new Date();

  return (
    <div className="w-full h-full flex flex-col gap-3 px-6 pt-28 pb-5 overflow-y-auto">
      {/* Top bar */}
      <div className="flex flex-wrap items-center gap-3">
        <div className="flex rounded-xl overflow-hidden border border-gray-300/40 backdrop-blur-md shadow-sm shrink-0">
          {VIEWS.map((v) => (
            <button
              key={v}
              onClick={() => setView(v)}
              className={`px-4 py-1.5 text-sm font-medium capitalize transition-all duration-200 cursor-pointer
                ${view === v ? 'bg-delaware_red text-white' : 'bg-gray-300/30 text-gray-700 hover:bg-gray-300/50'}`}
            >
              {v.charAt(0).toUpperCase() + v.slice(1)}
            </button>
          ))}
        </div>

        <div className="flex items-center gap-2">
          <button
            onClick={() => navigate(-1)}
            className="w-7 h-7 flex items-center justify-center rounded-full bg-gray-300/30 border border-gray-300/30 hover:bg-gray-400/30 transition-all text-base cursor-pointer"
          >
            ‹
          </button>
          <span className="font-semibold text-sm min-w-[160px] text-center select-none">
            {getPeriodLabel(view, currentDate)}
          </span>
          <button
            onClick={() => navigate(1)}
            className="w-7 h-7 flex items-center justify-center rounded-full bg-gray-300/30 border border-gray-300/30 hover:bg-gray-400/30 transition-all text-base cursor-pointer"
          >
            ›
          </button>
        </div>

        <div className="flex-1" />

        <button
          onClick={goToday}
          className="px-3 py-1.5 rounded-xl text-sm font-medium bg-gray-300/30 border border-gray-300/30 hover:bg-gray-400/30 transition-all cursor-pointer shrink-0"
        >
          Vandaag
        </button>
      </div>

      {/* Main content */}
      <div className="flex gap-4 flex-1 min-h-0">
        <div className="flex-1 min-h-0 relative">
          {loading && (
            <div className="absolute inset-0 flex items-center justify-center z-10 rounded-4xl">
              <span className="text-xs text-gray-400 animate-pulse">
                Laden...
              </span>
            </div>
          )}

          {view === 'maand' && (
            <MonthView
              currentDate={currentDate}
              selectedDate={selectedDate}
              today={today}
              afwezigheden={afwezigheden}
              onSelectDay={setSelectedDate}
            />
          )}
          {view === 'week' && (
            <WeekView
              currentDate={currentDate}
              selectedDate={selectedDate}
              today={today}
              afwezigheden={afwezigheden}
              onSelectDay={setSelectedDate}
            />
          )}
          {view === 'dag' && <DagView />}
        </div>

        <DetailPanel selectedDate={selectedDate} afwezigheden={afwezigheden} />
      </div>
    </div>
  );
}
