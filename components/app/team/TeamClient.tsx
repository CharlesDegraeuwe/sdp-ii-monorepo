'use client';
import { TabSwitcher } from '@/components/design system/TabSwitcher/TabSwitcher';
import { useState } from 'react';
import TeamsOverview from './TeamCheckView/TeamsOverview';
import CreateTeamForm from './TeamCreateView/CreateTeamForm';
import { useTeamsData } from '@/hooks/useTeamData';
import UsersOverview from '@/components/app/team/TeamCheckView/UserOverview';

type Mode = 'check' | 'creëer';
type Scope = 'teams' | 'users';

const modes: { key: Mode; label: string }[] = [
  { key: 'check', label: 'Check' },
  { key: 'creëer', label: 'Creëer' },
];

const scopes: { key: Scope; label: string }[] = [
  { key: 'teams', label: 'Teams' },
  { key: 'users', label: 'Werknemers' },
];

const TeamsClient = () => {
  const [mode, setMode] = useState<Mode>('check');
  const [scope, setScope] = useState<Scope>('teams');
  const { loaded } = useTeamsData();

  if (!loaded) {
    return <div className={'p-6 text-zinc-500'}>Laden...</div>;
  }

  return (
    <div className={'w-1/2 h-full flex items-center flex-col gap-3'}>
      <div className={'w-full h-fit justify-between flex flex-row'}>
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

      {mode === 'check' && scope === 'teams' && <TeamsOverview />}
      {mode === 'check' && scope === 'users' && <UsersOverview />}
      {mode === 'creëer' && <CreateTeamForm scope={scope} />}
    </div>
  );
};

TeamsClient.displayName = 'TeamsClient';
export default TeamsClient;
