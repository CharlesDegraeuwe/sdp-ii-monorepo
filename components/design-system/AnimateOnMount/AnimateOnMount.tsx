'use client';
import { useEffect, useState } from 'react';
import { AnimateOnMountProps } from '@/components/design-system/AnimateOnMount/AnimateOnMount.types';

const AnimateOnMount = ({
  children,
  delay = 0,
  className = '',
}: AnimateOnMountProps) => {
  const [mounted, setMounted] = useState(false);

  useEffect(() => {
    const timer = setTimeout(() => setMounted(true), delay);
    return () => clearTimeout(timer);
  }, [delay]);

  return (
    <div
      className={`transition-all duration-700 ${mounted ? 'opacity-100 translate-y-0' : 'opacity-0 -translate-y-4'} ${className}`}
    >
      {children}
    </div>
  );
};

AnimateOnMount.displayName = 'AnimateOnMount';
export default AnimateOnMount;
