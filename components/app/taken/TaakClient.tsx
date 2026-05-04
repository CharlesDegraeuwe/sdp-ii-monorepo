'use client';
import { TabSwitcher } from '@/components/design system/TabSwitcher/TabSwitcher';
import { useState } from 'react';

type Mode = 'check' | 'creëer' | 'toewijzen';
type Team = 'jouw_taken' | 'team_taken';

const modes: { key: Mode; label: string }[] = [
  { key: 'check', label: 'Check' },
  { key: 'creëer', label: 'Creëer' },
  { key: 'toewijzen', label: 'Toewijzen' },
];

const tabs: { key: Team; label: string }[] = [
  { key: 'jouw_taken', label: 'Jouw taken' },
  { key: 'team_taken', label: "Team's taken" },
];

const TaakClient = () => {
  const [mode, setMode] = useState<Mode>('check');
  const [team, setTeam] = useState<Team>('jouw_taken');
  return (
    <div className={'w-1/2 h-full flex items-center flex-col gap-3'}>
      <div className={'w-full h-fit justify-between flex flex-row'}>
        <TabSwitcher
          tabs={modes}
          value={mode}
          onChange={(key) => setMode(key as Mode)}
        />
        <TabSwitcher
          tabs={tabs}
          value={team}
          onChange={(key) => setTeam(key as Team)}
        />
      </div>
    </div>
  );
};

TaakClient.displayName = 'TaakClient';
export default TaakClient;
