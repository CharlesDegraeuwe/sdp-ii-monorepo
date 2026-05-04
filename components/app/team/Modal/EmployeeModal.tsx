'use client';
import { FormHelper } from '@/components/design system/Form';
import { Container } from '@/components/design system/Container';
import { Input } from '@/components/design system/Input';
import { Button } from '@/components/design system/Button';
import { Label } from '@/components/design system/Label';
import { useState } from 'react';
import { useCreateEmployee } from '@/hooks/useCreateEmployee';
import { IoClose } from 'react-icons/io5';

interface Props {
  onClose: () => void;
}

const EmployeeModal = ({ onClose }: Props) => {
  const createEmployee = useCreateEmployee();

  const [voornaam, setVoornaam] = useState('');
  const [naam, setNaam] = useState('');
  const [telefoon, setTelefoon] = useState('');
  const [email, setEmail] = useState('');
  const [beschikbaarheid, setBeschikbaarheid] = useState('');
  const [submitting, setSubmitting] = useState(false);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!voornaam || !naam || !email) return;

    setSubmitting(true);
    try {
      await createEmployee({
        voornaam,
        naam,
        email,
        telefoon,
        beschikbaarheid,
      });
      onClose();
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <div
      className={
        'fixed inset-0 bg-black/40 z-50 flex items-center justify-center p-6'
      }
      onClick={onClose}
    >
      <div className={'w-full max-w-md'} onClick={(e) => e.stopPropagation()}>
        <Container visible>
          <FormHelper onSubmit={handleSubmit} noHeight>
            <div className={'w-full flex flex-col gap-5'}>
              <div className={'flex flex-row justify-between items-center'}>
                <Label text={'Nieuwe werknemer'} size={'lg'} weight={700} />
                <Button
                  icon={<IoClose className={'w-5 h-5'} />}
                  variant={'ghost'}
                  type={'button'}
                  onClick={onClose}
                  px={0}
                />
              </div>

              <div className={'flex flex-col gap-3'}>
                <Label text={'Persoonlijke info'} size={'sm'} weight={600} />
                <Input
                  label={'Voornaam'}
                  type={'text'}
                  placeholder={'Voornaam'}
                  value={voornaam}
                  onChange={(e) => setVoornaam(e.target.value)}
                  errorOption={false}
                />
                <Input
                  label={'Naam'}
                  type={'text'}
                  placeholder={'Naam'}
                  value={naam}
                  onChange={(e) => setNaam(e.target.value)}
                  errorOption={false}
                />
                <Input
                  label={'Telefoon'}
                  type={'text'}
                  placeholder={'Telefoonnummer'}
                  value={telefoon}
                  onChange={(e) => setTelefoon(e.target.value)}
                  errorOption={false}
                />
                <Input
                  label={'Email'}
                  type={'text'}
                  placeholder={'Email'}
                  value={email}
                  onChange={(e) => setEmail(e.target.value)}
                  errorOption={false}
                />
              </div>

              <div className={'flex flex-col gap-3'}>
                <Label text={'Beschikbaarheid'} size={'sm'} weight={600} />
                <Input
                  label={'Beschikbaarheid'}
                  type={'text'}
                  placeholder={'Beschikbaarheid'}
                  value={beschikbaarheid}
                  onChange={(e) => setBeschikbaarheid(e.target.value)}
                  errorOption={false}
                />
              </div>

              <Button
                variant={'primary'}
                type={'submit'}
                label={submitting ? 'Bezig...' : 'Werknemer aanmaken'}
                loading={submitting}
                disabled={submitting}
              />
            </div>
          </FormHelper>
        </Container>
      </div>
    </div>
  );
};

EmployeeModal.displayName = 'EmployeeModal';
export default EmployeeModal;
