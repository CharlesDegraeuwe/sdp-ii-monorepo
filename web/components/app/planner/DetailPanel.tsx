'use client';

import {
  MdCheckCircle,
  MdDelete,
  MdRadioButtonUnchecked,
  MdPerson,
} from 'react-icons/md';
import type { Afwezigheid, PlannerTaak, Shift } from './types';
import type { WerknemerOptie } from '@/hooks/usePlanningFilters';
import {
  afwezighedenOpDag,
  datumNaarString,
  formatDag,
  badgeKleur,
  afwezigheidLabel,
} from './utils';
import { Container } from '@/components/design-system/Container';

interface DetailPanelProps {
  geselecteerdeDag: Date;
  afwezigheden: Afwezigheid[];
  eigenShiften: Shift[];
  taken: PlannerTaak[];
  isManager?: boolean;
  onAfgewerkt?: (id: number) => void;
  onVerwijder?: (id: number) => void;
  tab?: 'you' | 'team';
  teamWerknemers?: WerknemerOptie[];
  teamShiften?: Shift[];
  teamTaken?: Record<number, PlannerTaak[]>;
  geselecteerdeTeamWerknemer?: number | null;
  onTeamTaakAfgewerkt?: (taakId: number, werknemerId: number) => void;
  onTeamTaakVerwijder?: (taakId: number, werknemerId: number) => void;
}

function getShiftVoorDag(
  datum: Date,
  shifts: Shift[],
  werknemerId?: number,
): Shift | undefined {
  const ds = datumNaarString(datum);
  return shifts.find((s) => {
    const matchWerknemer = werknemerId == null || s.werknemerId === werknemerId;
    return matchWerknemer && s.startDatum <= ds && s.eindDatum >= ds;
  });
}

function SectieHeader({
  label,
  count,
  variant = 'default',
}: {
  label: string;
  count?: number;
  variant?: 'default' | 'done';
}) {
  return (
    <div className="flex items-center justify-between">
      <span
        className={`text-xs font-semibold uppercase tracking-wide ${
          variant === 'done' ? 'text-zinc-400' : 'text-zinc-500'
        }`}
      >
        {label}
      </span>
      {count != null && count > 0 && (
        <span
          className={`text-xs font-semibold rounded-full px-2 py-0.5 ${
            variant === 'done'
              ? 'bg-emerald-50 text-emerald-600'
              : 'bg-zinc-100 text-zinc-500'
          }`}
        >
          {count}
        </span>
      )}
    </div>
  );
}

function ShiftBadge({
  startTijd,
  eindTijd,
}: {
  startTijd: string;
  eindTijd: string;
}) {
  return (
    <div className="flex items-center gap-3 px-3 py-2.5 rounded-2xl bg-gray-300/30 border border-gray-300/30">
      <div className="flex flex-col gap-0.5 flex-1">
        <span className="text-[10px] font-semibold text-zinc-400 uppercase tracking-wide">
          Shift
        </span>
        <span className="text-sm font-bold text-zinc-800">
          {startTijd} – {eindTijd}
        </span>
      </div>
    </div>
  );
}

function AfwezigheidBadge({ afwezigheid }: { afwezigheid: Afwezigheid }) {
  const bg =
    afwezigheid.type === 'Ziekte'
      ? 'bg-red-50 border-red-100'
      : afwezigheid.status === 'In afwachting'
        ? 'bg-amber-50 border-amber-100'
        : 'bg-emerald-50 border-emerald-100';

  return (
    <div
      className={`flex items-center gap-2 px-3 py-2 rounded-2xl border ${bg}`}
    >
      <span
        className={`text-xs font-semibold px-2 py-0.5 rounded-full shrink-0 ${badgeKleur(afwezigheid)}`}
      >
        {afwezigheidLabel(afwezigheid)}
      </span>
      <span className="text-xs text-zinc-500">Niet aanwezig</span>
    </div>
  );
}

function TaakRij({
  taak,
  isManager,
  onAfgewerkt,
  onVerwijder,
}: {
  taak: PlannerTaak;
  isManager?: boolean;
  onAfgewerkt?: () => void;
  onVerwijder?: () => void;
}) {
  return (
    <div
      className={`flex items-center gap-2 px-3 py-2.5 rounded-2xl border ${
        taak.afgewerkt
          ? 'bg-zinc-50/60 border-zinc-100'
          : taak.belangrijk
            ? 'bg-rose-50/60 border-rose-100'
            : 'bg-white/60 border-gray-200/50'
      }`}
    >
      <button
        onClick={() => !taak.afgewerkt && onAfgewerkt?.()}
        disabled={taak.afgewerkt}
        className={`shrink-0 transition-colors ${
          taak.afgewerkt
            ? 'text-emerald-500 cursor-default'
            : 'text-zinc-300 hover:text-emerald-600 cursor-pointer'
        }`}
      >
        {taak.afgewerkt ? (
          <MdCheckCircle size={15} />
        ) : (
          <MdRadioButtonUnchecked size={15} />
        )}
      </button>
      <span
        className={`text-xs font-semibold flex-1 truncate ${
          taak.afgewerkt ? 'line-through text-zinc-400' : 'text-zinc-800'
        }`}
      >
        {taak.naam}
      </span>
      {isManager && onVerwijder && (
        <button
          onClick={onVerwijder}
          className="text-zinc-200 hover:text-red-500 transition-colors shrink-0"
        >
          <MdDelete size={13} />
        </button>
      )}
    </div>
  );
}

