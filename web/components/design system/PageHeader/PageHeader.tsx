'use client';
import { usePathname } from 'next/navigation';
import { MdOutlineCalendarMonth } from 'react-icons/md';
import Link from 'next/link';

const PageHeader = () => {
  const page = usePathname();
  const pageTitle = page.split('').splice(1, page.length).join('');

  const date = new Date().toLocaleDateString('nl-BE');
  const datum = date
    .split('/')
    .map((part) => (part.length === 1 ? '0' + part : part))
    .join('/');
  return (
    <div
      className={`z-[999] absolute gap-4 rounded-full py-1 pr-1 backdrop-blur-2xl pl-5 ${pageTitle != 'planner' && 'pr-5'} top-5 md:top-9 left-4 lg:left-20 flex flex-row justify-center items-center`}
    >
      <span className={'text-xl md:text-2xl font-bold first-letter:uppercase'}>
        {pageTitle}
      </span>
      {pageTitle === 'planner' && (
        <Link
          href={'/planner'}
          className={
            'flex items-center bg-zinc-100 gap-2 border border-zinc-200 rounded-full px-4 md:px-5 py-1.5'
          }
        >
          <MdOutlineCalendarMonth />
          <span>{datum}</span>
        </Link>
      )}
    </div>
  );
};

PageHeader.displayName = 'PageHeader';
export default PageHeader;
