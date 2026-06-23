'use client';

import { useState } from 'react';
import {
  MdCheckCircle,
  MdRadioButtonUnchecked,
  MdSearch,
} from 'react-icons/md';
import type { Afwezigheid, PlannerTaak, Shift } from '../types';
import { afwezigheidLabel, isVrij, vrijReden } from '../utils';
import { Container } from '@/components/design-system/Container';

function TaakRegel({
  taak,
  onAfgewerkt,
}: {
  taak: PlannerTaak;
  onAfgewerkt?: (id: number) => void;
}) {
  return (
    <div
      className={`flex items-center gap-2.5 px-3 py-2.5 rounded-2xl border transition-all duration-200 ${
        taak.afgewerkt
          ? 'border-gray-300/30 bg-gray-300/10 opacity-50'
          : taak.belangrijk
            ? 'border-rose-200 bg-rose-50/60'
            : 'border-gray-300/30 bg-gray-300/20'
      }`}
    >
      <button
        onClick={() => !taak.afgewerkt && onAfgewerkt?.(taak.id)}
        disabled={taak.afgewerkt}
        className={`shrink-0 transition-colors ${
          taak.afgewerkt
            ? 'text-emerald-500 cursor-default'
            : taak.belangrijk
              ? 'text-rose-300 hover:text-rose-500 cursor-pointer'
              : 'text-zinc-300 hover:text-zinc-500 cursor-pointer'
        }`}
      >
        {taak.afgewerkt ? (
          <MdCheckCircle size={16} />
        ) : (
          <MdRadioButtonUnchecked size={16} />
        )}
      </button>

      <span
        className={`text-xs font-semibold flex-1 truncate ${
          taak.afgewerkt
            ? 'line-through text-zinc-400'
            : taak.belangrijk
              ? 'text-rose-700'
              : 'text-zinc-800'
        }`}
      >
        {taak.naam}
      </span>

      {taak.belangrijk && !taak.afgewerkt && (
        <div className="w-1.5 h-1.5 rounded-full bg-rose-400 shrink-0" />
      )}
      {taak.deadline && (
        <span
          className={`text-[10px] shrink-0 ${taak.belangrijk && !taak.afgewerkt ? 'text-rose-400' : 'text-zinc-400'}`}
        >
          {new Date(taak.deadline).toLocaleDateString('nl-BE', {
            day: 'numeric',
            month: 'short',
          })}
        </span>
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

  const isAfwezig = afwezigheid.length > 0;
  const dagIsVrij = isVrij(datum) && !eigenShift;

  const startTijd = eigenShift?.startTijd?.substring(0, 5) ?? '09:00';
  const eindTijd = eigenShift?.eindTijd?.substring(0, 5) ?? '17:00';
  const pauzeStart = eigenShift?.pauzeStart?.substring(0, 5) ?? '12:00';
  const pauzeEind = eigenShift?.pauzeEind?.substring(0, 5) ?? '13:00';

  const gefilterd = filter.trim()
    ? taken.filter((t) => t.naam.toLowerCase().includes(filter.toLowerCase()))
    : taken;
  const todoTaken = gefilterd.filter((t) => !t.afgewerkt);
  const doneTaken = gefilterd.filter((t) => t.afgewerkt);

  const dagNaam = datum.toLocaleDateString('nl-BE', { weekday: 'long' });
  const dagNummer = datum.toLocaleDateString('nl-BE', {
    day: 'numeric',
    month: 'long',
    year: 'numeric',
  });

  return (
    <div className="h-full min-h-0 flex flex-col px-2 py-2 gap-6">
      {/* Datum header */}
      <div className="shrink-0">
        <p className="text-[10px] font-bold text-zinc-400 uppercase tracking-widest capitalize">
          {dagNaam}
        </p>
        <p className="text-xl font-bold text-zinc-900 capitalize mt-0.5">
          {dagNummer}
        </p>
      </div>

      {/* Twee kolommen */}
      <div className="flex flex-1 min-h-0 gap-6">
        {/* Linker kolom — Werkdag */}
        <div className="flex flex-col gap-4 w-72 shrink-0">
          <Container label="Mijn Werkdag">
            {dagIsVrij ? (
              <div className="flex items-center gap-2.5 px-3 py-3 rounded-2xl border border-gray-300/30 bg-gray-300/20">
                <div className="w-1.5 h-1.5 rounded-full bg-zinc-300 shrink-0" />
                <span className="text-xs font-semibold text-zinc-500 italic">
                  Vrij – {vrijReden(datum)}
                </span>
              </div>
            ) : isAfwezig ? (
              <div
                className={`flex items-center gap-2.5 px-3 py-3 rounded-2xl border ${
                  afwezigheid[0].type === 'Ziekte'
                    ? 'border-rose-200 bg-rose-50/60'
                    : afwezigheid[0].status === 'In afwachting'
                      ? 'border-amber-200 bg-amber-50/60'
                      : 'border-emerald-200 bg-emerald-50/60'
                }`}
              >
                <div
                  className={`w-1.5 h-1.5 rounded-full shrink-0 ${
                    afwezigheid[0].type === 'Ziekte'
                      ? 'bg-rose-400'
                      : afwezigheid[0].status === 'In afwachting'
                        ? 'bg-amber-400'
                        : 'bg-emerald-400'
                  }`}
                />
                <div className="flex flex-col gap-0.5">
                  <span
                    className={`text-xs font-semibold ${
                      afwezigheid[0].type === 'Ziekte'
                        ? 'text-rose-700'
                        : afwezigheid[0].status === 'In afwachting'
                          ? 'text-amber-700'
                          : 'text-emerald-700'
                    }`}
                  >
                    {afwezigheidLabel(afwezigheid[0])}
                  </span>
                  <span className="text-[10px] text-zinc-400">
                    Niet aanwezig
                  </span>
                </div>
              </div>
            ) : (
              <div className="flex flex-col gap-2.5">
                <div className="flex gap-2.5">
                  <div className="flex-1 flex flex-col gap-1 px-3 py-3 rounded-2xl border border-blue-200/50 bg-blue-50/40">
                    <span className="text-[10px] font-bold text-zinc-400 uppercase tracking-wide">
                      Voormiddag
                    </span>
                    <span className="text-sm font-bold text-blue-800">
                      {startTijd} – {pauzeStart}
                    </span>
                  </div>
                  <div className="flex-1 flex flex-col gap-1 px-3 py-3 rounded-2xl border border-blue-200/50 bg-blue-50/40">
                    <span className="text-[10px] font-bold text-zinc-400 uppercase tracking-wide">
                      Namiddag
                    </span>
                    <span className="text-sm font-bold text-blue-800">
                      {pauzeEind} – {eindTijd}
                    </span>
                  </div>
                </div>
                <div className="flex items-center gap-2.5 px-3 py-2.5 rounded-2xl border border-gray-300/30 bg-gray-300/20">
                  <span className="text-[10px] font-bold text-zinc-400 uppercase tracking-wide">
                    Pauze
                  </span>
                  <span className="text-xs font-semibold text-zinc-500">
                    {pauzeStart} – {pauzeEind}
                  </span>
                </div>
              </div>
            )}
          </Container>
        </div>

        {/* Rechter kolom — Taken */}
        <div className="flex flex-col flex-1 min-h-0">
          {isAfwezig || dagIsVrij ? (
            <Container label="Mijn Taken">
              <p className="h-full w-full flex-1 text-xs text-zinc-400 px-1 py-2">
                {isAfwezig
                  ? 'Geen taken op afwezige dag.'
                  : 'Geen taken op vrije dag.'}
              </p>
            </Container>
          ) : (
            <Container label="Mijn Taken">
              <div className="flex flex-col gap-3 flex-1 min-h-0 h-full">
                {/* Zoekbalk */}
                <div className="relative shrink-0">
                  <MdSearch
                    size={14}
                    className="absolute left-4 top-1/2 -translate-y-1/2 text-zinc-400 pointer-events-none"
                  />
                  <input
                    type="text"
                    value={filter}
                    onChange={(e) => setFilter(e.target.value)}
                    placeholder="Taken zoeken..."
                    className="w-full rounded-full outline-none ring-0 border border-gray-300/30 focus:border-gray-700/30 pl-9 pr-5 py-2.5 bg-gray-300/30 shadow-inner text-xs text-zinc-800 placeholder-zinc-400"
                  />
                </div>

                {/* Scrollbaar takenoverzicht */}
                <div className="flex flex-col w-full h-full  gap-4 flex-1 min-h-0 overflow-y-auto scroll-hidden">
                  {/* Te doen */}
                  <div className="flex flex-col gap-2 w-full h-full ">
                    <div className="flex items-center justify-between px-1">
                      <span className="text-[10px] font-bold text-zinc-500 uppercase tracking-widest">
                        Te doen
                      </span>
                      {todoTaken.length > 0 && (
                        <span className="text-[10px] font-bold bg-gray-300/40 text-zinc-500 rounded-full px-2 py-0.5">
                          {todoTaken.length}
                        </span>
                      )}
                    </div>
                    {todoTaken.length === 0 ? (
                      <p className="text-xs text-zinc-400 flex-1 items-center  flex justify-center px-1">
                        {filter
                          ? 'Geen resultaten.'
                          : 'Geen openstaande taken.'}
                      </p>
                    ) : (
                      todoTaken.map((t) => (
                        <TaakRegel
                          key={t.id}
                          taak={t}
                          onAfgewerkt={onAfgewerkt}
                        />
                      ))
                    )}
                  </div>

                  {/* Afgewerkt */}
                  {doneTaken.length > 0 && (
                    <div className="flex flex-col gap-2 w-full h-full">
                      <div className="flex items-center justify-between px-1">
                        <span className="text-[10px] font-bold text-zinc-400 uppercase tracking-widest">
                          Afgewerkt
                        </span>
                        <span className="text-[10px] font-bold bg-emerald-50 text-emerald-600 rounded-full px-2 py-0.5">
                          {doneTaken.length}
                        </span>
                      </div>
                      {doneTaken.map((t) => (
                        <TaakRegel key={t.id} taak={t} />
                      ))}
                    </div>
                  )}

                  {taken.length === 0 && (
                    <p className="text-xs text-zinc-400 italic px-1">
                      Geen taken gepland voor deze dag.
                    </p>
                  )}
                </div>
              </div>
            </Container>
          )}
        </div>
      </div>
    </div>
  );
}
