'use client';
import { Label } from '@/components/design-system/Label';
import { useTeamsStore } from '@/stores/teamStore';
import { useTeamLeden } from '@/hooks/useTeamleden';
import { TeamHeader } from './TeamHeader';
import { AddMemberButton } from './AddMemberButton';
import { TeamMembersList } from '@/components/app/team/TeamCheckView/components/TeamMemberList';

type Props = {
  teamId: number | null;
};

export const TeamDetails = ({ teamId }: Props) => {
  const team = useTeamsStore((s) => (teamId ? s.teams[teamId] : null));
  const leden = useTeamLeden(teamId);

  if (!team || !teamId) {
    return (
      <div className="w-full h-full flex items-center justify-center pb-12 px-4">
        <Label text="Selecteer een team" variant="emptystate" />
      </div>
    );
  }

  return (
    <div className="flex flex-col gap-5 h-full p-3">
      <TeamHeader team={team} ledenAantal={leden.length} />

      <hr className="opacity-10" />

      <div className="flex flex-col gap-3 text-sm flex-1 min-h-0">
        <div className="w-full flex flex-row items-center justify-between">
          <span className="text-xs text-zinc-400">Leden</span>
          <AddMemberButton teamId={teamId} />
        </div>

        <TeamMembersList leden={leden} teamId={teamId} />
      </div>
    </div>
  );
};
