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
import { useSplash } from '@/providers/SplashProvider';

interface passwordProps {
  setTab: (email: boolean) => void;
}

const PasswordForm = (props: passwordProps) => {
  const { setTab } = props;
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [loading, setLoading] = useState(false);

  const { splashOpen, setSplashOpen } = useSplash();
  const toast = useToast();
  const router = useRouter();
  const searchParams = useSearchParams();
  const callbackUrl = searchParams.get('callbackUrl') || `/overzicht`;

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setLoading(true);

    try {
      const result = await signIn('password', {
        email,
        wachtwoord: password,
        redirect: false,
      });

      if (result?.error) {
        toast.error('Ongeldig e-mailadres of wachtwoord');
        setLoading(false);
      } else {
        const session = await getSession();
        if (session?.user?.status !== 'Actief') {
          router.push('/activeer');
          return;
        }
        setSplashOpen(true);
        router.push(callbackUrl);
      }
    } catch {
      toast.error('Er ging iets mis');
      setLoading(false);
    }
  };

  return (
    <FormHelper onSubmit={handleSubmit} noHeight gap={5}>
      <Input
        type="text"
        placeholder="email..."
        onChange={(e) => setEmail(e.target.value)}
      />
      <AnimateOnMount className={'flex flex-col gap-3'}>
        <Input
          type="password"
          placeholder="wachtwoord..."
          onChange={(e) => setPassword(e.target.value)}
        />
      </AnimateOnMount>
      <div className={'w-full flex items-center justify-end'}>
        <span
          onClick={() => setTab(true)}
          className={
            'text-xs text-zinc-800 mb-3 cursor-pointer hover:underline transition-all'
          }
        >
          Login met token
        </span>
      </div>
      <Button
        color={'delaware_red'}
        textColor={'white'}
        type="submit"
        label={'Login'}
        iconRight={<FaArrowRight />}
        loading={loading}
      />
    </FormHelper>
  );
};

PasswordForm.displayName = 'PasswordForm';
export default PasswordForm;
