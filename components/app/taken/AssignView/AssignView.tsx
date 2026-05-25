'use client';
import { useState, useMemo, useEffect } from 'react';
import { Container } from '@/components/design-system/Container';
import { Label } from '@/components/design-system/Label';
import { Button } from '@/components/design-system/Button';
import { useUser } from '@/providers/UserProvider';
import { useToast } from '@/providers/ToastProvider';
import { HiOutlineClipboardDocumentList } from 'react-icons/hi2';
import { useTaken } from '@/hooks/useTaken';
import { useTeams } from '@/hooks/useTeams';
import { useAssignTaak } from '@/hooks/useAssignTaak';

const memberPalette = [
  'bg-sky-100',
  'bg-emerald-100',
  'bg-amber-100',
  'bg-rose-100',
  'bg-violet-100',
  'bg-orange-100',
  'bg-teal-100',
];

const colorForId = (id: string) => {
  let hash = 0;
  for (let i = 0; i < id.length; i++)
    hash = id.charCodeAt(i) + ((hash << 5) - hash);
  return memberPalette[Math.abs(hash) % memberPalette.length];
};

export const AssignView = () => {
  const { data: tasks = [] } = useTaken();
  const { data: allTeams = [] } = useTeams();
  const { mutateAsync: assignTaak, isPending } = useAssignTaak();
  const { user, isSupervisor } = useUser();
  const toast = useToast();

  const [selectedTaskId, setSelectedTaskId] = useState<string | null>(null);
  const [selectedTeamId, setSelectedTeamId] = useState<string | null>(null);
  const [selectedMemberId, setSelectedMemberId] = useState<string | null>(null);
  const [supervisorTeamIds, setSupervisorTeamIds] = useState<string[]>([]);

  useEffect(() => {
    if (!isSupervisor || !user?.id) return;
    const fetchMyTeams = async () => {
      try {
        const res = await fetch(`/api/teams/werknemer/${user.id}`);
        if (!res.ok) return;
        const data = (await res.json()) as { id: number }[];
        setSupervisorTeamIds(data.map((t) => String(t.id)));
      } catch {
        // ignore
      }
    };
    void fetchMyTeams();
  }, [isSupervisor, user?.id]);

  const visibleTeams = useMemo(() => {
    if (!isSupervisor) return allTeams;
    return allTeams.filter((t) => supervisorTeamIds.includes(t.id));
  }, [allTeams, isSupervisor, supervisorTeamIds]);

  const unassigned = useMemo(
    () => tasks.filter((t) => !t.assigneeId && !t.finished),
    [tasks],
  );

  const teamMembers = useMemo(() => {
    if (!selectedTeamId) return [];
    return allTeams.find((t) => t.id === selectedTeamId)?.members ?? [];
  }, [allTeams, selectedTeamId]);

  const handleAssign = async () => {
    if (!selectedTaskId || !selectedMemberId) return;
    try {
      await assignTaak({ taskId: selectedTaskId, memberId: selectedMemberId });
      toast.success('Taak toegewezen');
    } catch {
      toast.error('Kon taak niet toewijzen');
    }
    setSelectedTaskId(null);
    setSelectedMemberId(null);
  };

  return (
    <div
      className={'w-full grid grid-cols-1 sm:grid-cols-2 gap-5 min-h-96 pt-5'}
    >
      <Container label={'Niet-toegewezen taken'} padding="0">
        {unassigned.length === 0 ? (
          <div
            className={'w-full h-full flex justify-center items-center pb-12'}
          >
            <Label text={'Geen taken beschikbaar'} variant={'emptystate'} />
          </div>
        ) : (
          <div className="flex flex-col gap-2 max-h-150 min-h-150 relative pr-1 overflow-x-visible scroll-hidden">
            <div className="flex flex-col gap-2 h-full pb-10 overflow-y-auto overflow-x-visible scroll-hidden">
              {unassigned.map((t) => {
                const active = selectedTaskId === t.id;
                return (
                  <div
                    key={t.id}
                    onClick={() => setSelectedTaskId(t.id)}
                    className="p-0.5"
                  >
                    <div
                      className={`px-4 py-2 shadow-sm rounded-full text-sm flex flex-row gap-2 items-center min-h-13 cursor-pointer text-left transition ${
                        active
                          ? 'bg-white ring ring-zinc-50'
                          : 'bg-zinc-100 hover:bg-zinc-200'
                      }`}
                    >
                      <div className="w-8 h-8 rounded-full bg-zinc-200 flex items-center justify-center shrink-0">
                        <HiOutlineClipboardDocumentList className="w-4 h-4 text-zinc-600" />
                      </div>
                      <span className="flex-1 truncate">{t.name}</span>
                    </div>
                  </div>
                );
              })}
            </div>
            <div className="absolute bottom-0 w-full h-10 bg-linear-0 from-gray-200 to-transparent" />
          </div>
        )}
      </Container>

      <Container label={'Toewijzing'}>
        <div className={'flex flex-col gap-5 h-full'}>
          <div className={'flex flex-col gap-2'}>
            <Label text={'Teams'} size={'sm'} weight={600} />
            <div
              className={'flex flex-col gap-2 max-h-40 overflow-y-auto pr-1'}
            >
              {visibleTeams.length === 0 ? (
                <div className={'w-full py-4 flex items-center justify-center'}>
                  <Label
                    text={'Geen teams beschikbaar'}
                    variant={'emptystate'}
                  />
                </div>
              ) : (
                visibleTeams.map((team) => {
                  const active = selectedTeamId === team.id;
                  return (
                    <button
                      key={team.id}
                      onClick={() => {
                        setSelectedTeamId(team.id);
                        setSelectedMemberId(null);
                      }}
                      className={`flex flex-row items-center gap-3 px-4 py-2 rounded-full text-sm text-left transition ${
                        active ? 'bg-zinc-200' : 'bg-zinc-100 hover:bg-zinc-200'
                      }`}
                    >
                      <span
                        className={`w-3.5 h-3.5 rounded-full border ${
                          active
                            ? 'bg-zinc-500 border-zinc-500'
                            : 'border-zinc-300 bg-white'
                        }`}
                      />
                      <span>{team.name}</span>
                    </button>
                  );
                })
              )}
            </div>
          </div>

          <div className={'flex flex-col gap-2 flex-1 min-h-0'}>
            <Label text={'Teamleden'} size={'sm'} weight={600} />
            {!selectedTeamId ? (
              <div className={'w-full flex-1 flex justify-center items-center'}>
                <Label
                  text={'Selecteer eerst een team'}
                  variant={'emptystate'}
                />
              </div>
            ) : teamMembers.length === 0 ? (
              <div className={'w-full flex-1 flex justify-center items-center'}>
                <Label text={'Geen leden in dit team'} variant={'emptystate'} />
              </div>
            ) : (
              <div className={'flex flex-col gap-2 overflow-y-auto pr-1'}>
                {teamMembers.map((m) => {
                  const active = selectedMemberId === m.id;
                  const bg = colorForId(m.id);
                  return (
                    <button
                      key={m.id}
                      onClick={() => setSelectedMemberId(m.id)}
                      className={`flex flex-row items-center gap-3 px-4 py-2 rounded-full text-sm text-left transition ${bg} ${
                        active ? 'ring-2 ring-rose-500 shadow-sm' : ''
                      }`}
                    >
                      <span
                        className={`w-3.5 h-3.5 rounded-full border ${
                          active
                            ? 'bg-zinc-700 border-zinc-700'
                            : 'border-zinc-400 bg-white/60'
                        }`}
                      />
                      <span>
                        {m.firstName} {m.lastName}
                      </span>
                    </button>
                  );
                })}
              </div>
            )}
          </div>

          <div className={'mt-auto'}>
            <Button
              onClick={handleAssign}
              disabled={!selectedTaskId || !selectedMemberId || isPending}
              label={isPending ? 'Toewijzen...' : 'Taak toewijzen'}
              variant={'primary'}
            />
          </div>
        </div>
      </Container>
    </div>
  );
};
