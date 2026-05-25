'use client';
import { TabSwitcher } from '@/components/design-system/TabSwitcher/TabSwitcher';
import { useState } from 'react';
import TeamsOverview from './TeamCheckView/components/TeamsOverview';
import CreateTeamForm from './TeamCreateView/CreateTeamForm';
import { useTeamsData } from '@/hooks/useTeamData';
import UsersOverview from '@/components/app/team/UserCheckView/UserOverview';

type Mode = 'check' | 'creëer';
type Scope = 'teams' | 'users';

const modes: { key: Mode; label: string }[] = [
  { key: 'check', label: 'Zoeken' },
  { key: 'creëer', label: 'Aanmaken' },
];

const scopes: { key: Scope; label: string }[] = [
  { key: 'teams', label: 'Teams' },
  { key: 'users', label: 'Werknemers' },
];

type Props = {
  selectedTeamId?: number | null;
  selectedWerknemerId?: number | null;
  defaultScope?: Scope;
};

const TeamsClient = ({
  selectedTeamId,
  selectedWerknemerId,
  defaultScope,
}: Props) => {
  const [mode, setMode] = useState<Mode>('check');
  const [scope, setScope] = useState<Scope>(defaultScope ?? 'teams');
  const { loaded } = useTeamsData();

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

      {mode === 'check' && scope === 'teams' && (
        <TeamsOverview
          selectedTeamId={selectedTeamId}
          selectedWerknemerId={selectedWerknemerId}
        />
      )}
      {mode === 'check' && scope === 'users' && (
        <UsersOverview selectedWerknemerId={selectedWerknemerId} />
      )}
      {mode === 'creëer' && <CreateTeamForm />}
    </div>
  );
};

TeamsClient.displayName = 'TeamsClient';
export default TeamsClient;
