'use client';
import { TabSwitcher } from '@/components/design system/TabSwitcher/TabSwitcher';
import { useState } from 'react';

type Mode = 'check' | 'creëer';
type Team = 'teams' | 'users';

const modes: { key: Mode; label: string }[] = [
  { key: 'check', label: 'Check' },
  { key: 'creëer', label: 'Creëer' },
];

const tabs: { key: Team; label: string }[] = [
  { key: 'teams', label: 'Teams' },
  { key: 'users', label: 'Werknemers' },
];

const TeamClient = () => {
  const [mode, setMode] = useState<Mode>('check');
  const [team, setTeam] = useState<Team>('teams');
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

TeamClient.displayName = 'TeamClient';
export default TeamClient;
