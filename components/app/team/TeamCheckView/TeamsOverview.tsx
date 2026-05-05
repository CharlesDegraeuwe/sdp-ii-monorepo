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

const memberPalette = [
  'bg-sky-100',
  'bg-emerald-100',
  'bg-amber-100',
  'bg-rose-100',
  'bg-violet-100',
  'bg-orange-100',
  'bg-teal-100',
];

const colorForId = (id: number) =>
  memberPalette[Math.abs(id) % memberPalette.length];

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
    <div className={'grid grid-cols-2 gap-5 w-full h-3/4 pt-5'}>
      <Container label={'Teams'}>
        {filtered.length === 0 && (
          <div
            className={'w-full h-full flex items-center justify-center pb-12'}
          >
            <Label text={'Geen teams gevonden'} variant={'emptystate'} />
          </div>
        )}
        <div className={'flex flex-col gap-2'}>
          {filtered.map((t) => {
            const active = selectedTeamId === t.id;
            return (
              <div key={t.id} className={'flex flex-row items-center gap-2'}>
                <button
                  onClick={() => selectTeam(t.id)}
                  className={`flex-1 px-4 py-2 rounded-full text-sm text-left transition ${
                    active
                      ? 'bg-white ring-2 ring-rose-500 shadow-sm'
                      : 'bg-zinc-100 hover:bg-zinc-200'
                  }`}
                >
                  {t.naam}
                </button>
                <button
                  onClick={() => deleteTeam(t.id)}
                  className={
                    'p-2 rounded-full text-zinc-400 hover:text-rose-500 hover:bg-rose-50 transition'
                  }
                >
                  <FaRegTrashCan className={'w-4 h-4'} />
                </button>
              </div>
            );
          })}
        </div>
      </Container>

      <Container label={'Leden'}>
        {!selectedTeamId ? (
          <div
            className={'w-full h-full flex items-center justify-center pb-12'}
          >
            <Label text={'Selecteer een team'} variant={'emptystate'} />
          </div>
        ) : (
          <div className={'flex flex-col gap-3'}>
            <div className={'flex flex-row justify-end'}>
              <button
                className={
                  'flex flex-row items-center gap-1 text-xs text-zinc-500 hover:text-zinc-800 transition'
                }
              >
                <IoIosAdd className={'w-4 h-4'} />
                <span>Lid toevoegen</span>
              </button>
            </div>

            {leden.length === 0 ? (
              <div className={'w-full py-12 flex items-center justify-center'}>
                <Label text={'Geen leden in dit team'} variant={'emptystate'} />
              </div>
            ) : (
              <div className={'flex flex-col gap-2'}>
                {leden.map((l) => {
                  const bg = l.isSupervisor
                    ? 'bg-amber-100'
                    : colorForId(l.werknemerId);
                  return (
                    <div
                      key={l.werknemerId}
                      className={`group flex flex-row items-center justify-between pl-4 pr-2 py-2 rounded-full text-sm ${bg}`}
                    >
                      <div className={'flex flex-row items-center gap-2'}>
                        <span className={'font-medium'}>
                          {l.voornaam} {l.naam}
                        </span>
                        {l.isSupervisor && (
                          <span className={'text-xs text-zinc-500'}>
                            supervisor
                          </span>
                        )}
                      </div>

                      <div
                        className={
                          'flex flex-row items-center gap-3 opacity-0 group-hover:opacity-100 transition'
                        }
                      >
                        {!l.isSupervisor ? (
                          <button
                            onClick={() =>
                              promoot(selectedTeamId, l.werknemerId)
                            }
                            className={
                              'text-xs underline text-zinc-600 hover:text-zinc-900'
                            }
                          >
                            promoot
                          </button>
                        ) : (
                          <button
                            className={
                              'text-xs underline text-zinc-600 hover:text-zinc-900'
                            }
                          >
                            demoot
                          </button>
                        )}
                        <button
                          onClick={() =>
                            verwijder(selectedTeamId, l.werknemerId)
                          }
                          className={
                            'p-1.5 rounded-full text-zinc-500 hover:text-rose-500 hover:bg-white/60 transition'
                          }
                        >
                          <FaRegTrashCan className={'w-3.5 h-3.5'} />
                        </button>
                      </div>
                    </div>
                  );
                })}
              </div>
            )}
          </div>
        )}
      </Container>
    </div>
  );
};

TeamsOverview.displayName = 'TeamsOverview';
export default TeamsOverview;
