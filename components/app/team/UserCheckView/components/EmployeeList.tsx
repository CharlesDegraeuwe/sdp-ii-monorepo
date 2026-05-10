'use client';
import { useState } from 'react';
import { useRouter } from 'next/navigation';
import { Input } from '@/components/design-system/Input';
import { Label } from '@/components/design-system/Label';
import { EmployeeListItem } from './EmployeeListItem';
import { useFilteredWerknemers } from '@/hooks/useFilteredWerknemers';

type Props = {
  selectedWerknemerId?: number | null;
};

export const EmployeeList = ({ selectedWerknemerId = null }: Props) => {
  const [search, setSearch] = useState('');
  const filtered = useFilteredWerknemers(search);
  const router = useRouter();

  return (
    <div className="flex flex-col gap-3">
      <Input
        type="text"
        placeholder="Zoek werknemers..."
        value={search}
        onChange={(e) => setSearch(e.target.value)}
        errorOption={false}
      />
      <div className="flex flex-col gap-2 max-h-150 min-h-150 relative pr-1 overflow-x-visible scroll-hidden">
        <div className="flex flex-col gap-2 h-full pb-10 overflow-y-auto overflow-x-visible scroll-hidden">
          {filtered.length === 0 ? (
            <div className="w-full py-8 flex items-center justify-center">
              <Label text="Geen werknemers gevonden" variant="emptystate" />
            </div>
          ) : (
            filtered.map((e) => (
              <EmployeeListItem
                key={e.id}
                werknemer={e}
                active={selectedWerknemerId === e.id}
                onClick={() => router.push(`/teams/werknemers/${e.id}`)}
              />
            ))
          )}
        </div>
        <div className="absolute bottom-0 w-full h-10 bg-linear-0 from-gray-200 to-transparent" />
      </div>
    </div>
  );
};
