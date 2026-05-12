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

const sizeMap = {
  xs: 'py-1 px-2 text-xs',
  sm: 'py-1.5 px-3 text-sm',
  md: 'py-2 px-4 text-base',
  lg: 'py-3 px-5 text-base',
  xl: 'py-4 px-6 text-lg',
  icon: 'p-2 aspect-square',
} as const;

const variantMap = {
  primary: 'bg-rose-600 text-white',
  secondary: 'bg-blue-600 text-white',
  outline: 'border border-gray-300 text-black',
  prompt: 'border border-zinc-300 text-zinc-500',
  ghost: 'text-zinc-500 hover:bg-gray-200 transition-colors duration-200',
  approve: 'text-white bg-emerald-500',
  submit:
    'bg-rose-500 text-white p-0 min-h-full rounded-xl max-w-fit disabled:cursor-not-allowed disabled:active:scale-100',
} as const;

const gradientMap = {
  'rose-blue': 'bg-gradient-to-r from-rose-500 to-blue-500',
  'purple-pink': 'bg-gradient-to-r from-purple-500 to-pink-500',
} as const;

const Button = ({
  borderColor,
  label,
  variant = 'primary',
  size = 'lg',
  icon,
  error,
  textColor,
  textSize,
  gradient,
  loading,
  iconRight,
  iconLeft,
  px,
  disabled,
  onClick,
  type,
}: ButtonProps) => {
  const classes = [
    'flex items-center w-full rounded-full active:scale-95',
    'disabled:opacity-20 transition-all duration-300 cursor-pointer',
    'font-bold justify-center gap-2',
    gradient ? gradientMap[gradient] : variantMap[variant],
    sizeMap[size],
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
