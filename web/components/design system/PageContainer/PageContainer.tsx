import { PageContainerProps } from '@/components/design system/PageContainer/PageContainer.types';
import PageHeader from '../PageHeader/PageHeader';

const PageContainer = (props: PageContainerProps) => {
  const { color, height, children, padding } = props;
  return (
    <>
      <main className="w-full flex-1 border border-zinc-300 overflow-y-auto scroll-hidden rounded-3xl">
        <div
          className={`min-h-full w-full relative flex justify-center items-start
                              ${color ? `bg-[${color}]` : 'bg-bg-white'}
                              ${padding ? `p-${padding}` : ''}
                              ${height ? `min-h-${padding}` : ''}
                              `}
        >
          {children}
        </div>
      </main>
    </>
  );
};

PageContainer.displayName = 'PageContainer';
export default PageContainer;
