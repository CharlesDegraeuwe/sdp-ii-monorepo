'use client';
import { usePathname } from 'next/navigation';
import { MdOutlineCalendarMonth } from 'react-icons/md';

const PageHeader = () => {
  const page = usePathname();
  const date = new Date().toLocaleDateString('nl-BE');
  const datum = date
    .split('/')
    .map((part) => (part.length === 1 ? '0' + part : part))
    .join('/');
  return (
    <div
      className={`z-[999] absolute gap-4 rounded-full py-1 backdrop-blur-2xl pl-5 top-9 left-5 flex flex-row justify-center items-center`}
    >
      <span className={'text-2xl font-bold first-letter:uppercase'}>
        {page.split('').splice(1, page.length).join('')}
      </span>
      <div
        className={
          'flex items-center bg-zinc-100 gap-2 border border-zinc-200 rounded-full px-5 py-1.5'
        }
      >
        <MdOutlineCalendarMonth />
        <span>{datum}</span>
      </div>
    </div>
  );
};

PageHeader.displayName = 'PageHeader';
export default PageHeader;
