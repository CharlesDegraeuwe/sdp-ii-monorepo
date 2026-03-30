import { ButtonProps } from './Button.types';

const colorMap: Record<string, string> = {
  'rose-600': 'bg-rose-600',
  'blue-500': 'bg-blue-500',
  'green-400': 'bg-green-400',
  // voeg toe wat je nodig hebt
};

const textColorMap: Record<string, string> = {
  white: 'text-white',
  'gray-900': 'text-gray-900',
};

const fromMap: Record<string, string> = {
  'rose-600': 'from-rose-600',
  'purple-500': 'from-purple-500',
};

const toMap: Record<string, string> = {
  'blue-500': 'to-blue-500',
  'pink-400': 'to-pink-400',
};

const Button = (props: ButtonProps) => {
  const {
    children,
    color,
    label,
    icon,
    error,
    onClick,
    textColor,
    gradient,
    from,
    to,
  } = props;

  return (
    <div>
      <button
        className={`flex items-center w-full rounded-full active:scale-97 border border-gray-300/30 dark:border-200/30 hover:border-200/30 transition-all duration-300 cursor-pointer px-5 py-3 font-bold justify-center gap-2
       ${color ? colorMap[color] : 'bg-rose-600'}
      ${textColor ? textColorMap[textColor] : ''}
      ${gradient ? 'bg-linear-90' : ''}
      ${gradient && from ? fromMap[from] : ''}
      ${gradient && to ? toMap[to] : ''}
        `}
        onClick={props.onClick}
      >
        {icon && icon}
        {label}
      </button>
      {error && <span>{error}</span>}
    </div>
  );
};

Button.displayName = 'Button';

export default Button;
