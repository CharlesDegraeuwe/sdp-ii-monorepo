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
    className,
    color,
    label,
    icon,
    error,
    style,
    ...rest
  } = props;

  return (
    <div
      onClick={onClick}
      className={`border border-gray-300/30 rounded-4xl bg-gray-300/30 shadow-xl 
        hover:border-gray-400/30 transition-all duration-300 
        ${pointer ? 'cursor-pointer' : ''} 
        ${className ?? ''}`}
      style={{
        width: width ?? '100%',
        height: height ?? '100%',
        padding: padding ?? 20,
        flex: flex ?? undefined,
        flexDirection:
          flexDirection === 'col'
            ? 'column'
            : flexDirection === 'row'
              ? 'row'
              : undefined,
        gap: gap ?? undefined,
        display: flexDirection || gap ? 'flex' : undefined,
        ...style,
      }}
      {...rest}
    >
      {children}
    </div>
  );
};

Container.displayName = 'Container';
export default Container;
