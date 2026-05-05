import { PageContainerProps } from '@/components/design-system/PageContainer/PageContainer.types';
import PageHeader from '../PageHeader/PageHeader';

const PageContainer = (props: PageContainerProps) => {
  const { color, height, children, padding } = props;
  return (
    <>
      <main className="w-full flex-1 border border-zinc-300 overflow-hidden scroll-hidden rounded-3xl">
        <div
          className={`min-w-full min-h-full relative w-full h-full flex justify-center items-center 
                              ${color ? `bg-[${color}]` : 'bg-bg-white'}
                              ${padding ? `p-${padding}` : ''}
                              ${height ? `min-h-${padding}` : 'h-full'}
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
