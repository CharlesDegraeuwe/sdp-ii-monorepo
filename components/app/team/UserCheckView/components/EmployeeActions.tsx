import { Button } from '@/components/design-system/Button';
import { useTeamsStore, Werknemer } from '@/stores/teamStore';

type Props = {
  employee: Werknemer;
};

export const EmployeeActions = ({ employee }: Props) => {
  const updateWerknemer = useTeamsStore((s) => s.updateWerknemer);
  const isBlocked = employee.status === 'Geblokkeerd';

  return (
    <div className="flex flex-col gap-2 mt-auto">
      {employee.role === 'Supervisor' && (
        <Button
          variant="primary"
          label="Degradeer"
          onClick={() => updateWerknemer(employee.id, { role: 'Werknemer' })}
        />
      )}
      <Button
        variant="outline"
        label={isBlocked ? 'Deblokkeer werknemer' : 'Blokkeer werknemer'}
        onClick={() =>
          updateWerknemer(employee.id, {
            status: isBlocked ? 'Actief' : 'Geblokkeerd',
          })
        }
      />
    </div>
  );
};
