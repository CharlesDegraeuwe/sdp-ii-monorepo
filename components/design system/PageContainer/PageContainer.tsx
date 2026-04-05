import { PageContainerProps } from '@/components/design system/PageContainer/PageContainer.types';

const PageContainer = (props: PageContainerProps) => {
  const { color, height, children, padding } = props;
  return (
    <div
      className={`min-w-full min-h-full relative w-full h-full flex justify-center items-center 
      ${color ? `bg-[${color}]` : 'bg-bg-white'}
      ${padding ? `p-${padding}` : ''}
      ${height ? `min-h-${padding}` : 'h-full'}
      `}
    >
      {children}
    </div>
  );
};

PageContainer.displayName = 'PageContainer';
export default PageContainer;
