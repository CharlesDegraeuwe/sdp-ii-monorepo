'use client';
import FormHelper from '@/components/ui/Form/FormHelper';
import { Input } from '@/components/ui/Input';
import { Button } from '@/components/ui/Button';

const LoginForm = () => {
  const handleSubmit = () => {};
  return (
    <FormHelper onSubmit={handleSubmit}>
      <Input type="password" placeholder="email..." />

      <Input placeholder="password..." />

      <Button
        gradient={true}
        from={'rose-600'}
        to={'red-600'}
        textColor={'white'}
        type="submit"
        label="Login"
      />
    </FormHelper>
  );
};

LoginForm.displayName = 'LoginForm';
export default LoginForm;
