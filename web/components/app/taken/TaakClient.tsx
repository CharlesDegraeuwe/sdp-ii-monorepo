'use client';
import { TabSwitcher } from '@/components/design-system/TabSwitcher/TabSwitcher';
import { useState, useEffect, useMemo } from 'react';
import { useSearchParams } from 'next/navigation';
import { TakenOverview } from '@/components/app/taken/CheckView/TakenOverview';
import { FinishedOverview } from '@/components/app/taken/CheckView/FinishedOverview';
import { AssignView } from '@/components/app/taken/AssignView/AssignView';
import { CreateView } from '@/components/app/taken/CreateView/CreateView';
import { useTaakData } from '@/hooks/useTaakData';
import { useTaakStore } from '@/stores/taakStore';
import { useUser } from '@/providers/UserProvider';

type Mode = 'check' | 'creëer' | 'assign';
type Scope = 'taken' | 'afgewerkt';

const scopes: { key: Scope; label: string }[] = [
  { key: 'taken', label: 'Taken' },
  { key: 'afgewerkt', label: 'Afgewerkt' },
];

const TaakClient = () => {
  const [mode, setMode] = useState<Mode>('check');
  const [scope, setScope] = useState<Scope>('taken');
  const { loaded } = useTaakData();
  const searchParams = useSearchParams();
  const selectTask = useTaakStore((s) => s.selectTask);
  const { isModerator, isSupervisor } = useUser();

  const canManage = isModerator || isSupervisor;

  const modes = useMemo(() => {
    const base: { key: Mode; label: string }[] = [
      { key: 'check', label: 'Bekijken' },
    ];
    if (canManage) {
      base.push({ key: 'creëer', label: 'Aanmaken' });
      base.push({ key: 'assign', label: 'Toekennen' });
    }
    return base;
  }, [canManage]);

  useEffect(() => {
    if (!loaded) return;
    const taakId = searchParams.get('taakId');
    if (taakId) selectTask(taakId);
  }, [loaded, searchParams, selectTask]);

  if (!loaded) {
    return <div className={'p-6 text-zinc-500'}>Laden...</div>;
  }

  return (
    <div className={'w-full lg:w-3/4 xl:w-1/2 flex flex-col gap-3'}>
      <div
        className={
          'w-full h-fit flex flex-col sm:flex-row sm:justify-between gap-2'
        }
      >
        <TabSwitcher
          tabs={modes}
          value={mode}
          onChange={(key) => setMode(key as Mode)}
        />
        {mode === 'check' && (
          <TabSwitcher
            tabs={scopes}
            value={scope}
            onChange={(key) => setScope(key as Scope)}
          />
        )}
      </div>
      {mode === 'check' && scope === 'taken' && <TakenOverview />}
      {mode === 'check' && scope === 'afgewerkt' && <FinishedOverview />}
      {mode === 'assign' && <AssignView />}
      {mode === 'creëer' && <CreateView />}
    </div>
  );
};

TaakClient.displayName = 'TaakClient';
export default TaakClient;
