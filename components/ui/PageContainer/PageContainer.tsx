import { PageContainerProps } from '@/components/ui/PageContainer/PageContainer.types';

const PageContainer = (props: PageContainerProps) => {
  const { color, children } = props;
  return (
    <div
      className={`w-screen h-screen justify-center items-center ${color ? `bg-[${color}]` : 'bg-gray-100'}`}
    >
      {children}
    </div>
  );
};

PageContainer.displayName = 'PageContainer';
export default PageContainer;
