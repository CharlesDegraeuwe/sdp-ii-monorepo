'use client';
import { useTaakStore } from '@/stores/taakStore';
import Select from '@/components/design-system/Select/Select';

export const TeamFilterBar = () => {
  const teams = useTaakStore((s) => s.teams);
  const selectedTeamId = useTaakStore((s) => s.selectedTeamId);
  const selectTeam = useTaakStore((s) => s.selectTeam);

  const options = [
    { value: '', label: 'Alle teams' },
    ...Object.values(teams).map((t) => ({ value: t.id, label: t.name })),
  ];

  return (
    <div className="w-1/4">
      <Select
        size={'sm'}
        label="Team"
        options={options}
        value={selectedTeamId ?? ''}
        placeholder="Alle teams"
        onChange={(val) => selectTeam(val ? String(val) : null)}
      />
    </div>
  );
};
