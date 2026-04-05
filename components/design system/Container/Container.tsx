import { ContainerProps } from '@/components/design system/Container/Container.types';

const Container = (props: ContainerProps) => {
  const {
    children,
    onClick,
    width,
    height,
    pointer,
    padding,
    gap,
    flex,
    flexDirection,
  } = props;

  return (
    <div
      onClick={onClick && onClick}
      className={`border border-gray-300/30 rounded-4xl p-5 bg-gray-300/30 shadow-xl hover:border-gray-400/30 transition-all duration-300 
      ${pointer && 'cursor-pointer'} 
      ${width ? `min-w-${width} w-${width}` : 'w-full'} 
      ${height ? `h-${height}` : 'h-full'}
      ${padding ? `p-${padding}` : 'p-0'}
      ${flex ? `${flex}` : ''}
      ${flexDirection ? `flex-${flexDirection}` : ''}
      ${gap ? `gap-${gap}` : ''}
      `}
    >
      {children}
    </div>
  );
};

Container.displayName = 'Container';
export default Container;
