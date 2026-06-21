'use client';

import uFuzzy from '@leeoniya/ufuzzy';
import { CaretRightIcon, PlusIcon } from '@phosphor-icons/react';
import { DropdownMenu } from 'radix-ui';
import * as React from 'react';
import { useCallback, useMemo, useState } from 'react';

import { FilterOptionList } from './FilterOptionList';
import type { IFilterOption } from './types';

export interface IFilterCategory {
  key: string;
  label: string;
  icon?: React.ReactNode;
  options: IFilterOption[];
  selectedValues: string[];
}

export interface IFilterMenuProps {
  categories: IFilterCategory[];
  onToggleFilter: (categoryKey: string, value: string) => void;
  showSearch?: boolean;
  autofocus?: boolean;
}

function cn(...classes: (string | boolean | undefined | null)[]): string {
  return classes.filter(Boolean).join(' ');
}

export const FilterMenu: React.FC<IFilterMenuProps> = ({
  categories,
  onToggleFilter,
  showSearch = false,
  autofocus = false,
}) => {
  const [isOpen, setIsOpen] = useState(autofocus);
  const [searchQuery, setSearchQuery] = useState('');

  const handleOpenChange = useCallback((open: boolean) => {
    setIsOpen(open);
    if (!open) setSearchQuery('');
  }, []);

  const filteredCategories = useMemo(() => {
    if (!searchQuery) return categories;
    const haystack = categories.map((cat) => cat.label.toLowerCase());
    const matcher = new uFuzzy({ intraMode: 1 });
    const [idxs, info, order] = matcher.search(haystack, searchQuery);
    if (idxs == null || info == null || order == null) return [];
    return order.map((orderIdx) => categories[info.idx[orderIdx]!]!);
  }, [categories, searchQuery]);

  return (
    <DropdownMenu.Root open={isOpen} onOpenChange={handleOpenChange}>
      <DropdownMenu.Trigger asChild>
        <button
          type="button"
          className="h-8 p-1 rounded-full inline-flex justify-center items-center overflow-hidden text-sm text-zinc-700 transition-colors hover:bg-zinc-100 border border-dashed border-zinc-300 cursor-pointer hover:border-solid select-none"
        >
          <div className="w-6 h-6 flex justify-center items-center">
            <PlusIcon size={16} />
          </div>
          <div className="px-2">Filter</div>
        </button>
      </DropdownMenu.Trigger>
      <DropdownMenu.Portal>
        <DropdownMenu.Content
          className={cn(
            'z-50 bg-white min-w-56 rounded-3xl border border-zinc-200 shadow-lg overflow-hidden',
            showSearch ? 'pb-1.5' : 'py-1.5',
          )}
          sideOffset={4}
          align="start"
        >
          {showSearch && (
            <input
              type="text"
              placeholder="Filter..."
              value={searchQuery}
              onChange={(e) => setSearchQuery(e.target.value)}
              onKeyDown={(e) => {
                if (
                  e.key !== 'ArrowDown' &&
                  e.key !== 'ArrowUp' &&
                  e.key !== 'Escape'
                ) {
                  e.stopPropagation();
                }
              }}
              className="text-sm w-full py-4 px-4 focus:outline-none"
            />
          )}
          <div className="px-3 pb-2">
            <hr className="border-zinc-200" />
          </div>
          {filteredCategories.map((category) => (
            <DropdownMenu.Sub key={category.key}>
              <DropdownMenu.SubTrigger
                className="flex items-center justify-between w-full mx-1.5 px-3 h-10 text-sm text-zinc-700 rounded-full cursor-pointer hover:bg-zinc-100 outline-none data-[highlighted]:bg-zinc-100 data-[state=open]:bg-zinc-100"
                style={{ width: 'calc(100% - 12px)' }}
              >
                <div className="flex items-center gap-2">
                  {category.icon && (
                    <span className="text-zinc-500">{category.icon}</span>
                  )}
                  <span>{category.label}</span>
                </div>
                <CaretRightIcon size={14} className="text-zinc-400" />
              </DropdownMenu.SubTrigger>

              <DropdownMenu.Portal>
                <DropdownMenu.SubContent
                  className="z-50 bg-white min-w-48 rounded-3xl border border-zinc-200 shadow-lg overflow-hidden"
                  sideOffset={-4}
                  alignOffset={-46}
                >
                  <div className="flex items-center justify-between px-4 pt-4 pb-2">
                    <span className="text-xs font-semibold text-zinc-500 uppercase tracking-wide">
                      {category.label}
                    </span>
                  </div>
                  <FilterOptionList
                    options={category.options}
                    selectedValues={category.selectedValues}
                    onToggle={(value) => onToggleFilter(category.key, value)}
                  />
                </DropdownMenu.SubContent>
              </DropdownMenu.Portal>
            </DropdownMenu.Sub>
          ))}
        </DropdownMenu.Content>
      </DropdownMenu.Portal>
    </DropdownMenu.Root>
  );
};
