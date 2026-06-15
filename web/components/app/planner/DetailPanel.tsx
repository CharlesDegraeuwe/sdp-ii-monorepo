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
  formatDag,
  badgeKleur,
  afwezigheidLabel,
  takenOpDag,
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
  // Team props
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
  const ds = datum.toISOString().split('T')[0];
  return shifts.find((s) => {
    const matchWerknemer = werknemerId == null || s.werknemerId === werknemerId;
    return matchWerknemer && s.startDatum <= ds && s.eindDatum >= ds;
  });
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
      className={`flex items-center gap-2 px-3 py-2 rounded-xl border ${
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
        className={`text-xs font-semibold flex-1 truncate ${taak.afgewerkt ? 'line-through text-zinc-400' : 'text-zinc-800'}`}
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

  // TEAM MODE
  if (tab === 'team' && teamWerknemers.length > 0) {
    // Gefilterd op één lid
    if (geselecteerdeTeamWerknemer !== null) {
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
      const dagTaken = takenOpDag(alleTaken, geselecteerdeDag);
      const todoTaken = dagTaken.filter((t) => !t.afgewerkt);
      const doneTaken = dagTaken.filter((t) => t.afgewerkt);

      return (
        <div className="w-full lg:w-90 max-h-full">
          <Container>
            <div className="flex flex-col gap-1">
              <div className="flex items-center gap-2">
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
                  <div
                    className={`flex items-center gap-2 px-2.5 py-1.5 rounded-xl border ${wAfwezig[0].type === 'Ziekte' ? 'bg-red-50 border-red-100' : wAfwezig[0].status === 'In afwachting' ? 'bg-amber-50 border-amber-100' : 'bg-emerald-50 border-emerald-100'}`}
                  >
                    <span
                      className={`text-[10px] font-bold px-1.5 py-0.5 rounded-full ${badgeKleur(wAfwezig[0])}`}
                    >
                      {afwezigheidLabel(wAfwezig[0])}
                    </span>
                    <span className="text-xs text-zinc-500">Niet aanwezig</span>
                  </div>
                ) : (
                  <div className="flex items-center gap-2 px-2.5 py-1.5 rounded-xl border border-rose-100 bg-rose-50/50">
                    <span className="text-[10px] font-bold text-rose-600">
                      {startTijd} – {eindTijd}
                    </span>
                    <span className="text-[10px] text-zinc-400 ml-auto">
                      Shift
                    </span>
                  </div>
                ))}
            </div>

            {isAfwezig ? (
              <p className="text-xs text-zinc-400 italic">
                Geen taken op afwezige dag.
              </p>
            ) : (
              <>
                <div className="flex flex-col gap-1.5">
                  <div className="flex items-center justify-between">
                    <span className="text-[10px] font-bold text-zinc-500 uppercase tracking-wide">
                      Te doen
                    </span>
                    {todoTaken.length > 0 && (
                      <span className="text-[9px] font-bold bg-zinc-100 text-zinc-500 rounded-full px-1.5 py-0.5">
                        {todoTaken.length}
                      </span>
                    )}
                  </div>
                  {todoTaken.length === 0 && doneTaken.length === 0 && (
                    <p className="text-xs text-zinc-400 italic">
                      Niets gepland.
                    </p>
                  )}
                  {todoTaken.length === 0 && doneTaken.length > 0 && (
                    <p className="text-xs text-zinc-400 italic">
                      Alles afgewerkt.
                    </p>
                  )}
                  {todoTaken.map((t) => (
                    <TaakRij
                      key={t.id}
                      taak={t}
                      isManager={isManager}
                      onAfgewerkt={() => onTeamTaakAfgewerkt?.(t.id, w.id)}
                      onVerwijder={() => onTeamTaakVerwijder?.(t.id, w.id)}
                    />
                  ))}
                </div>
                {doneTaken.length > 0 && (
                  <div className="flex flex-col gap-1.5">
                    <div className="flex items-center justify-between">
                      <span className="text-[10px] font-bold text-zinc-400 uppercase tracking-wide">
                        Afgewerkt
                      </span>
                      <span className="text-[9px] font-bold bg-emerald-50 text-emerald-600 rounded-full px-1.5 py-0.5">
                        {doneTaken.length}
                      </span>
                    </div>
                    {doneTaken.map((t) => (
                      <TaakRij
                        key={t.id}
                        taak={t}
                        isManager={isManager}
                        onVerwijder={() => onTeamTaakVerwijder?.(t.id, w.id)}
                      />
                    ))}
                  </div>
                )}
              </>
            )}
          </Container>
        </div>
      );
    }

    // Hele dag geselecteerd → alle teamleden overzicht
    return (
      <div className="w-full lg:w-90 max-h-full overflow-y-auto scroll-hidden">
        <Container>
          <span className="text-sm font-bold text-zinc-900">
            {formatDag(geselecteerdeDag)}
          </span>
          <span className="text-xs text-zinc-400">Alle teamleden</span>

          {teamWerknemers.map((w) => {
            const wAfwezig = afwezighedenOpDag(
              afwezigheden.filter((a) => a.werknemerId === w.id),
              geselecteerdeDag,
            );
            const isAfwezig = wAfwezig.length > 0;
            const alleTaken = teamTaken[w.id] ?? [];
            const dagTaken = takenOpDag(alleTaken, geselecteerdeDag);
            const todoTaken = dagTaken.filter((t) => !t.afgewerkt);
            const doneTaken = dagTaken.filter((t) => t.afgewerkt);

            return (
              <div
                key={w.id}
                className="flex flex-col gap-1.5 border-b border-zinc-100 pb-3 last:border-b-0 last:pb-0"
              >
                <div className="flex items-center gap-2">
                  <MdPerson size={12} className="text-zinc-400 shrink-0" />
                  <span className="text-xs font-bold text-zinc-700">
                    {w.voornaam} {w.naam}
                  </span>
                  {isAfwezig && (
                    <span
                      className={`text-[9px] font-bold px-1.5 py-0.5 rounded-full ml-auto ${badgeKleur(wAfwezig[0])}`}
                    >
                      {afwezigheidLabel(wAfwezig[0])}
                    </span>
                  )}
                  {!isAfwezig && !weekend && (
                    <span className="text-[9px] text-zinc-400 ml-auto">
                      {todoTaken.length > 0
                        ? `${todoTaken.length} open`
                        : doneTaken.length > 0
                          ? 'Alles klaar'
                          : 'Geen taken'}
                    </span>
                  )}
                </div>

                {!isAfwezig &&
                  !weekend &&
                  dagTaken.map((t) => (
                    <TaakRij
                      key={t.id}
                      taak={t}
                      isManager={isManager}
                      onAfgewerkt={() => onTeamTaakAfgewerkt?.(t.id, w.id)}
                      onVerwijder={() => onTeamTaakVerwijder?.(t.id, w.id)}
                    />
                  ))}
                {!isAfwezig && !weekend && dagTaken.length === 0 && (
                  <p className="text-[10px] text-zinc-400 italic pl-4">
                    Geen taken
                  </p>
                )}
              </div>
            );
          })}
        </Container>
      </div>
    );
  }

  // PERSONAL MODE (original)
  const opDag = afwezighedenOpDag(afwezigheden, geselecteerdeDag);
  const isAfwezig = opDag.length > 0;
  const dagTaken = takenOpDag(taken, geselecteerdeDag);
  const todoTaken = dagTaken.filter((t) => !t.afgewerkt);
  const doneTaken = dagTaken.filter((t) => t.afgewerkt);
  const shift = getShiftVoorDag(geselecteerdeDag, eigenShiften);
  const startTijd = shift?.startTijd?.substring(0, 5) ?? '09:00';
  const eindTijd = shift?.eindTijd?.substring(0, 5) ?? '17:00';

  return (
    <div className="w-full lg:w-90 max-h-full">
      <Container>
        <div className="flex flex-col gap-1">
          <span className="text-sm font-bold text-zinc-900">
            {formatDag(geselecteerdeDag)}
          </span>
          {!weekend &&
            (isAfwezig ? (
              <div
                className={`flex items-center gap-2 px-2.5 py-1.5 rounded-xl border ${opDag[0].type === 'Ziekte' ? 'bg-red-50 border-red-100' : opDag[0].status === 'In afwachting' ? 'bg-amber-50 border-amber-100' : 'bg-emerald-50 border-emerald-100'}`}
              >
                <span
                  className={`text-[10px] font-bold px-1.5 py-0.5 rounded-full shrink-0 ${badgeKleur(opDag[0])}`}
                >
                  {afwezigheidLabel(opDag[0])}
                </span>
                <span className="text-xs text-zinc-500">Niet aanwezig</span>
              </div>
            ) : (
              <div className="flex items-center gap-2 px-2.5 py-1.5 rounded-xl border border-rose-100 bg-rose-50/50">
                <span className="text-[10px] font-bold text-rose-600">
                  {startTijd} – {eindTijd}
                </span>
                <span className="text-[10px] text-zinc-400 ml-auto">Shift</span>
              </div>
            ))}
        </div>

        {isAfwezig && (
          <p className="text-xs text-zinc-400 italic">
            Geen taken op afwezige dag.
          </p>
        )}

        {!isAfwezig && (
          <div className="flex flex-col gap-1.5">
            <div className="flex items-center justify-between">
              <span className="text-[10px] font-bold text-zinc-500 uppercase tracking-wide">
                Te doen
              </span>
              {todoTaken.length > 0 && (
                <span className="text-[9px] font-bold bg-zinc-100 text-zinc-500 rounded-full px-1.5 py-0.5">
                  {todoTaken.length}
                </span>
              )}
            </div>
            {todoTaken.length === 0 && doneTaken.length === 0 && (
              <p className="text-xs text-zinc-400 italic">Niets gepland.</p>
            )}
            {todoTaken.length === 0 && doneTaken.length > 0 && (
              <p className="text-xs text-zinc-400 italic">
                Alle taken afgewerkt.
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
        )}

        {!isAfwezig && doneTaken.length > 0 && (
          <div className="flex flex-col gap-1.5">
            <div className="flex items-center justify-between">
              <span className="text-[10px] font-bold text-zinc-400 uppercase tracking-wide">
                Afgewerkt
              </span>
              <span className="text-[9px] font-bold bg-emerald-50 text-emerald-600 rounded-full px-1.5 py-0.5">
                {doneTaken.length}
              </span>
            </div>
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
      </Container>
    </div>
  );
}
