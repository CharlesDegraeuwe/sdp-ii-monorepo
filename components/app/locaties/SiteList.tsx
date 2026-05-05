'use client';

import React, { useState } from 'react';
import { Input } from '@/components/design-system/Input';
import { Site } from '@/types/types';
import { StatusBadge } from '@/components/app/locaties/StatusBadge';
import { FaArrowRight } from 'react-icons/fa';

export function SiteList({
  sites,
  isLoading,
  onSiteClick,
}: {
  sites: Site[];
  isLoading: boolean;
  onSiteClick: (site: Site) => void;
}) {
  const [searchQuery, setSearchQuery] = useState('');

  const filteredSites = sites.filter((site) =>
    site.naam.toLowerCase().includes(searchQuery.toLowerCase()),
  );

  return (
    <>
      <div className="p-4 border-b border-gray-100 shrink-0 bg-bg-white">
        <Input
          placeholder={'zoeken...'}
          color={'white'}
          value={searchQuery}
          onChange={(e: React.ChangeEvent<HTMLInputElement> | string) =>
            setSearchQuery(typeof e === 'string' ? e : e.target.value)
          }
        />
      </div>

      <div className="flex-1 overflow-y-auto h-full p-4 flex flex-col gap-3 bg-bg-white">
        {isLoading ? (
          <p className="text-gray-500 text-sm text-center mt-4">
            Locaties laden...
          </p>
        ) : (
          filteredSites.map((site) => (
            <div
              key={site.id}
              onClick={() => onSiteClick(site)}
              className="w-full flex group items-center cursor-pointer shadow-lg justify-between hover:bg-zinc-200 bg-zinc-200/40 px-4 py-3 border border-zinc-300/50 rounded-2xl transition-all duration-300"
            >
              <div className="flex flex-col">
                <div className="flex justify-between items-center">
                  <h3 className="font-bold text-gray-800 text-base group-hover:text-zinc-600 transition-colors">
                    {site.naam}
                  </h3>
                </div>
                <StatusBadge status={site.status} size="xs" />
              </div>
              <FaArrowRight
                className={
                  'group-hover:opacity-100 group-hover:translate-x-0 -translate-x-5 opacity-0 transition-all duration-300'
                }
              />
            </div>
          ))
        )}
      </div>
    </>
  );
}
