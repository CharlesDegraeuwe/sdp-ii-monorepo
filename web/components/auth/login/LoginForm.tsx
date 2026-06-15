'use client';
import EmailForm from '@/components/auth/login/EmailForm';
import { Suspense, useState } from 'react';
import PasswordForm from '@/components/auth/login/PasswordForm';
import Button from '../../design-system/Button/Button';
import { Label } from '@/components/design-system/Label';
import ForgotPasswordForm from '@/components/auth/login/ForgotPasswordForm';
import { IoArrowBack } from 'react-icons/io5';

const LoginForm = () => {
  const date = new Date();
  const [emailForm, setEmailForm] = useState<boolean>(true);
  const [forgotForm, setForgotForm] = useState<boolean>(false);

  return (
    <div
      className={
        'relative max-w-[50rem] w-1/2 gap-5 p-10 lg:p-40 h-full shadow-2xl flex-col flex items-center rounded-4xl bg-white justify-center'
      }
    >
      <div className={'w-fit h-fit absolute top-5 right-5'}>
        {!emailForm && (
          <Button
            onClick={() => setForgotForm((prev) => !prev)}
            label={forgotForm ? 'Terug' : 'Wachtwoord vergeten'}
            variant={'outline'}
            size={'sm'}
            iconLeft={forgotForm && <IoArrowBack />}
          />
        )}
      </div>
      <div className={'flex flex-col gap-3'}>
        <Label
          text={forgotForm ? 'Wachtwoord vergeten' : 'Log in op je account'}
          variant={'title'}
        />
      </div>
      <Suspense fallback={<div>Laden...</div>}>
        {forgotForm ? (
          <ForgotPasswordForm setForgotForm={setForgotForm} />
        ) : emailForm ? (
          <EmailForm setTab={setEmailForm} />
        ) : (
          <PasswordForm setTab={setEmailForm} />
        )}
      </Suspense>

      <span
        className={
          'absolute bottom-10 left-1/2 text-sm opacity-50 -translate-1/2'
        }
      >
        copyright {date.getFullYear()} • alle rechten voorbehouden
      </span>
    </div>
  );
};

LoginForm.displayName = 'LoginForm';
export default LoginForm;
