'use client';
import { useState } from 'react';
import { useCreateTask } from '@/hooks/useCreateTask';
import { Input } from '@/components/design-system/Input';
import { Container } from '@/components/design-system/Container';
import { Button } from '@/components/design-system/Button';
import { FormHelper } from '@/components/design-system/Form';
import { TextArea } from '@/components/design-system/TextArea';
import Select from '@/components/design-system/Select/Select';

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
      <Container height={'fit'} label={'Taak beschrijving'}>
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

          <div className={'grid grid-cols-1 sm:grid-cols-2 gap-3'}>
            <Input
              type={'date'}
              label={'Deadline'}
              value={dueDate}
              onChange={(e) => setDueDate(e.target.value)}
              placeholder={'dd-mm-yyyy'}
              errorOption={false}
            />
            <Select
              label="Locatie"
              options={[
                { value: 'Plant 1', label: 'Plant 1' },
                { value: 'Plant 2', label: 'Plant 2' },
                { value: 'Plant 3', label: 'Plant 3' },
              ]}
              value={location}
              placeholder="Selecteer locatie"
              onChange={(val) => setLocation(String(val))}
            />
          </div>

          <div className={'flex flex-col sm:flex-row sm:items-center gap-3'}>
            <div className="flex items-center gap-3">
              <input
                type={'checkbox'}
                checked={wholeDay}
                onChange={(e) => setWholeDay(e.target.checked)}
              />
              <span className={'text-sm truncate min-w-fit'}>Hele dag</span>
            </div>
            {!wholeDay && (
              <>
                <span className={'text-xs text-zinc-500 hidden sm:inline'}>
                  of
                </span>
                <div
                  className={
                    'flex-row items-center justify-start flex gap-3 flex-wrap'
                  }
                >
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
