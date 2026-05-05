'use client';
import { TabSwitcher } from '@/components/design system/TabSwitcher/TabSwitcher';
import type { Afwezigheid, PlannerTaak } from './types';
import { useState } from 'react';

interface DayViewProps {
  huidigeDatum: Date;
  afwezigheden: Afwezigheid[];
  taken: PlannerTaak[];
}

type Tab = 'team' | 'you';
const tabs: { key: Team; label: string }[] = [
  { key: 'you', label: 'Jouw planning' },
  { key: 'team', label: 'Teamplanning' },
];

export default function DayView(props: DayViewProps) {
  const { huidigeDatum, afwezigheden, taken } = props;
  const [tab, setTab] = useState<tabs>('you');
  return (
    <div className={'w-full h-full'}>
      <div className={'w-full flex justify-end'}>
        <TabSwitcher
          tabs={tabs}
          value={tab}
          onChange={(key) => setTab(key as Tab)}
        />
      </div>
    </div>
  );
}
