import { LabelProps } from '@/components/design system/Label/Label.types';

const Label = (props: LabelProps) => {
  const { text, size, weight, color, icon, p, px, py } = props;
  return (
    <span
      className={`flex-1 
                ${size ? `text-${size}` : 'text-lg'} 
                ${weight ? `font-${weight}` : 'font-bold'}
                ${color ? `text-${color}` : 'text-gray-900'}
                ${p ? `p-${p}` : ''}
                ${px ? `px-${px}` : ''}
                ${py ? `py-${py}` : ''}
                `}
    >
      {icon} {text}
    </span>
  );
};

Label.displayName = 'Label';
export default Label;
