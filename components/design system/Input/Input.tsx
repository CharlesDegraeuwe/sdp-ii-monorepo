'use client';
import { InputProps } from './Input.types';
import React from 'react';
import { FaRegEye, FaRegEyeSlash } from 'react-icons/fa';

const Input = ({
  type,
  label,
  error,
  errorOption,
  id,
  ...props
}: InputProps) => {
  const [show, setShow] = React.useState(false);
  const toggleShow = () => setShow((prev) => !prev);
  if (type === 'password') {
    return (
      <div>
        {label && (
          <label htmlFor={id} className={' text-zinc-400 text-sm px-3'}>
            {label}
          </label>
        )}
        <div
          className={
            'w-full rounded-full flex flex-row items-center focus-within:border-gray-700/30 justify-between outline-none ring-0 border border-gray-300/30  px-5 bg-gray-300/30 shadow-inner'
          }
        >
          <input
            className={'w-full outline-none ring-0 py-3'}
            type={show ? 'text' : 'password'}
            id={id}
            autoComplete={'off'}
            {...props}
          />
          {show ? (
            <FaRegEyeSlash opacity={0.5} onClick={toggleShow} />
          ) : (
            <FaRegEye opacity={0.5} onClick={toggleShow} />
          )}
        </div>
        {errorOption && (
          <div className={'w-full h-7 items-end flex'}>
            {error && (
              <span className={'text-rose-600 text-sm w-fit max-w-1/2'}>
                {error}
              </span>
            )}
          </div>
        )}
      </div>
    );
  }
  return (
    <div>
      {label && (
        <label htmlFor={id} className={' text-zinc-400 text-sm px-3'}>
          {label}
        </label>
      )}
      <input
        className={
          'w-full rounded-full outline-none ring-0 border border-gray-300/30 focus:border-gray-700/30 px-5 py-3 bg-gray-300/30 shadow-inner'
        }
        autoComplete={'off'}
        type={type ? type : 'text'}
        id={id}
        {...props}
      />
      <div className={'w-full h-7 items-end flex'}>
        {errorOption && (
          <div className={'w-full h-7 items-end flex'}>
            {error && (
              <span className={'text-rose-600 text-sm w-fit max-w-1/2'}>
                {error}
              </span>
            )}
          </div>
        )}
      </div>
    </div>
  );
};

Input.displayName = 'Input';

export default Input;
