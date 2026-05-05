import { PageSectionProps } from '@/components/design-system/PageSection/PageSection.types';

const PageSection = (props: PageSectionProps) => {
  const { children, title, icon, danger } = props;
  return (
    <div
      className={`w-full rounded-3xl p-5 flex flex-col gap-3 border ${danger ? 'border-rose-700 text-rose-700 bg-rose-300/20' : 'border-zinc-600/30'}`}
    >
      <div className={'flex flex-row gap-3 items-center w-full'}>
        {icon}
        <span
          className={`font-bold text-lg first-letter:uppercase ${danger && 'text-rose-700'}`}
        >
          {title}:
        </span>
      </div>
      {children}
    </div>
  );
};

PageSection.displayName = 'PageSection';
export default PageSection;
