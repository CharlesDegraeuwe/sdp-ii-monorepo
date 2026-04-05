'use client';
import { FormHelperProps } from '@/components/design system/Form/FormHelper.types';

const FormHelper = (props: FormHelperProps) => {
  const { onSubmit, children } = props;
  return (
    <form onSubmit={onSubmit} className={'flex flex-col gap-5'}>
      {children}
    </form>
  );
};

FormHelper.displayName = 'FormHelper';
export default FormHelper;
