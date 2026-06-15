'use client';
import React from 'react';
import { FaChevronDown } from 'react-icons/fa';
import { SelectProps } from '@/components/design-system/Select/Select.types';

const sizeMap = {
  sm: {
    trigger: 'px-3 py-1.5 text-sm',
    chevron: 12,
    item: 'p-1.5 text-xs',
    label: 'text-xs px-2',
  },
  md: {
    trigger: 'px-4 py-2 text-base',
    chevron: 14,
    item: 'p-2 text-sm',
    label: 'text-xs px-2',
  },
  lg: {
    trigger: 'px-5 py-3 text-base',
    chevron: 14,
    item: 'p-2 text-sm',
    label: 'text-sm px-3',
  },
  xl: {
    trigger: 'px-6 py-4 text-lg',
    chevron: 16,
    item: 'p-3 text-base',
    label: 'text-sm px-3',
  },
} as const;

const Select = ({
  options,
  value,
  onChange,
  placeholder = 'Selecteer een optie',
  label,
  error,
  errorOption,
  id,
  disabled,
  size = 'lg',
}: SelectProps) => {
  const [open, setOpen] = React.useState(false);
  const containerRef = React.useRef<HTMLDivElement>(null);

  const selected = options.find((o) => String(o.value) === String(value));
  const s = sizeMap[size];

  React.useEffect(() => {
    const handleClickOutside = (e: MouseEvent) => {
      if (
        containerRef.current &&
        !containerRef.current.contains(e.target as Node)
      ) {
        setOpen(false);
      }
    };
    const handleEscape = (e: KeyboardEvent) => {
      if (e.key === 'Escape') setOpen(false);
    };
    if (open) {
      document.addEventListener('mousedown', handleClickOutside);
      document.addEventListener('keydown', handleEscape);
    }
    return () => {
      document.removeEventListener('mousedown', handleClickOutside);
      document.removeEventListener('keydown', handleEscape);
    };
  }, [open]);

  const handleSelect = (optionValue: string | number) => {
    onChange?.(optionValue);
    setOpen(false);
  };

  return (
    <div ref={containerRef} className={'relative'}>
      {label && (
        <label htmlFor={id} className={`text-zinc-400 ${s.label}`}>
          {label}
        </label>
      )}
      <button
        type={'button'}
        id={id}
        disabled={disabled}
        onClick={() => !disabled && setOpen((prev) => !prev)}
        className={`w-full rounded-full flex flex-row items-center justify-between outline-none ring-0 border border-gray-300/30 focus:border-gray-700/30 bg-gray-300/30 shadow-inner text-left disabled:opacity-50 disabled:cursor-not-allowed ${s.trigger}`}
      >
        <span className={selected ? '' : 'text-zinc-400'}>
          {selected ? selected.label : placeholder}
        </span>
        <FaChevronDown
          size={s.chevron}
          opacity={0.5}
          className={`transition-transform duration-200 ${
            open ? 'rotate-180' : ''
          }`}
        />
      </button>

      <div
        className={`absolute left-0 right-0 top-full mt-2
          z-[9999] p-3 border border-zinc-300
          rounded-2xl text-black
          bg-zinc-100 gap-2 flex flex-col h-fit
          shadow-xl shadow-black/10 dark:shadow-black/30
          transition-all duration-300 ease-out origin-top
          ${
            open
              ? 'opacity-100 scale-100 translate-y-0 visible pointer-events-auto'
              : 'opacity-0 scale-95 -translate-y-2 invisible pointer-events-none'
          }
        `}
        onClick={(e) => e.stopPropagation()}
      >
        <div className={'w-full flex flex-col gap-1'}>
          <span className={`w-full text-zinc-400 ${s.label}`}>
            {label ?? 'Opties'}
          </span>
          <div
            className={'w-full h-fit flex flex-col max-h-60 overflow-y-auto'}
          >
            {options.map((option) => {
              const isSelected = String(option.value) === String(value);
              return (
                <button
                  key={String(option.value)}
                  type={'button'}
                  onClick={() => handleSelect(option.value)}
                  className={`w-full hover:bg-zinc-400/20 rounded-lg flex flex-row items-center justify-between cursor-pointer ${
                    s.item
                  } ${isSelected ? 'bg-zinc-400/20' : ''}`}
                >
                  <span>{option.label}</span>
                  {isSelected && (
                    <span className={'text-xs text-zinc-500'}>✓</span>
                  )}
                </button>
              );
            })}
          </div>
        </div>
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
};

Select.displayName = 'Select';

export default Select;
