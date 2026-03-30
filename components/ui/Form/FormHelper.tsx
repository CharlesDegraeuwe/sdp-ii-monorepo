'use client';
import { FormHelperProps } from '@/components/ui/Form/FormHelper.types';

const FormHelper = (props: FormHelperProps) => {
  const { onSubmit, children, gap } = props;
  return (
    <form action="" onSubmit={onSubmit} className={'flex flex-col gap-5'}>
      {children}
    </form>
  );
};

FormHelper.displayName = 'FormHelper';
export default FormHelper;
