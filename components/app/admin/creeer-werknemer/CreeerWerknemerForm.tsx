'use client';
import { useState } from 'react';
import { Input } from '@/components/design-system/Input';
import { FormHelper } from '@/components/design-system/Form';
import { Container } from '@/components/design-system/Container';
import { Button } from '@/components/design-system/Button';

const CreeerWerknemerForm = () => {
  //states
  const [surName, setSurName] = useState<string>('');
  const [name, setName] = useState<string>('');
  const [email, setEmail] = useState<string>('');
  const [phone, setPhone] = useState<string>('');
  const [availability, setAvailability] = useState<string>('');
  const [location, setLocation] = useState<string>('');

  const handleSubmit = (e: React.FormEvent<HTMLFormElement>) => {
    e.preventDefault();
  };

  return (
    <div className="w-1/4 h-3/4">
      <Container>
        <FormHelper onSubmit={handleSubmit} noHeight>
          <div className={'w-full h-full py-2 px-1 flex flex-col gap-5'}>
            <h1 className={'text-xl font-bold'}>Creëer Werknemer</h1>
            <div className={'w-full flex flex-col gap-5'}>
              <span>Persoonlijke info</span>
              <Input
                type="text"
                placeholder="voornaam..."
                onChange={(e) => setSurName(e.target.value)}
              />
              <Input
                type="text"
                placeholder="naam..."
                onChange={(e) => setName(e.target.value)}
              />
              <Input
                type="text"
                placeholder="telefoon..."
                onChange={(e) => setPhone(e.target.value)}
              />
              <Input
                type="text"
                placeholder="email..."
                onChange={(e) => setEmail(e.target.value)}
              />
            </div>

            <div className={'w-full flex flex-col gap-5'}>
              <span>Beschikbaarheid</span>
              <Input
                type="text"
                placeholder="beschikbaarheid..."
                onChange={(e) => setAvailability(e.target.value)}
              />
              <Input
                type="text"
                placeholder="locatie..."
                onChange={(e) => setLocation(e.target.value)}
              />
            </div>

            <div className={'w-full flex flex-col justify-end'}>
              <Button
                color={'delaware_red'}
                textColor={'white'}
                type="submit"
                label={'Creëer Manager'}
              />
            </div>
          </div>
        </FormHelper>
      </Container>
    </div>
  );
};

CreeerWerknemerForm.displayName = 'CreeerWerknemerForm';
export default CreeerWerknemerForm;
