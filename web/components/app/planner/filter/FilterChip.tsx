'use client';

import { DotsThreeIcon, TrashIcon, XIcon } from '@phosphor-icons/react';
import { DropdownMenu } from 'radix-ui';
import * as React from 'react';
import { useCallback, useState } from 'react';

import { FilterOptionList } from './FilterOptionList';
import type { IFilterOption } from './types';

export interface IFilterChipProps {
  icon?: React.ReactNode;
  label: string;
  options: IFilterOption[];
  selectedValues: string[];
  onSelectionChange: (values: string[]) => void;
  onDelete: () => void;
  isNegated?: boolean;
  onNegationChange?: (isNegated: boolean) => void;
  formatValue?: (values: string[], options: IFilterOption[]) => string;
}

function defaultFormatValue(
  values: string[],
  options: IFilterOption[],
): string {
  if (values.length === 0) return 'any';
  if (values.length === 1) {
    const option = options.find((o) => o.value === values[0]);
    return typeof option?.label === 'string'
      ? option.label
      : (values[0] ?? 'unknown');
  }
  return `${values.length} types`;
}

export const FilterChip: React.FC<IFilterChipProps> = ({
  icon,
  label,
  options,
  selectedValues,
  onSelectionChange,
  onDelete,
  isNegated,
  onNegationChange,
  formatValue = defaultFormatValue,
}) => {
  const [isDropdownOpen, setIsDropdownOpen] = useState(false);

  const displayText = formatValue(selectedValues, options);
  const hasMultipleValues = selectedValues.length > 1;
  const connectionWord = isNegated
    ? hasMultipleValues
      ? 'is not any of'
      : 'is not'
    : hasMultipleValues
      ? 'is any of'
      : 'is';

  const handleToggleOption = useCallback(
    (value: string) => {
      const isSelected = selectedValues.includes(value);
      if (isSelected) {
        onSelectionChange(selectedValues.filter((v) => v !== value));
      } else {
        onSelectionChange([...selectedValues, value]);
      }
    },
    [selectedValues, onSelectionChange],
  );

  return (
    <DropdownMenu.Root open={isDropdownOpen} onOpenChange={setIsDropdownOpen}>
      <div className="bg-zinc-50 rounded-full border border-zinc-200 inline-flex justify-center items-center overflow-hidden text-sm text-zinc-800">
        <div className="h-8 px-2 border-r border-zinc-200 flex justify-center items-center gap-2">
          {icon && <span className="text-zinc-700 shrink-0">{icon}</span>}
          <span className="font-semibold">{label}</span>
        </div>

        {onNegationChange ? (
          <DropdownMenu.Root>
            <DropdownMenu.Trigger asChild>
              <button
                type="button"
                className="h-8 px-2 border-r border-zinc-200 flex justify-center items-center hover:bg-zinc-100 cursor-pointer transition-colors focus:outline-none"
              >
                <span className="text-zinc-500">{connectionWord}</span>
              </button>
            </DropdownMenu.Trigger>
            <DropdownMenu.Portal>
              <DropdownMenu.Content
                className="z-50 bg-white min-w-24 rounded-3xl border border-zinc-200 p-1.5 shadow-lg"
                sideOffset={4}
                align="start"
              >
                <DropdownMenu.Item
                  className="flex items-center outline-none cursor-pointer px-4 hover:bg-zinc-100 h-10 rounded-full text-sm"
                  onClick={() => onNegationChange(false)}
                >
                  {hasMultipleValues ? 'is any of' : 'is'}
                </DropdownMenu.Item>
                <DropdownMenu.Item
                  className="flex items-center outline-none cursor-pointer px-4 hover:bg-zinc-100 h-10 rounded-full text-sm"
                  onClick={() => onNegationChange(true)}
                >
                  {hasMultipleValues ? 'is not any of' : 'is not'}
                </DropdownMenu.Item>
              </DropdownMenu.Content>
            </DropdownMenu.Portal>
          </DropdownMenu.Root>
        ) : (
          <div className="h-8 px-2 border-r border-zinc-200 flex justify-center items-center">
            <span className="text-zinc-500">{connectionWord}</span>
          </div>
        )}

        <DropdownMenu.Trigger asChild>
          <button
            type="button"
            className="h-8 px-2 border-r border-zinc-200 flex justify-center items-center hover:bg-zinc-100 cursor-pointer transition-colors focus:outline-none"
          >
            <span>{displayText}</span>
          </button>
        </DropdownMenu.Trigger>

        <button
          type="button"
          aria-label={`Verwijder ${label} filter`}
          className="w-8 h-8 flex justify-center items-center hover:bg-zinc-100 transition-colors cursor-pointer"
          onClick={onDelete}
        >
          <XIcon size={16} />
        </button>
      </div>

      <DropdownMenu.Portal>
        <DropdownMenu.Content
          className="z-50 bg-white min-w-52 rounded-3xl border border-zinc-200 shadow-lg overflow-hidden"
          sideOffset={4}
          align="start"
        >
          <div className="flex items-center justify-between px-4 pt-4 pb-2">
            <span className="text-xs font-semibold text-zinc-500 uppercase tracking-wide">
              {label}
            </span>
            <DropdownMenu.Sub>
              <DropdownMenu.SubTrigger asChild>
                <button
                  type="button"
                  className="p-2 rounded-3xl hover:bg-zinc-100 data-[state=open]:bg-zinc-100 text-zinc-400 hover:text-zinc-600 transition-colors cursor-pointer outline-none"
                >
                  <DotsThreeIcon size={16} weight="bold" />
                </button>
              </DropdownMenu.SubTrigger>
              <DropdownMenu.Portal>
                <DropdownMenu.SubContent
                  className="z-50 bg-white min-w-36 rounded-3xl border border-zinc-200 p-1.5 shadow-lg"
                  sideOffset={4}
                  alignOffset={-4}
                >
                  <DropdownMenu.Item
                    className="flex items-center gap-2 outline-none cursor-pointer px-4 hover:bg-zinc-100 data-[highlighted]:bg-zinc-100 h-10 rounded-full text-sm"
                    onSelect={() => {
                      onDelete();
                      setIsDropdownOpen(false);
                    }}
                  >
                    <TrashIcon size={14} />
                    Filter verwijderen
                  </DropdownMenu.Item>
                </DropdownMenu.SubContent>
              </DropdownMenu.Portal>
            </DropdownMenu.Sub>
          </div>
          <FilterOptionList
            options={options}
            selectedValues={selectedValues}
            onToggle={handleToggleOption}
          />
        </DropdownMenu.Content>
      </DropdownMenu.Portal>
    </DropdownMenu.Root>
  );
};

FilterChip.displayName = 'FilterChip';
