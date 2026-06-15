import { AppContainerProps } from '@/components/design-system/AppContainer/AppContainer.types';

const AppContainer = (props: AppContainerProps) => {
  const { children, px, py } = props;
  return (
    <div
      className={`relative w-full h-full min-h-full ${px ? px : 'px-4 sm:px-8 lg:px-20'} ${py ? py : 'py-6 lg:py-15'} items-center flex flex-col overflow-x-hidden scroll-hidden`}
    >
      {children}
    </div>
  );
};

AppContainer.displayName = 'AppContainer';
export default AppContainer;
