'use client';

import { TextAreaProps } from '@/components/design-system/TextArea/TextArea.types';
import React from 'react';

const TextArea = ({ label, id, ...props }: TextAreaProps) => {
  return (
    <div>
      {label && (
        <label htmlFor={id} className={' text-zinc-400 text-sm px-3'}>
          {label}
        </label>
      )}
      <div
        className={
          'w-full rounded-3xl flex flex-row items-center focus-within:border-gray-700/30 justify-between outline-none ring-0 border border-gray-300/30  px-5 bg-gray-300/30 shadow-inner'
        }
      >
        <textarea
          className={'w-full outline-none ring-0 py-3 resize-none'}
          id={id}
          autoComplete={'off'}
          {...props}
        />
      </div>
    </div>
  );
};

TextArea.displayName = 'TextArea';
export default TextArea;
