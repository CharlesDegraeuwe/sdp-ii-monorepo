import { useTaakStore } from '@/stores/taakStore';
import Select from '@/components/design-system/Select/Select';

export const MemberFilterBar = () => {
  const members = useTaakStore((s) => s.members);
  const selectedMemberId = useTaakStore((s) => s.selectedMemberId);
  const selectMember = useTaakStore((s) => s.selectMember);

  const options = [
    { value: '', label: 'Alle werknemers' },
    ...Object.values(members).map((m) => ({
      value: m.id,
      label: `${m.firstName} ${m.lastName}`,
    })),
  ];

  return (
    <div className="w-1/4">
      <Select
        size={'sm'}
        label="Werknemer"
        options={options}
        value={selectedMemberId ?? ''}
        placeholder="Alle werknemers"
        onChange={(val) => selectMember(val ? String(val) : null)}
      />
    </div>
  );
};
