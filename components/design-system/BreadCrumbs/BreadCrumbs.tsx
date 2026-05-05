'use client';
import { useBreadCrumbs } from '@/providers/BreadCrumbProvider';
import Link from 'next/link';
import { FaChevronRight } from 'react-icons/fa';

const BreadCrumbs = () => {
  const pages = useBreadCrumbs().pages;
  return (
    <div
      className={
        'py-1 text-xs flex font-bold items-center px-3 h-fit   truncate w-fit'
      }
    >
      {pages.map((page, i) => (
        <div className="flex flex-row gap-2 items-center" key={i}>
          {i === pages.length - 1 ? (
            <div
              className={
                'first-letter:uppercase hover:underline cursor-pointer'
              }
            >
              {page}
            </div>
          ) : (
            <Link
              href={'/' + page}
              className={'first-letter:uppercase hover:underline'}
            >
              {page}
            </Link>
          )}
          {i != pages.length - 1 && (
            <FaChevronRight className={'text-zinc-400 mr-2'} />
          )}
        </div>
      ))}
    </div>
  );
};

BreadCrumbs.displayName = 'BreadCrumbs';
export default BreadCrumbs;
