'use client';
import FormHelper from '@/components/design system/Form/FormHelper';
import { Input } from '@/components/design system/Input';
import { Button } from '@/components/design system/Button';
import { useState } from 'react';
import { signIn } from 'next-auth/react';
import { useSearchParams } from 'next/navigation';
import { useRouter } from 'next/navigation';

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
  const [password, setPassword] = useState('');
  const [loading, setLoading] = useState(false);
  const [errors, setErrors] = useState<Errors>(initialErrors);

  const router = useRouter();
  const searchParams = useSearchParams();
  const callbackUrl = searchParams.get('callbackUrl') || `/overzicht`;

  //login functie
  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setErrors(initialErrors);
    setLoading(true);

    try {
      const result = await signIn('credentials', {
        email,
        password,
        redirect: false,
      });
      console.log('signIn result:', JSON.stringify(result));

      if (result?.error) {
        setErrors({ email: 'Ongeldige email', password: '' });
        setLoading(false);
      } else {
        router.push(callbackUrl);
      }
    } catch (err) {
      console.error('Login error:', err);
      setErrors({ email: 'Ongeldige email', password: '' });
      setLoading(false);
    }
  };

  return (
    <FormHelper onSubmit={handleSubmit}>
      <Input
        type="text"
        placeholder="email..."
        error={errors.email}
        onChange={(e) => setEmail(e.target.value)}
      />

      <Input
        type="password"
        placeholder="wachtwoord..."
        error={errors.password}
        onChange={(e) => setPassword(e.target.value)}
      />

      <Button
        gradient={true}
        from={'rose-600'}
        to={'rose-500'}
        textColor={'white'}
        type="submit"
        label="Login"
        loading={loading}
      />
    </FormHelper>
  );
};

LoginForm.displayName = 'LoginForm';
export default LoginForm;
