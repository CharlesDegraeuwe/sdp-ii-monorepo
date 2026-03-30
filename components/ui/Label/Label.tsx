import { LabelProps } from '@/components/ui/Label/Label.types';

const Label = (props: LabelProps) => {
  const { text, className, size, weight, color, children, icon, p, px, py } =
    props;
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
