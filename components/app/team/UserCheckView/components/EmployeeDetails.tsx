'use client';
import { useState } from 'react';
import { Label } from '@/components/design-system/Label';
import { useTeamsStore } from '@/stores/teamStore';
import { TabSwitcher } from '@/components/design-system/TabSwitcher/TabSwitcher';
import { EmployeeHeader } from './EmployeeHeader';
import { EmployeeDetailsCard } from './EmployeeDetailsCard';
import { EmployeeTeamInfo } from './EmployeeTeamInfo';
import { EmployeeActions } from './EmployeeActions';

type Tab = 'team' | 'taken';

const tabs: { key: Tab; label: string }[] = [
  { key: 'team', label: 'Team' },
  { key: 'taken', label: 'Taken' },
];

type Props = {
  werknemerId: number | null;
};

export const EmployeeDetails = ({ werknemerId }: Props) => {
  const employee = useTeamsStore((s) =>
    werknemerId ? s.werknemers[werknemerId] : null,
  );
  const [tab, setTab] = useState<Tab>('team');

  if (!employee) {
    return (
      <div className="w-full h-full flex items-center justify-center pb-12 px-4">
        <Label text="Selecteer een gebruiker" variant="emptystate" />
      </div>
    );
  }

  return (
    <div className="flex flex-col gap-5 h-full p-3">
      <div className="flex flex-row justify-between items-start gap-4">
        <div className="flex flex-col min-w-0 w-full">
          <EmployeeHeader employee={employee} />
          <EmployeeDetailsCard employee={employee} />
        </div>
      </div>

      <hr className="opacity-10" />

      <div className="flex flex-col gap-3 text-sm">
        <div className="w-full flex flex-row items-center justify-end">
          <TabSwitcher
            size="sm"
            tabs={tabs}
            value={tab}
            onChange={(key) => setTab(key as Tab)}
          />
        </div>

        {tab === 'team' && <EmployeeTeamInfo employee={employee} />}
        {tab === 'taken' && (
          <Label text="Taken komen hier" variant="emptystate" />
        )}
      </div>

      <EmployeeActions employee={employee} />
    </div>
  );
};
