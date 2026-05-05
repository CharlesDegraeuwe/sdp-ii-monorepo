'use client';
import { useState, useMemo } from 'react';
import { FaRegTrashCan } from 'react-icons/fa6';
import { Container } from '@/components/design system/Container';
import { Label } from '@/components/design system/Label';
import { Button } from '@/components/design system/Button';
import { useTaakStore } from '@/stores/taakStore';

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

const formatDue = (iso: string) => {
  const d = new Date(iso);
  const hh = d.getHours().toString().padStart(2, '0');
  const mm = d.getMinutes().toString().padStart(2, '0');
  return `${hh}:${mm}`;
};

export const AssignView = () => {
  const tasks = useTaakStore((s) => s.tasks);
  const teams = useTaakStore((s) => s.teams);
  const members = useTaakStore((s) => s.members);
  const assignTask = useTaakStore((s) => s.assignTask);
  const removeTask = useTaakStore((s) => s.removeTask);

  const [selectedTaskId, setSelectedTaskId] = useState<string | null>(null);
  const [selectedTeamId, setSelectedTeamId] = useState<string | null>(null);
  const [selectedMemberId, setSelectedMemberId] = useState<string | null>(null);

  const unassigned = useMemo(
    () => Object.values(tasks).filter((t) => !t.assigneeId && !t.finished),
    [tasks],
  );

  const teamMembers = useMemo(() => {
    if (!selectedTeamId) return [];
    return teams[selectedTeamId]?.members ?? [];
  }, [teams, selectedTeamId]);

  const handleAssign = async () => {
    if (!selectedTaskId || !selectedMemberId) return;
    assignTask(selectedTaskId, selectedMemberId);

    try {
      await fetch(`/api/taken/${selectedTaskId}/toewijzen`, {
        method: 'PUT',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ werknemerId: Number(selectedMemberId) }),
      });
    } catch (e) {
      console.error(e);
    }

    setSelectedTaskId(null);
    setSelectedMemberId(null);
  };

  const handleDelete = async (id: string) => {
    const backup = tasks[id];
    removeTask(id);
    try {
      await fetch(`/api/taken/${id}`, { method: 'DELETE' });
    } catch (e) {
      if (backup) useTaakStore.getState().addTask(backup);
      console.error(e);
    }
  };

  return (
    <div className={'w-full grid grid-cols-2 gap-5 h-3/4 pt-5'}>
      <Container label={'Taken'}>
        {unassigned.length === 0 ? (
          <div
            className={'w-full h-full flex justify-center items-center pb-12'}
          >
            <Label text={'Geen taken beschikbaar'} variant={'emptystate'} />
          </div>
        ) : (
          <div className={'flex flex-col gap-2'}>
            {unassigned.map((t) => {
              const active = selectedTaskId === t.id;
              return (
                <div key={t.id} className={'flex flex-row items-center gap-2'}>
                  <button
                    onClick={() => setSelectedTaskId(t.id)}
                    className={`flex-1 flex flex-row justify-between items-center px-4 py-2 rounded-full text-sm transition ${
                      active
                        ? 'bg-white ring-2 ring-rose-500 shadow-sm'
                        : 'bg-zinc-100 hover:bg-zinc-200'
                    }`}
                  >
                    <span>{t.name}</span>
                    <span className={'text-xs text-zinc-500'}>
                      Deadline {formatDue(t.dueDate)}
                    </span>
                  </button>
                  <button
                    onClick={() => handleDelete(t.id)}
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
        )}
      </Container>

      <Container label={'Toewijzing'}>
        <div className={'flex flex-col gap-5 h-full'}>
          <div className={'flex flex-col gap-2'}>
            <Label text={'Plant teams'} size={'sm'} weight={600} />
            <div
              className={'flex flex-col gap-2 max-h-40 overflow-y-auto pr-1'}
            >
              {Object.values(teams).length === 0 ? (
                <div className={'w-full py-4 flex items-center justify-center'}>
                  <Label
                    text={'Geen teams beschikbaar'}
                    variant={'emptystate'}
                  />
                </div>
              ) : (
                Object.values(teams).map((team) => {
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
                      <span>{team.naam ?? team.name}</span>
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
                  const member = members[m.id] ?? m;
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
                        {member.firstName ?? member.voornaam}{' '}
                        {member.lastName ?? member.naam}
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
              disabled={!selectedTaskId || !selectedMemberId}
              label={'Taak toewijzen'}
              variant={'primary'}
            />
          </div>
        </div>
      </Container>
    </div>
  );
};
