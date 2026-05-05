'use client';
import { useTaakStore } from '@/stores/taakStore';

export const TeamFilterBar = () => {
  const teams = useTaakStore((s) => s.teams);
  const selectedTeamId = useTaakStore((s) => s.selectedTeamId);
  const selectTeam = useTaakStore((s) => s.selectTeam);

  return (
    <div className={'flex flex-row gap-2 flex-wrap'}>
      {Object.values(teams).map((t) => (
        <button
          key={t.id}
          onClick={() => selectTeam(t.id)}
          className={`px-4 py-2 rounded-full text-sm ${
            selectedTeamId === t.id
              ? 'bg-blue-200'
              : 'bg-white hover:bg-zinc-100'
          }`}
        >
          {t.name}
        </button>
      ))}
    </div>
  );
};
