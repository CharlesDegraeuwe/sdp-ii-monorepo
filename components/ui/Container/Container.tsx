import { ContainerProps } from '@/components/ui/Container/Container.types';

const Container = (props: ContainerProps) => {
  const { children, onClick } = props;

  return (
    <div
      onClick={onClick && onClick}
      className={'border border-gray-300/30 rounded-4xl p-5 bg-gray-300/30'}
    >
      {children}
    </div>
  );
};

Container.displayName = 'Container';
export default Container;
