'use client';

import * as React from 'react';
import { CheckIcon } from '@phosphor-icons/react';
import { DropdownMenu } from 'radix-ui';
import type { IFilterOption } from './types';

interface IFilterOptionListProps {
  options: IFilterOption[];
  selectedValues: string[];
  onToggle: (value: string) => void;
}

export const FilterOptionList: React.FC<IFilterOptionListProps> = ({
  options,
  selectedValues,
  onToggle,
}) => {
  return (
    <div className="py-1.5 px-1.5 flex flex-col gap-0.5">
      {options.map((option) => {
        const isSelected = selectedValues.includes(option.value);
        return (
          <DropdownMenu.Item
            key={option.value}
            className="flex items-center justify-between gap-2 mx-0 px-3 h-9 text-sm rounded-full cursor-pointer hover:bg-gray-300/30 data-[highlighted]:bg-gray-300/30 outline-none"
            onSelect={(e) => {
              e.preventDefault();
              onToggle(option.value);
            }}
          >
            <div className="flex items-center gap-2 min-w-0">
              {option.icon && (
                <span className="text-neutral-500 shrink-0">{option.icon}</span>
              )}
              <span className="truncate">
                {typeof option.label === 'string' ? option.label : option.label}
              </span>
            </div>
            {isSelected && (
              <CheckIcon size={14} className="text-neutral-700 shrink-0" />
            )}
          </DropdownMenu.Item>
        );
      })}
    </div>
  );
};
