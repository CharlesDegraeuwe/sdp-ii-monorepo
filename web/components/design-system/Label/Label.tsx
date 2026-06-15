import { LabelProps } from '@/components/design-system/Label/Label.types';

const variantStyles = {
  title: 'text-2xl font-bold text-gray-900',
  subtitle: 'text-lg font-medium text-gray-600',
  body: 'text-base text-gray-800',
  caption: 'text-sm text-gray-500',
  emptystate: 'text-gray-400 text-sm text-center',
  inputLabel: 'text-sm text-gray-400 text-base',
};

const Label = (props: LabelProps) => {
  const { text, variant = 'body', icon, center, verplicht } = props;

  const className = [
    variantStyles[variant],
    center && 'flex justify-center items-center text-center',
  ]
    .filter(Boolean)
    .join(' ');

  return (
    <div className={className}>
      {icon}{' '}
      <span>
        {text}
        {verplicht && <span className={'text-red-600'}>*</span>}
      </span>
    </div>
  );
};

export default Label;
