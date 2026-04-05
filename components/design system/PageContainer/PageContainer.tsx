import { PageContainerProps } from '@/components/design system/PageContainer/PageContainer.types';

const PageContainer = (props: PageContainerProps) => {
  const { color, children } = props;
  return (
    <div
      className={`min-w-screen min-h-screen w-screen h-screen flex justify-center items-center ${color ? `bg-[${color}]` : 'bg-gray-100'}`}
    >
      {children}
    </div>
  );
};

PageContainer.displayName = 'PageContainer';
export default PageContainer;
