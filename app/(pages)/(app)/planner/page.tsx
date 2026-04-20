'use client';

import { useSession } from 'next-auth/react';
import { useEffect, useMemo, useState } from 'react';
import PageHeader from '@/components/design system/PageHeader/PageHeader';

type View = 'maand' | 'week' | 'dag';

interface Afwezigheid {
  werknemerId: number;
  voornaam: string;
  naam: string;
  type: string;
  startDatum: string;
  eindDatum: string;
  status: string | null;
}

const BASE = process.env.NEXT_PUBLIC_API_URL ?? 'http://localhost:8080/api';
const DAGEN_KORT = ['ma', 'di', 'wo', 'do', 'vr', 'za', 'zo'];
const MAANDEN = [
  'januari',
  'februari',
  'maart',
  'april',
  'mei',
  'juni',
  'juli',
  'augustus',
  'september',
  'oktober',
  'november',
  'december',
];

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
    const van = new Date(
      huidigeDatum.getFullYear(),
      huidigeDatum.getMonth() - 3,
      1,
    )
      .toISOString()
      .split('T')[0];
    const tot = new Date(
      huidigeDatum.getFullYear(),
      huidigeDatum.getMonth() + 9,
      0,
    )
      .toISOString()
      .split('T')[0];
    fetch(`${BASE}/planning/team/${user.id}?van=${van}&tot=${tot}`, {
      headers: authHeader,
    })
      .then((res) => res.json())
      .then(setAfwezigheden)
      .catch(console.error);
  }, [user, authHeader, huidigeDatum]);

  function afwezighedenOpDag(datum: Date): Afwezigheid[] {
    return afwezigheden.filter((a) => {
      const start = new Date(a.startDatum);
      const eind = new Date(a.eindDatum);
      return datum >= start && datum <= eind;
    });
  }

  function formatDag(d: Date) {
    return d.toLocaleDateString('nl-BE', {
      day: 'numeric',
      month: 'long',
      year: 'numeric',
    });
  }

  function isVandaag(d: Date) {
    const v = new Date();
    return (
      d.getDate() === v.getDate() &&
      d.getMonth() === v.getMonth() &&
      d.getFullYear() === v.getFullYear()
    );
  }

  function periodeLabel() {
    if (view === 'maand')
      return `${MAANDEN[huidigeDatum.getMonth()]} ${huidigeDatum.getFullYear()}`;
    if (view === 'week') {
      const ma = getMaandag(huidigeDatum);
      const zo = new Date(ma);
      zo.setDate(zo.getDate() + 6);
      return `${ma.getDate()} ${MAANDEN[ma.getMonth()]} – ${zo.getDate()} ${MAANDEN[zo.getMonth()]} ${zo.getFullYear()}`;
    }
    return formatDag(huidigeDatum);
  }

  function getMaandag(d: Date) {
    const ma = new Date(d);
    const dag = ma.getDay() === 0 ? 6 : ma.getDay() - 1;
    ma.setDate(ma.getDate() - dag);
    return ma;
  }

  function navigeer(richting: 1 | -1) {
    const d = new Date(huidigeDatum);
    if (view === 'maand') d.setMonth(d.getMonth() + richting);
    if (view === 'week') d.setDate(d.getDate() + richting * 7);
    if (view === 'dag') d.setDate(d.getDate() + richting);
    setHuidigeDatum(d);
  }

  function badgeKleur(a: Afwezigheid) {
    if (a.type === 'Ziekte') return 'bg-red-100 text-red-600';
    if (a.status === 'In afwachting') return 'bg-amber-100 text-amber-700';
    return 'bg-emerald-100 text-emerald-700';
  }

  function rijKleur(a: Afwezigheid) {
    if (a.type === 'Ziekte') return 'bg-red-50/60 border-red-100';
    if (a.status === 'In afwachting') return 'bg-amber-50/60 border-amber-100';
    return 'bg-emerald-50/60 border-emerald-100';
  }

  function tekenMaand() {
    const eersteVanMaand = new Date(
      huidigeDatum.getFullYear(),
      huidigeDatum.getMonth(),
      1,
    );
    const startKolom =
      eersteVanMaand.getDay() === 0 ? 6 : eersteVanMaand.getDay() - 1;
    const aantalDagen = new Date(
      huidigeDatum.getFullYear(),
      huidigeDatum.getMonth() + 1,
      0,
    ).getDate();
    const cellen: (Date | null)[] = Array(startKolom).fill(null);
    for (let i = 1; i <= aantalDagen; i++)
      cellen.push(
        new Date(huidigeDatum.getFullYear(), huidigeDatum.getMonth(), i),
      );
    while (cellen.length % 7 !== 0) cellen.push(null);

    return (
      <div className="flex flex-col gap-1">
        <div className="grid grid-cols-7 gap-1">
          {DAGEN_KORT.map((d) => (
            <div
              key={d}
              className="text-center text-xs font-bold text-zinc-400 uppercase py-2"
            >
              {d}
            </div>
          ))}
        </div>
        <div className="grid grid-cols-7 gap-1">
          {cellen.map((datum, i) => {
            if (!datum) return <div key={i} className="min-h-20" />;
            const opDag = afwezighedenOpDag(datum);
            const weekend = datum.getDay() === 0 || datum.getDay() === 6;
            const geselecteerd =
              geselecteerdeDag &&
              datum.toDateString() === geselecteerdeDag.toDateString();
            return (
              <div
                key={i}
                onClick={() => setGeselecteerdeDag(datum)}
                className={`min-h-20 rounded-2xl p-2 cursor-pointer transition-all duration-200 border
                  ${weekend ? 'bg-zinc-50 border-zinc-100' : 'bg-white border-gray-200/50'}
                  ${isVandaag(datum) ? 'border-zinc-900 border-2' : ''}
                  ${geselecteerd ? 'ring-2 ring-zinc-400' : ''}
                  hover:shadow-md hover:border-zinc-300`}
              >
                <span
                  className={`text-xs font-bold ${isVandaag(datum) ? 'text-zinc-900' : 'text-zinc-500'}`}
                >
                  {datum.getDate()}
                </span>
                <div className="flex flex-col gap-0.5 mt-1">
                  {opDag.slice(0, 2).map((a, j) => (
                    <span
                      key={j}
                      className={`text-[9px] font-bold px-1.5 py-0.5 rounded-full truncate ${badgeKleur(a)}`}
                    >
                      {a.voornaam}
                    </span>
                  ))}
                  {opDag.length > 2 && (
                    <span className="text-[9px] text-zinc-400 font-bold">
                      +{opDag.length - 2}
                    </span>
                  )}
                </div>
              </div>
            );
          })}
        </div>
      </div>
    );
  }

  function tekenWeek() {
    const maandag = getMaandag(huidigeDatum);
    const dagen = Array.from({ length: 7 }, (_, i) => {
      const d = new Date(maandag);
      d.setDate(d.getDate() + i);
      return d;
    });
    return (
      <div className="grid grid-cols-7 gap-2 min-h-64">
        {dagen.map((datum, i) => {
          const opDag = afwezighedenOpDag(datum);
          const weekend = datum.getDay() === 0 || datum.getDay() === 6;
          const geselecteerd =
            geselecteerdeDag &&
            datum.toDateString() === geselecteerdeDag.toDateString();
          return (
            <div
              key={i}
              onClick={() => setGeselecteerdeDag(datum)}
              className={`flex flex-col gap-2 rounded-2xl p-3 cursor-pointer transition-all duration-200 border
                ${weekend ? 'bg-zinc-50 border-zinc-100' : 'bg-white border-gray-200/50'}
                ${isVandaag(datum) ? 'border-zinc-900 border-2' : ''}
                ${geselecteerd ? 'ring-2 ring-zinc-400' : ''}
                hover:shadow-md`}
            >
              <div className="flex flex-col">
                <span className="text-xs font-bold text-zinc-400 uppercase">
                  {DAGEN_KORT[i]}
                </span>
                <span
                  className={`text-lg font-bold ${isVandaag(datum) ? 'text-zinc-900' : 'text-zinc-600'}`}
                >
                  {datum.getDate()}
                </span>
              </div>
              {opDag.length === 0 && (
                <span className="text-xs text-zinc-300">–</span>
              )}
              {opDag.map((a, j) => (
                <div
                  key={j}
                  className={`rounded-xl px-2 py-1.5 ${badgeKleur(a)}`}
                >
                  <span className="text-[10px] font-bold block truncate">
                    {a.voornaam} {a.naam}
                  </span>
                  <span className="text-[9px] block">
                    {a.type === 'Ziekte'
                      ? 'Ziekte'
                      : a.status === 'In afwachting'
                        ? 'Verlof (wachten)'
                        : 'Verlof'}
                  </span>
                </div>
              ))}
            </div>
          );
        })}
      </div>
    );
  }

  function tekenDag() {
    const opDag = afwezighedenOpDag(huidigeDatum);
    return (
      <div className="flex flex-col gap-3">
        {opDag.length === 0 && (
          <p className="text-sm text-zinc-400">
            Geen afwezigheden op deze dag.
          </p>
        )}
        {opDag.map((a, i) => (
          <div
            key={i}
            className={`flex items-center justify-between px-4 py-3 rounded-2xl border ${rijKleur(a)}`}
          >
            <div className="flex flex-col gap-0.5">
              <span className="text-sm font-bold text-zinc-900">
                {a.voornaam} {a.naam}
              </span>
              <span className="text-xs text-zinc-500">
                {new Date(a.startDatum).toLocaleDateString('nl-BE', {
                  day: 'numeric',
                  month: 'short',
                })}
                {a.startDatum !== a.eindDatum &&
                  ` – ${new Date(a.eindDatum).toLocaleDateString('nl-BE', { day: 'numeric', month: 'short' })}`}
              </span>
            </div>
            <div className="flex items-center gap-2">
              <span
                className={`text-xs font-bold px-2 py-1 rounded-full ${badgeKleur(a)}`}
              >
                {a.type}
              </span>
              {a.status && (
                <span
                  className={`text-xs font-bold px-2 py-1 rounded-full ${badgeKleur(a)}`}
                >
                  {a.status}
                </span>
              )}
            </div>
          </div>
        ))}
      </div>
    );
  }

  function tekenDetailPanel() {
    if (!geselecteerdeDag) return null;
    const opDag = afwezighedenOpDag(geselecteerdeDag);
    return (
      <div className="bg-white border border-gray-200/50 rounded-2xl p-5 flex flex-col gap-3 w-56 flex-shrink-0">
        <span className="text-sm font-bold text-zinc-900">
          {formatDag(geselecteerdeDag)}
        </span>
        {opDag.length === 0 && (
          <p className="text-xs text-zinc-400">Geen afwezigheden.</p>
        )}
        {opDag.map((a, i) => (
          <div
            key={i}
            className={`flex flex-col gap-0.5 px-3 py-2.5 rounded-xl border ${rijKleur(a)}`}
          >
            <span className="text-xs font-bold text-zinc-800">
              {a.voornaam} {a.naam}
            </span>
            <span
              className={`text-[10px] font-bold w-fit px-1.5 py-0.5 rounded-full ${badgeKleur(a)}`}
            >
              {a.type === 'Ziekte'
                ? 'Ziekte'
                : a.status === 'In afwachting'
                  ? 'Verlof (wachten)'
                  : 'Verlof'}
            </span>
          </div>
        ))}
      </div>
    );
  }

  return (
    <div className="w-full min-h-screen overflow-y-auto">
      <div className="w-full max-w-6xl mx-auto flex flex-col gap-6 pt-36 px-8 pb-16">
        <PageHeader />

        {/* Toolbar — view toggles gecentreerd, navigatie ernaast */}
        <div className="flex items-center justify-center gap-4 flex-wrap">
          {/* Navigatie links */}
          <button
            onClick={() => navigeer(-1)}
            className="w-8 h-8 flex items-center justify-center rounded-full bg-white border border-gray-200/50 hover:bg-zinc-100 transition-all duration-200 shadow-sm text-zinc-600 font-bold"
          >
            ‹
          </button>

          {/* Periode label */}
          <span className="text-sm font-bold text-zinc-800 min-w-52 text-center capitalize">
            {periodeLabel()}
          </span>

          {/* Navigatie rechts */}
          <button
            onClick={() => navigeer(1)}
            className="w-8 h-8 flex items-center justify-center rounded-full bg-white border border-gray-200/50 hover:bg-zinc-100 transition-all duration-200 shadow-sm text-zinc-600 font-bold"
          >
            ›
          </button>

          {/* View toggles */}
          <div className="flex gap-1 bg-gray-300/30 border border-gray-300/30 rounded-full p-1 shadow-sm">
            {(['maand', 'week', 'dag'] as View[]).map((v) => (
              <button
                key={v}
                onClick={() => setView(v)}
                className={`px-5 py-2 rounded-full text-sm font-bold capitalize transition-all duration-300 ${view === v ? 'bg-zinc-900 text-white shadow' : 'text-zinc-500 hover:text-zinc-800 hover:bg-zinc-200/50'}`}
              >
                {v}
              </button>
            ))}
          </div>

          {/* Vandaag */}
          <button
            onClick={() => {
              setHuidigeDatum(new Date());
              setGeselecteerdeDag(new Date());
            }}
            className="px-4 py-2 rounded-full bg-zinc-900 text-white text-sm font-bold hover:bg-zinc-700 active:scale-95 transition-all duration-200"
          >
            Vandaag
          </button>
        </div>

        {/* Kalender + detail panel */}
        <div className="flex gap-4">
          <div className="flex-1 min-w-0">
            {view === 'maand' && tekenMaand()}
            {view === 'week' && tekenWeek()}
            {view === 'dag' && tekenDag()}
          </div>
          {view !== 'dag' && tekenDetailPanel()}
        </div>
      </div>
    </div>
  );
}
