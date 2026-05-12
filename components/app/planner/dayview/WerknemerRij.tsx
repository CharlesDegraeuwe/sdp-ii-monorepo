'use client';

import { badgeKleur, afwezigheidLabel } from '@/components/app/planner/utils';
import type { Afwezigheid, Shift } from '../types';
import {
  VIS_START,
  UUR_BREEDTE,
  ROW_H,
  NAAM_B,
  TIMELINE_W,
  STANDAARD_TIJDEN,
  HOUR_LABELS,
  type Rij,
} from './helpers';
import { ShiftBlok } from './ShiftBlok';

interface WerknemerRijProps {
  rij: Rij;
  shift?: Shift;
  afwezig: Afwezigheid[];
  isManager: boolean;
  dagIsVrij: boolean;
  huidigUurX: number | null;
  onEdit: () => void;
  onCreate: () => void;
}

export function WerknemerRij({
  rij,
  shift,
  afwezig,
  isManager,
  dagIsVrij,
  huidigUurX,
  onEdit,
  onCreate,
}: WerknemerRijProps) {
  return (
    <div
      className="flex border-b border-zinc-100 hover:bg-zinc-50/50 transition-colors"
      style={{ height: ROW_H }}
    >
      <div
        style={{ width: NAAM_B, minWidth: NAAM_B }}
        className="shrink-0 flex flex-col justify-center px-3 py-1 border-r border-zinc-100"
      >
        <span className="text-xs font-semibold text-zinc-800 truncate">
          {rij.label}
        </span>
        {afwezig.length > 0 && (
          <span
            className={`text-[9px] font-bold px-1.5 py-0.5 rounded-full w-fit mt-0.5 ${badgeKleur(afwezig[0])}`}
          >
            {afwezigheidLabel(afwezig[0])}
          </span>
        )}
      </div>

      <div
        className="relative"
        style={{ width: TIMELINE_W, minWidth: TIMELINE_W }}
      >
        {HOUR_LABELS.map((u) => (
          <div
            key={u}
            className="absolute top-0 bottom-0 border-l border-zinc-100"
            style={{ left: (u - VIS_START) * UUR_BREEDTE }}
          />
        ))}

        {shift ? (
          <ShiftBlok
            shift={shift}
            isDefault={false}
            isAfwezig={afwezig.length > 0}
            isManager={isManager}
            onEdit={onEdit}
          />
        ) : dagIsVrij ? (
          <button
            className={`absolute inset-0 flex items-center px-4 ${isManager ? 'group cursor-pointer' : 'cursor-default'}`}
            onClick={isManager ? onCreate : undefined}
            disabled={!isManager}
          >
            <span className="text-[10px] text-zinc-300 italic">Vrij</span>
            {isManager && (
              <span className="ml-auto text-[10px] text-zinc-300 opacity-0 group-hover:opacity-100 transition-opacity">
                + Shift
              </span>
            )}
          </button>
        ) : (
          <ShiftBlok
            shift={{
              id: -1,
              werknemerId: rij.werknemerId,
              werknemerNaam: rij.label,
              startDatum: '',
              eindDatum: '',
              ...STANDAARD_TIJDEN,
            }}
            isDefault={true}
            isAfwezig={afwezig.length > 0}
            isManager={isManager}
            onEdit={onCreate}
          />
        )}

        {huidigUurX !== null && huidigUurX >= 0 && huidigUurX <= TIMELINE_W && (
          <div
            className="absolute top-0 bottom-0 w-px bg-red-400 z-10 pointer-events-none"
            style={{ left: huidigUurX }}
          />
        )}
      </div>
    </div>
  );
}