function TakenSectie({
  todoTaken,
  doneTaken,
  isManager,
  onAfgewerkt,
  onVerwijder,
}: {
  todoTaken: PlannerTaak[];
  doneTaken: PlannerTaak[];
  isManager?: boolean;
  onAfgewerkt?: (id: number) => void;
  onVerwijder?: (id: number) => void;
}) {
  const leeg = todoTaken.length === 0 && doneTaken.length === 0;

  return (
    <div className="flex flex-col flex-1 min-h-0 gap-4">
      <div className="flex flex-col gap-2 flex-1 min-h-0 overflow-y-auto">
        <SectieHeader label="Te doen" count={todoTaken.length} />
        {leeg && (
          <p className="text-xs w-full h-full flex items-center justify-center text-zinc-400">
            Niets gepland
          </p>
        )}
        {!leeg && todoTaken.length === 0 && (
          <p className="text-xs w-full h-full flex items-center justify-center text-zinc-400">
            Alles afgewerkt
          </p>
        )}
        {todoTaken.map((t) => (
          <TaakRij
            key={t.id}
            taak={t}
            isManager={isManager}
            onAfgewerkt={() => onAfgewerkt?.(t.id)}
            onVerwijder={() => onVerwijder?.(t.id)}
          />
        ))}
      </div>

      {doneTaken.length > 0 && (
        <div className="flex flex-col gap-2 flex-1 min-h-0 overflow-y-auto">
          <SectieHeader
            label="Afgewerkt"
            count={doneTaken.length}
            variant="done"
          />
          {doneTaken.map((t) => (
            <TaakRij
              key={t.id}
              taak={t}
              isManager={isManager}
              onVerwijder={() => onVerwijder?.(t.id)}
            />
          ))}
        </div>
      )}
    </div>
  );
}

