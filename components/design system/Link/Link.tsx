import NextLink from 'next/link';
import { LinkProps } from '@/components/design system/Link/Link.types';
import { FaArrowRight } from 'react-icons/fa';

const Link = ({ href, icon, label, rounded }: LinkProps) => {
  return (
    <NextLink href={href} className={'w-full group'}>
      <div
        className={`w-full flex items-center justify-between hover:bg-zinc-200 bg-zinc-200/40 px-4 py-3 border border-zinc-300/50 ${rounded ? 'rounded-' + rounded : 'rounded-2xl'} transition-all duration-300`}
      >
        <div className={'flex w-full flex-row gap-2 items-center'}>
          {icon} <span>{label}</span>
        </div>
        <FaArrowRight
          className={
            'group-hover:opacity-100 group-hover:translate-x-0 -translate-x-5 opacity-0 transition-all duration-300'
          }
        />
      </div>
    </NextLink>
  );
};

export default Link;
