'use client';

import { useState } from 'react';
import { MdCheckCircle, MdRadioButtonUnchecked } from 'react-icons/md';
import { MdSearch } from 'react-icons/md';
import type { Afwezigheid, PlannerTaak, Shift } from '../types';
import {
  badgeKleur,
  takenOpDag,
  afwezigheidLabel,
  isVrij,
  vrijReden,
} from '../utils';

function TaakRegel({
  taak,
  onAfgewerkt,
}: {
  taak: PlannerTaak;
  onAfgewerkt?: (id: number) => void;
}) {
  return (
    <div
      className={`flex items-center gap-3 px-3 py-2.5 rounded-xl border transition-colors ${
        taak.afgewerkt
          ? 'bg-zinc-50/60 border-zinc-100'
          : taak.belangrijk
            ? 'bg-rose-50/60 border-rose-100'
            : 'bg-white/60 border-gray-200/60'
      }`}
    >
      <button
        onClick={() => !taak.afgewerkt && onAfgewerkt?.(taak.id)}
        disabled={taak.afgewerkt}
        className={`shrink-0 transition-colors ${
          taak.afgewerkt
            ? 'text-emerald-500 cursor-default'
            : 'text-zinc-300 hover:text-emerald-500 cursor-pointer'
        }`}
        title={taak.afgewerkt ? 'Afgewerkt' : 'Markeer als afgewerkt'}
      >
        {taak.afgewerkt ? (
          <MdCheckCircle size={18} />
        ) : (
          <MdRadioButtonUnchecked size={18} />
        )}
      </button>
      <div className="flex flex-col flex-1 min-w-0">
        <span
          className={`text-sm font-semibold truncate ${
            taak.afgewerkt ? 'line-through text-zinc-400' : 'text-zinc-800'
          }`}
        >
          {taak.naam}
        </span>
        {taak.specificaties && (
          <span className="text-xs text-zinc-400 truncate">
            {taak.specificaties}
          </span>
        )}
      </div>
      {taak.duur && (
        <span className="text-[10px] text-zinc-400 shrink-0">{taak.duur}</span>
      )}
    </div>
  );
}

