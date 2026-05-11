'use client';
import FormHelper from '@/components/design-system/Form/FormHelper';
import { Input } from '@/components/design-system/Input';
import { Button } from '@/components/design-system/Button';
import { useState } from 'react';
import { getSession, signIn } from 'next-auth/react';
import { useSearchParams } from 'next/navigation';
import { useRouter } from 'next/navigation';
import { FaArrowRight } from 'react-icons/fa';
import { AnimateOnMount } from '@/components/design-system/AnimateOnMount';
import { useToast } from '@/providers/ToastProvider';

interface Errors {
  email: string;
  password: string;
}

const initialErrors = {
  email: '',
  password: '',
};

interface emailFormProps {
  setTab: (email: boolean) => void;
}
const EmailForm = (props: emailFormProps) => {
  const { setTab } = props;
  const [email, setEmail] = useState('');
  const [submittedEmail, setSubmittedEmail] = useState(false);
  const [password, setPassword] = useState('');
  const [loading, setLoading] = useState(false);
  // eslint-disable-next-line @typescript-eslint/no-unused-vars
  const [errors, setErrors] = useState<Errors>(initialErrors);

  const toast = useToast();
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
          toast.error('Email niet gevonden');
          setLoading(false);
          return;
        }

        setSubmittedEmail(true);
        setLoading(false);
      } catch {
        toast.error('Er ging iets mis');
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
          toast.error('Ongeldige code');
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
        toast.error('Er ging iets mis');
        setLoading(false);
      }
    }
  };

  return (
    <FormHelper onSubmit={handleSubmit} noHeight gap={5}>
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
      <div className={'w-full flex items-center justify-end'}>
        <span
          onClick={() => setTab(false)}
          className={
            'text-xs text-zinc-800 mb-3 cursor-pointer hover:underline transition-all'
          }
        >
          Login met wachtwoord
        </span>
      </div>

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

EmailForm.displayName = 'EmailForm';
export default EmailForm;
