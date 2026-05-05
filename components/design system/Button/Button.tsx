import { ButtonProps } from './Button.types';

const textColorMap = {
  white: 'text-white',
  black: 'text-black',
} as const;

const textSizeMap = {
  sm: 'text-sm',
  md: 'text-md',
  lg: 'text-lg',
} as const;

const borderMap = {
  default:
    'border border-gray-300/30 dark:border-gray-200/30 hover:border-gray-200/30',
  red: 'border border-red-500',
} as const;

const variantMap = {
  primary: 'bg-rose-600 text-white py-3 ',
  secondary: 'bg-blue-600 text-white py-3 ',
  outline: 'border border-gray-300 text-black py-3 ',
  prompt: 'border border-zinc-300 text-zinc-500 py-2 px-2',
} as const;

const gradientMap = {
  'rose-blue': 'bg-gradient-to-r from-rose-500 to-blue-500',
  'purple-pink': 'bg-gradient-to-r from-purple-500 to-pink-500',
} as const;

const Button = ({
  borderColor,
  label,
  variant = 'primary',
  icon,
  error,
  textColor,
  textSize,
  gradient,
  loading,
  iconRight,
  iconLeft,
  px = 'px-5',
  disabled,
  onClick,
  type,
}: ButtonProps) => {
  const classes = [
    'flex items-center w-full rounded-full active:scale-95',
    'disabled:opacity-20 transition-all duration-300 cursor-pointer',
    'font-bold justify-center gap-2',
    gradient ? gradientMap[gradient] : variantMap[variant],
    textColor ? textColorMap[textColor] : '',
    textSize ? textSizeMap[textSize] : '',
    borderColor ? borderMap[borderColor] : '',
    px,
  ]
    .filter(Boolean)
    .join(' ');

  return (
    <div>
      <button
        type={type}
        disabled={disabled}
        onClick={onClick}
        className={classes}
      >
        {iconLeft}
        {icon}
        {loading ? 'laden...' : label}
        {iconRight}
      </button>

      {error && <span className="text-red-500 text-sm mt-1">{error}</span>}
    </div>
  );
};

Button.displayName = 'Button';
export default Button;
