import { InputProps } from './Input.types';

const Input = ({ label, error, id, ...props }: InputProps) => {
  return (
    <div>
      {label && <label htmlFor={id}>{label}</label>}
      <input id={id} {...props} />
      {error && <span>{error}</span>}
    </div>
  );
};

Input.displayName = 'Input';

export default Input;
