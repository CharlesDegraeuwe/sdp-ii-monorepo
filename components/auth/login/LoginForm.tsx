'use client';
import FormHelper from '@/components/design system/Form/FormHelper';
import { Input } from '@/components/design system/Input';
import { Button } from '@/components/design system/Button';
import { useState } from 'react';
import { getSession, signIn } from 'next-auth/react';
import { useSearchParams } from 'next/navigation';
import { useRouter } from 'next/navigation';
import { FaArrowRight } from 'react-icons/fa';
import { AnimateOnMount } from '@/components/design system/AnimateOnMount';

interface Errors {
  email: string;
  password: string;
}

const initialErrors = {
  email: '',
  password: '',
};

const LoginForm = () => {
  const [email, setEmail] = useState('');
  const [submittedEmail, setSubmittedEmail] = useState(false);
  const [password, setPassword] = useState('');
  const [loading, setLoading] = useState(false);
  const [errors, setErrors] = useState<Errors>(initialErrors);

  const router = useRouter();
  const searchParams = useSearchParams();
  const callbackUrl = searchParams.get('callbackUrl') || `/overzicht`;

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setErrors(initialErrors);
    setLoading(true);

    if (!submittedEmail) {
      try {
        const res = await fetch('/api/auth/email', {
          method: 'POST',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify({ email }),
        });

        if (!res.ok) {
          setErrors({ email: 'Email niet gevonden', password: '' });
          setLoading(false);
          return;
        }

        setSubmittedEmail(true);
        setLoading(false);
      } catch {
        setErrors({ email: 'Er ging iets mis', password: '' });
        setLoading(false);
      }
    } else {
      try {
        const result = await signIn('credentials', {
          email,
          code: password,
          redirect: false,
        });

        if (result?.error) {
          setErrors({ email: '', password: 'Ongeldige code' });
          setLoading(false);
        } else {
          const session = await getSession();
          if (session?.user?.status !== 'Actief') {
            router.push('/activeer');
            return;
          }
          router.push(callbackUrl);
        }
      } catch {
        setErrors({ email: '', password: 'Er ging iets mis' });
        setLoading(false);
      }
    }
  };

  return (
    <FormHelper onSubmit={handleSubmit}>
      <Input
        type="text"
        placeholder="email..."
        onChange={(e) => setEmail(e.target.value)}
      />

      {submittedEmail && (
        <AnimateOnMount className={'flex flex-col gap-3'}>
          <Input
            type="text"
            placeholder="verificatiecode..."
            onChange={(e) => setPassword(e.target.value)}
          />
        </AnimateOnMount>
      )}

      <Button
        color={'delaware_red'}
        textColor={'white'}
        type="submit"
        label={submittedEmail ? 'Login' : 'Verdergaan'}
        iconRight={<FaArrowRight />}
        loading={loading}
      />
    </FormHelper>
  );
};

LoginForm.displayName = 'LoginForm';
export default LoginForm;
