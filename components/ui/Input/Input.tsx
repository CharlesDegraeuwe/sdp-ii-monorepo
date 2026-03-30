import { InputProps } from './Input.types';

const Input = ({ type, label, error, id, ...props }: InputProps) => {
  return (
    <div>
      {label && <label htmlFor={id}>{label}</label>}
      <input
        className={
          'w-full rounded-full outline-none ring-0 border border-gray-300/30 focus:border-gray-700/30 px-5 py-3 bg-gray-300/30 shadow-inner'
        }
        type={type ? type : 'text'}
        id={id}
        {...props}
      />
      {error && <span>{error}</span>}
    </div>
  );
};

Input.displayName = 'Input';

export default Input;
