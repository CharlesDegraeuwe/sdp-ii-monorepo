'use client';
import { useState } from 'react';
import { useCreateTask } from '@/hooks/useCreateTask';
import { Input } from '@/components/design system/Input';
import { Container } from '@/components/design system/Container';
import { Button } from '@/components/design system/Button';
import { FormHelper } from '@/components/design system/Form';
import { Label } from '@/components/design system/Label';
import { TextArea } from '@/components/design system/TextArea';

export const CreateView = () => {
  const createTask = useCreateTask();
  const [name, setName] = useState('');
  const [specifications, setSpecifications] = useState('');
  const [dueDate, setDueDate] = useState('');
  const [location, setLocation] = useState('Plant 1');
  const [wholeDay, setWholeDay] = useState(false);
  const [hour, setHour] = useState('00');
  const [minute, setMinute] = useState('00');
  const [duration, setDuration] = useState('');
  const [submitting, setSubmitting] = useState(false);

  const handleSubmit = async () => {
    if (!name || !dueDate) return;
    setSubmitting(true);
    try {
      const [d, m, y] = dueDate.split('-');
      const iso = wholeDay
        ? new Date(`${y}-${m}-${d}T00:00:00`).toISOString()
        : new Date(`${y}-${m}-${d}T${hour}:${minute}:00`).toISOString();

      await createTask({
        name,
        specifications,
        dueDate: iso,
        location,
        duration: wholeDay ? undefined : duration,
        important: false,
      });

      setName('');
      setSpecifications('');
      setDueDate('');
      setDuration('');
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <div className={'flex flex-col pt-5 min-h-full w-full'}>
      <Container width={'2/3'} height={'fit'} label={'Taak beschrijving'}>
        <FormHelper onSubmit={handleSubmit} noHeight>
          <Input
            label={'Naam'}
            value={name}
            onChange={(e) => setName(e.target.value)}
            placeholder={'Taaknaam'}
            errorOption={false}
          />
          <TextArea
            label={'Specificaties'}
            value={specifications}
            onChange={(e) => setSpecifications(e.target.value)}
            placeholder={'Specificaties'}
          />

          <div className={'grid grid-cols-2 gap-3'}>
            <Input
              label={'Deadline'}
              value={dueDate}
              onChange={(e) => setDueDate(e.target.value)}
              placeholder={'dd-mm-yyyy'}
              errorOption={false}
            />
            <div className={'flex flex-col gap-1'}>
              <Label text={'Locatie'} variant={'inputLabel'} />
              <select
                value={location}
                onChange={(e) => setLocation(e.target.value)}
                className={
                  'w-full rounded-full outline-none ring-0 border border-gray-300/30 focus:border-gray-700/30 px-5 py-3.5 bg-gray-300/30 shadow-inner'
                }
              >
                <option>Plant 1</option>
                <option>Plant 2</option>
                <option>Plant 3</option>
              </select>
            </div>
          </div>

          <div className={'flex flex-row items-center gap-3'}>
            <input
              type={'checkbox'}
              checked={wholeDay}
              onChange={(e) => setWholeDay(e.target.checked)}
            />
            <span className={'text-sm truncate min-w-fit'}>Hele dag</span>
            {!wholeDay && (
              <>
                <span className={'text-xs text-zinc-500'}>of</span>
                <div className={'flex-row items-center justify-end flex gap-3'}>
                  <Input
                    value={hour}
                    onChange={(e) => setHour(e.target.value)}
                    errorOption={false}
                  />
                  <span>:</span>
                  <Input
                    value={minute}
                    onChange={(e) => setMinute(e.target.value)}
                    errorOption={false}
                  />
                  <Input
                    value={duration}
                    onChange={(e) => setDuration(e.target.value)}
                    placeholder={'uu:mm'}
                    errorOption={false}
                  />
                </div>
              </>
            )}
          </div>

          <Button
            type="submit"
            label={submitting ? 'Aanmaken...' : 'Taak aanmaken'}
            loading={submitting}
            disabled={submitting}
            variant={'primary'}
          />
        </FormHelper>
      </Container>
    </div>
  );
};
