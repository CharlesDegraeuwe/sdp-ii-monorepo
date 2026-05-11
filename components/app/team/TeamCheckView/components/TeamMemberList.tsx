import { Label } from '@/components/design-system/Label';
import { TeamMemberCard } from './TeamMemberCard';
import { TeamLid } from '@/stores/teamStore';

type Props = {
  leden: TeamLid[];
  teamId: number;
};

export const TeamMembersList = ({ leden, teamId }: Props) => {
  if (leden.length === 0) {
    return (
      <div className="w-full py-12 flex items-center justify-center">
        <Label text="Geen leden in dit team" variant="emptystate" />
      </div>
    );
  }

  return (
    <div className="flex flex-col gap-2 overflow-y-auto scroll-hidden pr-1">
      {leden.map((lid) => (
        <TeamMemberCard key={lid.werknemerId} lid={lid} teamId={teamId} />
      ))}
    </div>
  );
};
