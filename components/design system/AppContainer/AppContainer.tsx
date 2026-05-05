import { AppContainerProps } from '@/components/design system/AppContainer/AppContainer.types';

const AppContainer = (props: AppContainerProps) => {
  const { children, ai, px, py } = props;
  return (
    <div
      className={`relative w-full max-h-full min-h-full h-full ${px ? px : 'px-20'} ${py ? py : 'py-15'} items-center flex flex-col scroll-hidden`}
    >
      {children}
    </div>
  );
};

AppContainer.displayName = 'AppContainer';
export default AppContainer;
