'use client';
import { TabSwitcher } from '@/components/design-system/TabSwitcher/TabSwitcher';
import type { Afwezigheid, PlannerTaak } from './types';
import { useState } from 'react';

interface DayViewProps {
  huidigeDatum: Date;
  afwezigheden: Afwezigheid[];
  taken: PlannerTaak[];
}

export default function DayView(props: DayViewProps) {
  const { huidigeDatum, afwezigheden, taken } = props;

  return (
    <div className={'w-full h-full'}>
      <div className={'w-full flex justify-end'}></div>
    </div>
  );
}
