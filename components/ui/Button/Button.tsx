import { ButtonProps } from './Button.types';

const Button = (props: ButtonProps) => {
  const { children, color, label, icon, error, onClick } = props;

  return (
    <div>
      <button
        className={`flex items-center justify-center gap-2  ${color}`}
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
