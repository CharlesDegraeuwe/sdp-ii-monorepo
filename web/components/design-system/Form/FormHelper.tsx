'use client';
import { FormHelperProps } from '@/components/design-system/Form/FormHelper.types';

const FormHelper = (props: FormHelperProps) => {
  const { onSubmit, children, noHeight } = props;
  return (
    <form
      onSubmit={onSubmit}
      className={`relative flex flex-col gap-3 ${noHeight ? 'min-h-fit' : 'min-h-full'} w-full`}
    >
      {children}
    </form>
  );
};

FormHelper.displayName = 'FormHelper';
export default FormHelper;
