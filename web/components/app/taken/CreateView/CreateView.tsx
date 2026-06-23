'use client';
import { useState } from 'react';
import { useCreateTaak } from '@/hooks/useCreateTaak';
import { Input } from '@/components/design-system/Input';
import { Container } from '@/components/design-system/Container';
import { Button } from '@/components/design-system/Button';
import { FormHelper } from '@/components/design-system/Form';
import { TextArea } from '@/components/design-system/TextArea';
import { useToast } from '@/providers/ToastProvider';

export const CreateView = () => {
  const { mutateAsync: createTaak, isPending } = useCreateTaak();
  const toast = useToast();
  const [name, setName] = useState('');
  const [specifications, setSpecifications] = useState('');
  const [dueDate, setDueDate] = useState('');
  const [wholeDay, setWholeDay] = useState(false);
  const [time, setTime] = useState('08:00');

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!name || !dueDate) return;
    try {
      const deadline = wholeDay ? dueDate : `${dueDate}T${time}:00`;
      await createTaak({
        name,
        specifications,
        dueDate: deadline,
        location: '',
        important: false,
      });
      setName('');
      setSpecifications('');
      setDueDate('');
      setTime('08:00');
      setWholeDay(false);
      toast.success('Taak aangemaakt');
    } catch {
      toast.error('Kon taak niet aanmaken');
    }
  };

  return (
    <div className="flex flex-col pt-5 w-full">
      <Container height="fit" label="Taak aanmaken">
        <FormHelper onSubmit={handleSubmit} noHeight>
          <Input
            label="Naam"
            value={name}
            onChange={(e) => setName(e.target.value)}
            placeholder="Taaknaam"
            errorOption={false}
          />

          <TextArea
            label="Specificaties"
            value={specifications}
            onChange={(e) => setSpecifications(e.target.value)}
            placeholder="Optionele beschrijving"
          />

          <div className="grid grid-cols-1 sm:grid-cols-2 gap-3">
            <Input
              type="date"
              label="Deadline"
              value={dueDate}
              onChange={(e) => setDueDate(e.target.value)}
              errorOption={false}
            />
            {!wholeDay && (
              <Input
                type="time"
                label="Tijdstip"
                value={time}
                onChange={(e) => setTime(e.target.value)}
                errorOption={false}
              />
            )}
          </div>

          <label className="flex items-center gap-2.5 cursor-pointer w-fit">
            <input
              type="checkbox"
              checked={wholeDay}
              onChange={(e) => setWholeDay(e.target.checked)}
              className="w-4 h-4 rounded"
            />
            <span className="text-sm text-zinc-700">Hele dag</span>
          </label>

          <Button
            type="submit"
            label={isPending ? 'Aanmaken...' : 'Taak aanmaken'}
            loading={isPending}
            disabled={isPending || !name || !dueDate}
            variant="primary"
          />
        </FormHelper>
      </Container>
    </div>
  );
};
