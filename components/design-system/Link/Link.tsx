'use client';

import { useState } from 'react';
import NextLink from 'next/link';
import { LinkProps } from '@/components/design-system/Link/Link.types';
import { FaArrowRight } from 'react-icons/fa';

const Link = ({ href, icon, label, rounded }: LinkProps) => {
  const [hovered, setHovered] = useState(false);

  return (
    <NextLink href={href} className={'w-full group'}>
      <div
        onMouseEnter={() => setHovered(true)}
        onMouseLeave={() => setHovered(false)}
        className={`w-full flex items-center justify-between px-4 py-3 border transition-all duration-300
          ${hovered ? 'bg-zinc-200 border-zinc-900' : 'bg-zinc-200/40 border-zinc-200'}
          ${rounded ? 'rounded-' + rounded : 'rounded-2xl'}`}
      >
        <div className={'flex w-full flex-row gap-2 items-center'}>
          {icon} <span>{label}</span>
        </div>
        <FaArrowRight
          className={`transition-all duration-300 ${hovered ? 'opacity-100 translate-x-0' : 'opacity-0 -translate-x-5'}`}
        />
      </div>
    </NextLink>
  );
};

export default Link;
