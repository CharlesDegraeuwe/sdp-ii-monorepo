import { AppContainerProps } from '@/components/design system/AppContainer/AppContainer.types';

const AppContainer = (props: AppContainerProps) => {
  const { children } = props;
  return (
    <div
      className={
        'relative w-full max-h-full min-h-full h-full px-20 py-15  items-center flex flex-col scroll-hidden'
      }
    >
      {children}
    </div>
  );
};

AppContainer.displayName = 'AppContainer';
export default AppContainer;
