'use client';
import { useTaakStore } from '@/stores/taakStore';
import { Container } from '@/components/design system/Container';
import { Label } from '@/components/design system/Label';
import { Button } from '@/components/design system/Button';

export const TaskDetailPanel = () => {
  const selectedTaskId = useTaakStore((s) => s.selectedTaskId);
  const task = useTaakStore((s) =>
    selectedTaskId ? s.tasks[selectedTaskId] : null,
  );

  if (!task) {
    return (
      <Container height={'1/2'} label={'Taak details'}>
        <div className={'w-full h-full flex items-center justify-center'}>
          <Label text={'Geen taak geselecteerd'} variant={'emptystate'} />
        </div>
      </Container>
    );
  }

  const due = new Date(task.dueDate);
  const time = `${due.getHours().toString().padStart(2, '0')}:${due
    .getMinutes()
    .toString()
    .padStart(2, '0')}`;

  return (
    <Container height={'1/2'} label={'Taak details'}>
      <div className={'flex flex-row justify-between items-center'}>
        <Button
          type="button"
          label={'Bewerk'}
          variant={'outline'}
          textSize={'sm'}
          px={'px-3'}
        />
      </div>
      <div className={'flex flex-row justify-between text-sm text-zinc-500'}>
        <span>Deadline vandaag om {time}</span>
        <span>{task.location}</span>
      </div>
      <div className={'px-4 py-3 bg-zinc-100 rounded-full text-sm'}>
        {task.name}
      </div>
      <div className={'px-4 py-3 bg-zinc-100 rounded-3xl text-sm min-h-32'}>
        {task.specifications ?? ''}
      </div>
    </Container>
  );
};
