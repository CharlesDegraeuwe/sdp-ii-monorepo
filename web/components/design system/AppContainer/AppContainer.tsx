import { AppContainerProps } from '@/components/design system/AppContainer/AppContainer.types';

const AppContainer = (props: AppContainerProps) => {
  const { children } = props;
  return (
    <div
      className={
        'relative w-full min-h-full px-4 md:px-10 lg:px-20 py-6 md:py-10 lg:py-15 items-center flex flex-col'
      }
    >
      {children}
    </div>
  );
};

AppContainer.displayName = 'AppContainer';
export default AppContainer;
