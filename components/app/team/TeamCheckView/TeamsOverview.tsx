'use client';
import { Container } from '@/components/design system/Container';
import { Label } from '@/components/design system/Label';
import { Button } from '@/components/design system/Button';
import { useTeamsStore } from '@/stores/teamStore';
import { useTeamLeden } from '@/hooks/useTeamleden';
import { useDeleteTeam } from '@/hooks/useDeleteTeam';
import { useTeamMembership } from '@/hooks/useTeamMemberShip';
import { FaRegTrashCan } from 'react-icons/fa6';
import { IoIosAdd } from 'react-icons/io';
import { useMemo } from 'react';

const TeamsOverview = () => {
  const teams = useTeamsStore((s) => s.teams);
  const filterSiteId = useTeamsStore((s) => s.filterSiteId);
  const selectedTeamId = useTeamsStore((s) => s.selectedTeamId);
  const selectTeam = useTeamsStore((s) => s.selectTeam);

  const deleteTeam = useDeleteTeam();
  const { promoot, verwijder } = useTeamMembership();
  const leden = useTeamLeden(selectedTeamId);

  const filtered = useMemo(() => {
    const list = Object.values(teams);
    if (!filterSiteId) return list;
    return list.filter((t) => t.siteId === filterSiteId);
  }, [teams, filterSiteId]);

  return (
    <div className={'w-full h-3/4 grid grid-cols-2 gap-5 pt-5'}>
      <Container label={'Teams'}>
        {filtered.length === 0 && (
          <div
            className={'w-full h-full flex items-center justify-center pb-12'}
          >
            <Label text={'Geen teams gevonden'} variant={'emptystate'} />
          </div>
        )}
        {filtered.map((t) => {
          const active = selectedTeamId === t.id;
          return (
            <div key={t.id} className={'flex flex-row items-center gap-2'}>
              <button
                onClick={() => selectTeam(t.id)}
                className={`flex-1 px-4 py-2 rounded-full text-sm text-left transition ${
                  active
                    ? 'bg-white ring-2 ring-rose-500'
                    : 'bg-zinc-100 hover:bg-zinc-200'
                }`}
              >
                {t.naam}
              </button>
              <button
                onClick={() => deleteTeam(t.id)}
                className={'p-2 rounded-full hover:bg-zinc-100'}
              >
                <FaRegTrashCan className={'w-4 h-4'} />
              </button>
            </div>
          );
        })}
      </Container>

      <Container label={'Leden'}>
        {!selectedTeamId ? (
          <div
            className={'w-full h-full flex items-center justify-center pb-12'}
          >
            <Label text={'Selecteer een team'} variant={'emptystate'} />
          </div>
        ) : (
          <>
            <div className={'flex flex-row justify-end'}>
              <button
                className={
                  'flex flex-row items-center gap-1 text-xs text-zinc-500 hover:text-zinc-800'
                }
              >
                <IoIosAdd className={'w-4 h-4'} />
                <span>Lid toevoegen</span>
              </button>
            </div>

            {leden.length === 0 && (
              <div
                className={
                  'w-full h-full flex items-center justify-center pb-12'
                }
              >
                <Label text={'Geen leden in dit team'} variant={'emptystate'} />
              </div>
            )}

            {leden.map((l) => (
              <div
                key={l.werknemerId}
                className={`flex flex-row items-center justify-between px-4 py-2 rounded-full text-sm ${
                  l.isSupervisor ? 'bg-yellow-100' : 'bg-zinc-100'
                }`}
              >
                <span>
                  {l.voornaam} {l.naam}
                  {l.isSupervisor && (
                    <span className={'text-xs text-zinc-500 ml-2'}>
                      supervisor
                    </span>
                  )}
                </span>
                <div className={'flex flex-row gap-2'}>
                  {!l.isSupervisor && (
                    <Button
                      type="button"
                      label={'Promoot'}
                      variant={'outline'}
                      textSize={'sm'}
                      px={'px-3'}
                      onClick={() => promoot(selectedTeamId, l.werknemerId)}
                    />
                  )}
                  <Button
                    type="button"
                    label={'Verwijder'}
                    variant={'outline'}
                    textSize={'sm'}
                    px={'px-3'}
                    onClick={() => verwijder(selectedTeamId, l.werknemerId)}
                  />
                </div>
              </div>
            ))}
          </>
        )}
      </Container>
    </div>
  );
};

TeamsOverview.displayName = 'TeamsOverview';
export default TeamsOverview;
