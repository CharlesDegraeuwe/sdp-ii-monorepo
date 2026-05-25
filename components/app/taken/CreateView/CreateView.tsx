'use client';
import { useState } from 'react';
import { useCreateTaak } from '@/hooks/useCreateTaak';
import { Input } from '@/components/design-system/Input';
import { Container } from '@/components/design-system/Container';
import { Button } from '@/components/design-system/Button';
import { FormHelper } from '@/components/design-system/Form';
import { TextArea } from '@/components/design-system/TextArea';
import Select from '@/components/design-system/Select/Select';
import { useToast } from '@/providers/ToastProvider';

export const CreateView = () => {
  const { mutateAsync: createTaak, isPending } = useCreateTaak();
  const toast = useToast();
  const [name, setName] = useState('');
  const [specifications, setSpecifications] = useState('');
  const [dueDate, setDueDate] = useState('');
  const [location, setLocation] = useState('Plant 1');
  const [wholeDay, setWholeDay] = useState(false);
  const [hour, setHour] = useState('00');
  const [minute, setMinute] = useState('00');
  const [duration, setDuration] = useState('');

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!name || !dueDate) return;
    try {
      const deadline = wholeDay
        ? dueDate
        : `${dueDate}T${hour.padStart(2, '0')}:${minute.padStart(2, '0')}:00`;

      await createTaak({
        name,
        specifications,
        dueDate: deadline,
        location,
        duration: wholeDay ? undefined : duration,
        important: false,
      });

      setName('');
      setSpecifications('');
      setDueDate('');
      setDuration('');
      toast.success('Taak aangemaakt');
    } catch {
      toast.error('Kon taak niet aanmaken');
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
            label={isPending ? 'Aanmaken...' : 'Taak aanmaken'}
            loading={isPending}
            disabled={isPending || !name || !dueDate}
            variant={'primary'}
          />
        </FormHelper>
      </Container>
    </div>
  );
};