export function PersonalDayView({
  datum,
  afwezigheid,
  taken,
  eigenShift,
  onAfgewerkt,
}: {
  datum: Date;
  afwezigheid: Afwezigheid[];
  taken: PlannerTaak[];
  eigenShift?: Shift;
  onAfgewerkt?: (id: number) => void;
}) {
  const [filter, setFilter] = useState('');

  const dagAfwezigheid = afwezigheid;
  const isAfwezig = dagAfwezigheid.length > 0;
  const dagIsVrij = isVrij(datum) && !eigenShift;

  const startTijd = eigenShift?.startTijd?.substring(0, 5) ?? '09:00';
  const eindTijd = eigenShift?.eindTijd?.substring(0, 5) ?? '17:00';
  const pauzeStart = eigenShift?.pauzeStart?.substring(0, 5) ?? '12:00';
  const pauzeEind = eigenShift?.pauzeEind?.substring(0, 5) ?? '13:00';

  const allesTaken = takenOpDag(taken, datum);
  const gefilterd = filter.trim()
    ? allesTaken.filter((t) =>
        t.naam.toLowerCase().includes(filter.toLowerCase()),
      )
    : allesTaken;

  const todoTaken = gefilterd.filter((t) => !t.afgewerkt);
  const doneTaken = gefilterd.filter((t) => t.afgewerkt);

  return (
    <div className="flex flex-col gap-4 p-4 h-full overflow-y-auto scroll-hidden">
      {/* Datum header */}
      <div className="flex flex-col gap-2">
        <span className="text-sm font-bold text-zinc-800 capitalize">
          {datum.toLocaleDateString('nl-BE', {
            weekday: 'long',
            day: 'numeric',
            month: 'long',
            year: 'numeric',
          })}
        </span>

        {/* Status: vrij / afwezig / shift */}
        {dagIsVrij ? (
          <div className="flex items-center gap-2 px-3 py-2 rounded-xl border border-zinc-100 bg-zinc-50">
            <span className="text-xs font-semibold text-zinc-400">
              Vrij – {vrijReden(datum)}
            </span>
          </div>
        ) : isAfwezig ? (
          <div
            className={`flex items-center gap-2 px-3 py-2.5 rounded-xl border ${
              dagAfwezigheid[0].type === 'Ziekte'
                ? 'bg-red-50 border-red-100'
                : dagAfwezigheid[0].status === 'In afwachting'
                  ? 'bg-amber-50 border-amber-100'
                  : 'bg-emerald-50 border-emerald-100'
            }`}
          >
            <span
              className={`text-xs font-bold px-2 py-0.5 rounded-full ${badgeKleur(dagAfwezigheid[0])}`}
            >
              {afwezigheidLabel(dagAfwezigheid[0])}
            </span>
            <span className="text-xs text-zinc-500">
              Je bent deze dag niet aanwezig
            </span>
          </div>
        ) : (
          <div className="flex items-center gap-3 px-3 py-2.5 rounded-xl border border-rose-100 bg-rose-50/50">
            <div className="flex flex-col flex-1">
              <span className="text-xs font-bold text-rose-700">
                {startTijd} – {pauzeStart}
              </span>
              <span className="text-[10px] text-zinc-400">Voormiddag</span>
            </div>
            <div className="w-px h-6 bg-rose-200" />
            <div className="flex flex-col flex-1">
              <span className="text-xs font-bold text-rose-700">
                {pauzeEind} – {eindTijd}
              </span>
              <span className="text-[10px] text-zinc-400">Namiddag</span>
            </div>
          </div>
        )}
      </div>

      {/* Taken sectie (niet tonen als afwezig of vrij) */}
      {!isAfwezig && !dagIsVrij && (
        <>
          {/* Filter */}
          <div className="relative">
            <MdSearch
              size={16}
              className="absolute left-3 top-1/2 -translate-y-1/2 text-zinc-400"
            />
            <input
              type="text"
              value={filter}
              onChange={(e) => setFilter(e.target.value)}
              placeholder="Taken zoeken..."
              className="w-full rounded-full border border-gray-300/30 bg-gray-300/30 shadow-inner pl-8 pr-4 py-2 text-sm outline-none focus:border-gray-500/30"
            />
          </div>

          {/* Te doen */}
          <div className="flex flex-col gap-2">
            <div className="flex items-center justify-between">
              <span className="text-xs font-bold text-zinc-500 uppercase tracking-wide">
                Te doen
              </span>
              {todoTaken.length > 0 && (
                <span className="text-[10px] font-bold bg-zinc-100 text-zinc-500 rounded-full px-2 py-0.5">
                  {todoTaken.length}
                </span>
              )}
            </div>
            {todoTaken.length === 0 ? (
              <p className="text-xs text-zinc-400 italic px-1">
                {filter ? 'Geen resultaten.' : 'Geen openstaande taken.'}
              </p>
            ) : (
              <div className="flex flex-col gap-1.5">
                {todoTaken.map((t) => (
                  <TaakRegel key={t.id} taak={t} onAfgewerkt={onAfgewerkt} />
                ))}
              </div>
            )}
          </div>

          {/* Afgewerkt */}
          {doneTaken.length > 0 && (
            <div className="flex flex-col gap-2">
              <div className="flex items-center justify-between">
                <span className="text-xs font-bold text-zinc-400 uppercase tracking-wide">
                  Afgewerkt
                </span>
                <span className="text-[10px] font-bold bg-emerald-50 text-emerald-600 rounded-full px-2 py-0.5">
                  {doneTaken.length}
                </span>
              </div>
              <div className="flex flex-col gap-1.5">
                {doneTaken.map((t) => (
                  <TaakRegel key={t.id} taak={t} />
                ))}
              </div>
            </div>
          )}

          {allesTaken.length === 0 && (
            <p className="text-xs text-zinc-400 italic px-1">
              Geen taken gepland voor deze dag.
            </p>
          )}
        </>
      )}
    </div>
  );
}
