'use client';
import { useState } from 'react';
import { useRouter } from 'next/navigation';
import { Input } from '@/components/design-system/Input';
import { Label } from '@/components/design-system/Label';

import { TeamListItem } from './TeamListItem';
import { useFilteredTeams } from '@/hooks/useFilteredTeams';

type Props = {
  selectedTeamId?: number | null;
};

export const TeamList = ({ selectedTeamId = null }: Props) => {
  const [search, setSearch] = useState('');
  const filtered = useFilteredTeams(search);
  const router = useRouter();

  return (
    <div className="flex flex-col gap-3">
      <Input
        type="text"
        placeholder="Zoek teams..."
        value={search}
        onChange={(e) => setSearch(e.target.value)}
        errorOption={false}
      />
      <div className="flex flex-col gap-2 max-h-150 min-h-150 relative pr-1 overflow-x-visible scroll-hidden">
        <div className="flex flex-col gap-2 h-full pb-10 overflow-y-auto overflow-x-visible scroll-hidden">
          {filtered.length === 0 ? (
            <div className="w-full py-8 flex items-center justify-center">
              <Label text="Geen teams gevonden" variant="emptystate" />
            </div>
          ) : (
            filtered.map((t) => (
              <TeamListItem
                key={t.id}
                team={t}
                active={selectedTeamId === t.id}
                onClick={() => router.push(`/teams/${t.id}`)}
              />
            ))
          )}
        </div>
        <div className="absolute bottom-0 w-full h-10 bg-linear-0 from-gray-200 to-transparent" />
      </div>
    </div>
  );
};
