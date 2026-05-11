'use client';

import { MdEdit } from 'react-icons/md';
import type { Shift } from '../types';
import {
  VIS_START,
  VIS_END,
  UUR_BREEDTE,
  TIMELINE_W,
  tijdDecimaal,
} from './helpers';

export function ShiftBlok({
  shift,
  isDefault,
  isAfwezig,
  isManager,
  onEdit,
}: {
  shift: Shift;
  isDefault: boolean;
  isAfwezig: boolean;
  isManager: boolean;
  onEdit: () => void;
}) {
  if (!shift.startTijd || !shift.eindTijd) return null;

  const shiftStart = tijdDecimaal(shift.startTijd);
  const shiftEnd = tijdDecimaal(shift.eindTijd);
  const hasPauze = !!(shift.pauzeStart && shift.pauzeEind);
  const pauzeStart = hasPauze ? tijdDecimaal(shift.pauzeStart!) : null;
  const pauzeEind = hasPauze ? tijdDecimaal(shift.pauzeEind!) : null;

  function segment(from: number, to: number) {
    const l = Math.max(from, VIS_START);
    const r = Math.min(to, VIS_END);
    if (r <= l) return null;
    return {
      left: (l - VIS_START) * UUR_BREEDTE,
      width: (r - l) * UUR_BREEDTE,
    };
  }

  const blockBase = isAfwezig
    ? 'bg-zinc-300/60'
    : isDefault
      ? 'bg-red-50 border-2 border-dashed border-red-300'
      : 'bg-red-500/80';
  const blockHover = isAfwezig
    ? ''
    : isDefault
      ? 'hover:bg-red-100'
      : 'hover:bg-red-600/80';
  const textColor = isDefault || isAfwezig ? 'text-red-300' : 'text-white';
  const editColor = isDefault || isAfwezig ? 'text-red-300' : 'text-white';

  if (!hasPauze || pauzeStart === null || pauzeEind === null) {
    const seg = segment(shiftStart, shiftEnd);
    if (!seg) return null;
    return (
      <div
        className={`absolute top-2 bottom-2 rounded-xl flex items-center px-2.5 transition-all duration-150 group ${blockBase} ${blockHover} ${isManager ? 'cursor-pointer' : ''}`}
        style={seg}
        onClick={isManager ? onEdit : undefined}
      >
        <span className={`text-[10px] font-bold truncate flex-1 ${textColor}`}>
          {shift.startTijd.substring(0, 5)} – {shift.eindTijd.substring(0, 5)}
        </span>
        {isManager && (
          <MdEdit
            size={10}
            className={`${editColor} shrink-0 opacity-0 group-hover:opacity-80 transition-opacity`}
          />
        )}
      </div>
    );
  }

  const morning = segment(shiftStart, pauzeStart);
  const pauze = segment(pauzeStart, pauzeEind);
  const afternoon = segment(pauzeEind, shiftEnd);

  const breakClass = isDefault
    ? 'bg-white border-2 border-dashed border-red-200'
    : 'bg-white border-2 border-red-500';
  const breakTextColor = isDefault ? 'text-red-200' : 'text-red-500';

  return (
    <div
      className={`absolute top-0 bottom-0 ${isManager ? 'cursor-pointer' : ''}`}
      style={{ left: 0, width: TIMELINE_W }}
      onClick={isManager ? onEdit : undefined}
    >
      {morning && (
        <div
          className={`absolute top-2 bottom-2 rounded-l-xl flex items-center px-2.5 transition-all duration-150 ${blockBase} ${blockHover}`}
          style={morning}
        >
          <span className={`text-[10px] font-bold truncate ${textColor}`}>
            {shift.startTijd.substring(0, 5)}
          </span>
        </div>
      )}
      {pauze && (
        <div
          className={`absolute top-2 bottom-2 flex items-center justify-center ${breakClass}`}
          style={pauze}
        >
          <span
            className={`text-[9px] font-bold truncate px-1 ${breakTextColor}`}
          >
            pauze
          </span>
        </div>
      )}
      {afternoon && (
        <div
          className={`absolute top-2 bottom-2 rounded-r-xl flex items-center justify-end px-2.5 transition-all duration-150 group ${blockBase} ${blockHover}`}
          style={afternoon}
        >
          <span className={`text-[10px] font-bold truncate ${textColor}`}>
            {shift.eindTijd.substring(0, 5)}
          </span>
          {isManager && (
            <MdEdit
              size={10}
              className={`ml-1 ${editColor} shrink-0 opacity-0 group-hover:opacity-80 transition-opacity`}
            />
          )}
        </div>
      )}
    </div>
  );
}
