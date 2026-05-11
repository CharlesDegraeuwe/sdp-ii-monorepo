'use client';
import { TabSwitcher } from '@/components/design-system/TabSwitcher/TabSwitcher';
import { useState, useEffect } from 'react';
import { useSearchParams } from 'next/navigation';
import { CheckView } from '@/components/app/taken/CheckView/CheckView';
import { AssignView } from '@/components/app/taken/AssignView/AssignView';
import { CreateView } from '@/components/app/taken/CreateView/CreateView';
import { useTaakData } from '@/hooks/useTaakData';
import { useTaakStore } from '@/stores/taakStore';

type Mode = 'check' | 'creëer' | 'assign';
type Team = 'teams' | 'users';

const modes: { key: Mode; label: string }[] = [
  { key: 'check', label: 'Check' },
  { key: 'creëer', label: 'Creëer' },
  { key: 'assign', label: 'Toekennen' },
];

const tabs: { key: Team; label: string }[] = [
  { key: 'teams', label: 'Teams' },
  { key: 'users', label: 'Werknemers' },
];

const TaakClient = () => {
  const [mode, setMode] = useState<Mode>('check');
  const [team, setTeam] = useState<Team>('teams');
  const { loaded } = useTaakData();
  const searchParams = useSearchParams();
  const selectTask = useTaakStore((s) => s.selectTask);

  useEffect(() => {
    if (!loaded) return;
    const taakId = searchParams.get('taakId');
    if (taakId) selectTask(taakId);
  }, [loaded, searchParams, selectTask]);

  if (!loaded) {
    return <div className={'p-6 text-zinc-500'}>Laden...</div>;
  }

  return (
    <div
      className={
        'w-full md:w-3/4 lg:w-1/2 h-full flex items-center flex-col gap-3'
      }
    >
      <div className={'w-full h-fit justify-between flex flex-row'}>
        <TabSwitcher
          tabs={modes}
          value={mode}
          onChange={(key) => setMode(key as Mode)}
        />
        {mode === 'check' && (
          <TabSwitcher
            tabs={tabs}
            value={team}
            onChange={(key) => setTeam(key as Team)}
          />
        )}
      </div>
      {mode === 'check' && <CheckView scope={team} />}
      {mode === 'assign' && <AssignView />}
      {mode === 'creëer' && <CreateView />}
    </div>
  );
};

TaakClient.displayName = 'TaakClient';
export default TaakClient;
