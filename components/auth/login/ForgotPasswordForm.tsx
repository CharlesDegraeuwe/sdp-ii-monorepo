'use client';
import { Input } from '@/components/design-system/Input';
import { AnimateOnMount } from '@/components/design-system/AnimateOnMount';
import { Button } from '@/components/design-system/Button';
import { FaArrowRight } from 'react-icons/fa';
import FormHelper from '../../design-system/Form/FormHelper';
import { useToast } from '@/providers/ToastProvider';
import { useState } from 'react';
import { useSplash } from '@/providers/SplashProvider';

const BASE = process.env.NEXT_PUBLIC_API_URL ?? 'http://localhost:8080/api';

interface FpwProps {
  setForgotForm: (value: boolean) => void;
}
const ForgotPasswordForm = (props: FpwProps) => {
  const { setForgotForm } = props;
  const { splashOpen, setSplashOpen } = useSplash();

  const toast = useToast();
  const [email, setEmail] = useState('');
  const [resetCode, setResetCode] = useState('');
  const [nieuwWachtwoord, setNieuwWachtwoord] = useState('');
  const [loading, setLoading] = useState(false);
  const [emailVerstuurd, setEmailVerstuurd] = useState(false);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setLoading(true);

    if (!emailVerstuurd) {
      try {
        const res = await fetch(`${BASE}/werknemers/wachtwoord-vergeten`, {
          method: 'POST',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify({ email }),
        });

        if (!res.ok) {
          toast.error('E-mailadres niet gevonden');
          setLoading(false);
          return;
        }

        toast.success('Reset code verstuurd naar je e-mail');
        setEmailVerstuurd(true);
      } catch {
        toast.error('Er ging iets mis');
      }
    } else {
      try {
        const res = await fetch(`${BASE}/werknemers/wachtwoord-resetten`, {
          method: 'POST',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify({ resetCode, nieuwWachtwoord }),
        });

        if (!res.ok) {
          toast.error('Ongeldige reset code');
          setLoading(false);
          return;
        }

        toast.success('Wachtwoord succesvol gereset');
        setForgotForm(false);
        return;
      } catch {
        toast.error('Er ging iets mis');
      }
    }

    setLoading(false);
  };

  return (
    <FormHelper onSubmit={handleSubmit} noHeight gap={5}>
      {!emailVerstuurd ? (
        <>
          <Input
            type="text"
            placeholder="email..."
            value={email}
            onChange={(e) => setEmail(e.target.value)}
          />
          <Button
            color={'delaware_red'}
            textColor={'white'}
            type="submit"
            label={'Verstuur reset code'}
            iconRight={<FaArrowRight />}
            loading={loading}
          />
        </>
      ) : (
        <>
          <AnimateOnMount className={'flex flex-col gap-3'}>
            <Input
              type="text"
              placeholder="reset code..."
              value={resetCode}
              onChange={(e) => setResetCode(e.target.value)}
            />
            <Input
              type="password"
              placeholder="nieuw wachtwoord..."
              value={nieuwWachtwoord}
              onChange={(e) => setNieuwWachtwoord(e.target.value)}
            />
          </AnimateOnMount>
          <Button
            color={'delaware_red'}
            textColor={'white'}
            type="submit"
            label={'Wachtwoord resetten'}
            iconRight={<FaArrowRight />}
            loading={loading}
          />
        </>
      )}
    </FormHelper>
  );
};

export default ForgotPasswordForm;
