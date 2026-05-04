'use client';
import { useState } from 'react';
import { FaRegTrashCan } from 'react-icons/fa6';
import { Container } from '@/components/design system/Container';
import { Label } from '@/components/design system/Label';
import { Button } from '@/components/design system/Button';
import { useTaakStore } from '@/stores/taakStore';

const memberColors: Record<string, string> = {
  blue: 'bg-blue-100 ring-blue-300',
  green: 'bg-green-100 ring-green-300',
  yellow: 'bg-yellow-100 ring-yellow-300',
  red: 'bg-red-100 ring-red-300',
};

export const AssignView = () => {
  const tasks = useTaakStore((s) => s.tasks);
  const members = useTaakStore((s) => s.members);
  const assignTask = useTaakStore((s) => s.assignTask);
  const removeTask = useTaakStore((s) => s.removeTask);

  const [selectedTaskId, setSelectedTaskId] = useState<string | null>(null);
  const [selectedMemberId, setSelectedMemberId] = useState<string | null>(null);

  const unassigned = Object.values(tasks).filter(
    (t) => !t.assigneeId && !t.finished,
  );

  const handleAssign = async () => {
    if (!selectedTaskId || !selectedMemberId) return;
    assignTask(selectedTaskId, selectedMemberId);

    try {
      await fetch(`/api/tasks/${selectedTaskId}/assign`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ memberId: selectedMemberId }),
      });
    } catch (e) {
      console.error(e);
    }

    setSelectedTaskId(null);
    setSelectedMemberId(null);
  };

  return (
    <div className={'w-full grid grid-cols-2 gap-4 h-2/3 pt-5'}>
      <Container label={'Taken'}>
        {unassigned.length === 0 && (
          <div
            className={'w-full h-full flex justify-center items-center pb-12'}
          >
            <Label text={'Geen taken beschikbaar'} variant={'emptystate'} />
          </div>
        )}
        {unassigned.map((t) => {
          const active = selectedTaskId === t.id;
          return (
            <div key={t.id} className={'flex flex-row items-center gap-2'}>
              <button
                onClick={() => setSelectedTaskId(t.id)}
                className={`flex-1 flex flex-row justify-between items-center px-4 py-2 rounded-full text-sm ${
                  active
                    ? 'bg-white ring-2 ring-blue-400'
                    : 'bg-zinc-100 hover:bg-zinc-200'
                }`}
              >
                <span>{t.name}</span>
                <span className={'text-xs text-zinc-500'}>
                  Due today{' '}
                  {new Date(t.dueDate).toLocaleTimeString().slice(0, 5)}
                </span>
              </button>
              <button
                onClick={() => removeTask(t.id)}
                className={'p-2 rounded-full hover:bg-zinc-100'}
              >
                <FaRegTrashCan className={'w-4 h-4'} />
              </button>
            </div>
          );
        })}
      </Container>

      <Container label={'Toewijzing'}>
        <div className={'flex flex-col justify-between h-full'}>
          {selectedMemberId === null && (
            <div className={'w-full h-full flex justify-center items-center'}>
              <Label
                text={'Geen teamlid geselecteerd'}
                variant={'emptystate'}
              />
            </div>
          )}
          {Object.values(members).map((m) => {
            const active = selectedMemberId === m.id;
            const color = memberColors[m.color ?? 'blue'];
            return (
              <button
                key={m.id}
                onClick={() => setSelectedMemberId(m.id)}
                className={`px-4 py-2 rounded-full text-sm text-left ring-2 ${color} ${
                  active ? 'ring-offset-2' : ''
                }`}
              >
                {m.firstName} {m.lastName}
              </button>
            );
          })}

          <Button
            onClick={handleAssign}
            disabled={!selectedTaskId || !selectedMemberId}
            label={'Taak toewijzen'}
            variant={'primary'}
          />
        </div>
      </Container>
    </div>
  );
};
