import { useTaakStore } from '@/stores/taakStore';

export const MemberFilterBar = () => {
  const members = useTaakStore((s) => s.members);
  const selectedMemberId = useTaakStore((s) => s.selectedMemberId);
  const selectMember = useTaakStore((s) => s.selectMember);

  return (
    <div className={'flex flex-row gap-2 flex-wrap'}>
      {Object.values(members).map((m) => (
        <button
          key={m.id}
          onClick={() => selectMember(m.id)}
          className={`px-4 py-2 rounded-full text-sm ${
            selectedMemberId === m.id
              ? 'bg-blue-200'
              : 'bg-white hover:bg-zinc-100'
          }`}
        >
          {m.firstName}
        </button>
      ))}
    </div>
  );
};
