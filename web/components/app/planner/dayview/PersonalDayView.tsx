'use client';

import { useState } from 'react';
import type { Afwezigheid, PlannerTaak, Shift } from '../types';
import {
  badgeKleur,
  takenOpDag,
  taakBadgeKleur,
  afwezigheidLabel,
  isVrij,
  vrijReden,
} from '../utils';
import { STANDAARD_TIJDEN } from './helpers';

export function PersonalDayView({
  datum,
  afwezigheid,
  taken,
  eigenShift,
}: {
  datum: Date;
  afwezigheid: Afwezigheid[];
  taken: PlannerTaak[];
  eigenShift?: Shift;
}) {
  const [actieveTab, setActieveTab] = useState<'dag' | 'taken'>('dag');

  const dagTaken = takenOpDag(
    taken.filter((t) => !t.afgewerkt),
    datum,
  );

  const dagIsVrij = isVrij(datum) && !eigenShift;
  const startTijd =
    eigenShift?.startTijd?.substring(0, 5) ?? STANDAARD_TIJDEN.startTijd;
  const eindTijd =
    eigenShift?.eindTijd?.substring(0, 5) ?? STANDAARD_TIJDEN.eindTijd;
  const pauzeStart =
    eigenShift?.pauzeStart?.substring(0, 5) ?? STANDAARD_TIJDEN.pauzeStart;
  const pauzeEind =
    eigenShift?.pauzeEind?.substring(0, 5) ?? STANDAARD_TIJDEN.pauzeEind;
  const isAfwijkend =
    eigenShift != null &&
    (startTijd !== STANDAARD_TIJDEN.startTijd ||
      eindTijd !== STANDAARD_TIJDEN.eindTijd);

  return (
    <div className="flex flex-col gap-3 p-4">
      <span className="text-xs font-bold text-zinc-500 capitalize">
        {datum.toLocaleDateString('nl-BE', {
          weekday: 'long',
          day: 'numeric',
          month: 'long',
        })}
      </span>

      <div className="flex gap-1 border-b border-zinc-100 pb-1">
        {(['dag', 'taken'] as const).map((t) => (
          <button
            key={t}
            onClick={() => setActieveTab(t)}
            className={`text-[11px] font-semibold px-2.5 py-1 rounded-lg transition-colors
              ${
                actieveTab === t
                  ? 'bg-zinc-900 text-white'
                  : 'text-zinc-400 hover:text-zinc-600'
              }`}
          >
            {t === 'dag' ? 'Mijn dag' : 'Mijn taken vandaag'}
            {t === 'taken' && dagTaken.length > 0 && (
              <span className="ml-1 bg-zinc-600 text-white text-[9px] font-bold rounded-full px-1.5 py-px">
                {dagTaken.length}
              </span>
            )}
          </button>
        ))}
      </div>

      {actieveTab === 'dag' && (
        <>
          {dagIsVrij ? (
            <div className="flex items-center gap-2 px-3 py-2 rounded-xl border border-gray-200/40 bg-white/40">
              <span className="text-[10px] font-bold px-2 py-0.5 rounded-full bg-zinc-100 text-zinc-400 shrink-0">
                Vrij
              </span>
              <span className="text-xs text-zinc-400 italic">
                Vrij – {vrijReden(datum)}
              </span>
            </div>
          ) : (
            <div className="flex items-center gap-2 px-3 py-2 rounded-xl border border-gray-200/40 bg-white/40">
              <span className="text-[10px] font-bold px-2 py-0.5 rounded-full bg-zinc-100 text-zinc-500 shrink-0">
                Shift
              </span>
              <span
                className={`text-xs font-semibold ${isAfwijkend ? 'text-red-600' : 'text-zinc-800'}`}
              >
                {startTijd} – {eindTijd}
              </span>
              <span className="text-[10px] text-zinc-400 ml-auto shrink-0">
                pauze {pauzeStart} – {pauzeEind}
              </span>
            </div>
          )}

          {afwezigheid.map((a, i) => (
            <div
              key={i}
              className="flex items-center gap-2 px-3 py-2 rounded-xl border border-gray-200/40 bg-white/40"
            >
              <span
                className={`text-[10px] font-bold px-2 py-0.5 rounded-full shrink-0 ${badgeKleur(a)}`}
              >
                {afwezigheidLabel(a)}
              </span>
              <span className="text-xs text-zinc-600">Afwezig</span>
            </div>
          ))}
        </>
      )}

      {actieveTab === 'taken' && (
        <>
          {dagTaken.length === 0 ? (
            <p className="text-xs text-zinc-400 italic">
              Geen taken gepland voor vandaag.
            </p>
          ) : (
            dagTaken.map((t) => (
              <div
                key={t.id}
                className="flex items-center gap-2 px-3 py-2 rounded-xl border border-gray-200/40 bg-white/40"
              >
                <span
                  className={`text-[10px] font-bold px-2 py-0.5 rounded-full shrink-0 ${taakBadgeKleur(t)}`}
                >
                  {t.locatie}
                </span>
                <span className="text-xs font-semibold text-zinc-800 truncate">
                  {t.naam}
                </span>
                {t.duur && (
                  <span className="text-[10px] text-zinc-400 ml-auto shrink-0">
                    {t.duur}
                  </span>
                )}
              </div>
            ))
          )}
        </>
      )}
    </div>
  );
}