export default function DetailPanel({
  geselecteerdeDag,
  afwezigheden,
  eigenShiften,
  taken,
  isManager,
  onAfgewerkt,
  onVerwijder,
  tab = 'you',
  teamWerknemers = [],
  teamShiften = [],
  teamTaken = {},
  geselecteerdeTeamWerknemer = null,
  onTeamTaakAfgewerkt,
  onTeamTaakVerwijder,
}: DetailPanelProps) {
  const weekend =
    geselecteerdeDag.getDay() === 0 || geselecteerdeDag.getDay() === 6;

  // TEAM MODE — enkel lid geselecteerd
  if (
    tab === 'team' &&
    teamWerknemers.length > 0 &&
    geselecteerdeTeamWerknemer !== null
  ) {
    const w = teamWerknemers.find((w) => w.id === geselecteerdeTeamWerknemer);
    if (!w) return null;

    const wAfwezig = afwezighedenOpDag(
      afwezigheden.filter((a) => a.werknemerId === w.id),
      geselecteerdeDag,
    );
    const isAfwezig = wAfwezig.length > 0;
    const wShift = getShiftVoorDag(geselecteerdeDag, teamShiften, w.id);
    const startTijd = wShift?.startTijd?.substring(0, 5) ?? '09:00';
    const eindTijd = wShift?.eindTijd?.substring(0, 5) ?? '17:00';
    const alleTaken = teamTaken[w.id] ?? [];
    const todoTaken = alleTaken.filter((t) => !t.afgewerkt);
    const doneTaken = alleTaken.filter((t) => t.afgewerkt);

    return (
      <div className="w-full lg:w-90 h-full">
        <Container>
          <div className="flex flex-col gap-4 h-full">
            <div className="flex flex-col gap-1.5 shrink-0">
              <div className="flex items-center gap-1.5">
                <MdPerson size={14} className="text-zinc-400 shrink-0" />
                <span className="text-sm font-bold text-zinc-900 truncate">
                  {w.voornaam} {w.naam}
                </span>
              </div>
              <span className="text-xs text-zinc-400">
                {formatDag(geselecteerdeDag)}
              </span>

              {!weekend &&
                (isAfwezig ? (
                  <AfwezigheidBadge afwezigheid={wAfwezig[0]} />
                ) : wShift ? (
                  <ShiftBadge startTijd={startTijd} eindTijd={eindTijd} />
                ) : null)}
            </div>

            {isAfwezig ? (
              <p className="text-xs text-zinc-400 italic">
                Geen taken op afwezige dag.
              </p>
            ) : (
              <TakenSectie
                todoTaken={todoTaken}
                doneTaken={doneTaken}
                isManager={isManager}
                onAfgewerkt={(id) => onTeamTaakAfgewerkt?.(id, w.id)}
                onVerwijder={(id) => onTeamTaakVerwijder?.(id, w.id)}
              />
            )}
          </div>
        </Container>
      </div>
    );
  }

  // TEAM MODE — alle teamleden
  if (tab === 'team' && teamWerknemers.length > 0) {
    return (
      <div className="w-full lg:w-90 max-h-full overflow-y-auto scroll-hidden">
        <Container>
          <div className="flex flex-col gap-4">
            <div className="flex flex-col gap-0.5">
              <span className="text-sm font-bold text-zinc-900">
                {formatDag(geselecteerdeDag)}
              </span>
              <span className="text-xs text-zinc-400">Alle teamleden</span>
            </div>

            <div className="flex flex-col gap-4">
              {teamWerknemers.map((w) => {
                const wAfwezig = afwezighedenOpDag(
                  afwezigheden.filter((a) => a.werknemerId === w.id),
                  geselecteerdeDag,
                );
                const isAfwezig = wAfwezig.length > 0;
                const dagTaken = teamTaken[w.id] ?? [];

                return (
                  <div
                    key={w.id}
                    className="flex flex-col gap-2 pb-4 border-b border-zinc-100 last:border-b-0 last:pb-0"
                  >
                    <div className="flex items-center gap-1.5">
                      <MdPerson size={12} className="text-zinc-400 shrink-0" />
                      <span className="text-xs font-bold text-zinc-700 flex-1 truncate">
                        {w.voornaam} {w.naam}
                      </span>
                      {isAfwezig && (
                        <span
                          className={`text-xs font-semibold px-2 py-0.5 rounded-full ${badgeKleur(wAfwezig[0])}`}
                        >
                          {afwezigheidLabel(wAfwezig[0])}
                        </span>
                      )}
                    </div>

                    {!isAfwezig &&
                      !weekend &&
                      (dagTaken.length === 0 ? (
                        <p className="text-xs text-zinc-400 italic pl-4">
                          Geen taken
                        </p>
                      ) : (
                        dagTaken.map((t) => (
                          <TaakRij
                            key={t.id}
                            taak={t}
                            isManager={isManager}
                            onAfgewerkt={() =>
                              onTeamTaakAfgewerkt?.(t.id, w.id)
                            }
                            onVerwijder={() =>
                              onTeamTaakVerwijder?.(t.id, w.id)
                            }
                          />
                        ))
                      ))}
                  </div>
                );
              })}
            </div>
          </div>
        </Container>
      </div>
    );
  }

  // PERSONAL MODE
  const opDag = afwezighedenOpDag(afwezigheden, geselecteerdeDag);
  const isAfwezig = opDag.length > 0;
  const todoTaken = taken.filter((t) => !t.afgewerkt);
  const doneTaken = taken.filter((t) => t.afgewerkt);
  const shift = getShiftVoorDag(geselecteerdeDag, eigenShiften);
  const startTijd = shift?.startTijd?.substring(0, 5) ?? '09:00';
  const eindTijd = shift?.eindTijd?.substring(0, 5) ?? '17:00';

  return (
    <div className="w-full lg:w-90 h-full">
      <Container>
        <div className="flex flex-col gap-4 h-full">
          <div className="flex flex-col gap-1.5 shrink-0">
            <span className="text-sm font-bold text-zinc-900">
              {formatDag(geselecteerdeDag)}
            </span>
            {!weekend &&
              (isAfwezig ? (
                <AfwezigheidBadge afwezigheid={opDag[0]} />
              ) : shift ? (
                <ShiftBadge startTijd={startTijd} eindTijd={eindTijd} />
              ) : null)}
          </div>

          {isAfwezig ? (
            <p className="text-xs text-zinc-400 italic">
              Geen taken op afwezige dag.
            </p>
          ) : (
            <TakenSectie
              todoTaken={todoTaken}
              doneTaken={doneTaken}
              isManager={isManager}
              onAfgewerkt={onAfgewerkt}
              onVerwijder={onVerwijder}
            />
          )}
        </div>
      </Container>
    </div>
  );
}
