import { ButtonProps } from './Button.types';
const Button = (props: ButtonProps) => {
  const {
    color,
    label,
    icon,
    error,
    textColor,
    gradient,
    from,
    to,
    loading,
    absolute,
  } = props;
  const bgClass = gradient ? '' : `bg-${color ?? 'rose-600'}`;
  const gradientClasses = gradient
    ? `bg-linear-to-r from-${from} to-${to}`
    : '';

  return (
    <div>
      <button
        className={`flex items-center w-full rounded-full active:scale-97 border border-gray-300/30 dark:border-200/30 hover:border-200/30 transition-all duration-300 cursor-pointer px-5 py-3 font-bold justify-center gap-2
                ${bgClass} ${gradientClasses} ${textColor ? `text-${textColor}` : ''}
        `}
        onClick={props.onClick}
      >
        {icon && icon}
        {loading ? 'laden...' : label}
      </button>
      {error && <span>{error}</span>}
    </div>
  );
};
Button.displayName = 'Button';

export default Button;
