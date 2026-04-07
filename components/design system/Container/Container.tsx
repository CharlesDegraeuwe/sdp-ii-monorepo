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
    label,
    indent,
    bg,
  } = props;

  return (
    <div
      className={`flex flex-col gap-2 w-full
      bg-transparent
              ${className && className} 
            ${height ? `h-${height}` : 'h-full'}
             ${width ? `min-w-${width} w-${width}` : 'w-full'}
            `}
    >
      {(label || indent) && (
        <span className={`font-bold text-sm px-5 ${!label ? 'invisible' : ''}`}>
          {label || 'placeholder'}
        </span>
      )}
      <div
        onClick={onClick && onClick}
        className={`border border-gray-300/30 backdrop-blur-2xl rounded-4xl w-full h-full p-5 bg-gray-300/30 shadow-xl hover:border-gray-400/30 transition-all duration-300 
                  ${pointer && 'cursor-pointer'} 
                  ${bg && `bg-${bg}`} 
                
                  ${padding ? `p-${padding}` : 'p-0'}
                  ${flex ? `${flex}` : ''}
                  ${flexDirection ? `flex-${flexDirection}` : ''}
                  ${gap ? `gap-${gap}` : ''}
                  `}
      >
        {children}
      </div>
    </div>
  );
};

Container.displayName = 'Container';
export default Container;
