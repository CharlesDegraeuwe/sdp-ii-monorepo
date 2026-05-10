'use client';
import { useLayoutEffect, useRef, useState } from 'react';
import { TabSwitcherProps } from '@/components/design-system/TabSwitcher/TabSwitcher.types';

const SIZE_STYLES = {
  sm: {
    button: 'px-3 py-1 text-xs',
    padding: 'p-0.5',
    inset: 'top-0.5 bottom-0.5',
  },
  md: {
    button: 'px-5 py-2 text-sm',
    padding: 'p-1',
    inset: 'top-1 bottom-1',
  },
  lg: {
    button: 'px-6 py-2.5 text-base',
    padding: 'p-1',
    inset: 'top-1 bottom-1',
  },
  xl: {
    button: 'px-8 py-3 text-lg',
    padding: 'p-1.5',
    inset: 'top-1.5 bottom-1.5',
  },
} as const;

export const TabSwitcher = <T extends string = string>({
  tabs,
  value,
  onChange,
  size = 'md',
}: TabSwitcherProps<T>) => {
  const containerRef = useRef<HTMLDivElement>(null);
  const buttonRefs = useRef<Record<string, HTMLButtonElement | null>>({});
  const [indicator, setIndicator] = useState<{
    left: number;
    width: number;
  } | null>(null);
  const hasAnimated = useRef(false);

  const styles = SIZE_STYLES[size];

  useLayoutEffect(() => {
    const measure = () => {
      const activeBtn = buttonRefs.current[value];
      const container = containerRef.current;
      if (!activeBtn || !container) return;

      const containerRect = container.getBoundingClientRect();
      const btnRect = activeBtn.getBoundingClientRect();

      setIndicator({
        left: btnRect.left - containerRect.left,
        width: btnRect.width,
      });
    };

    measure();

    if (document.fonts?.status !== 'loaded') {
      document.fonts?.ready.then(measure);
    }

    const ro = new ResizeObserver(measure);
    if (containerRef.current) ro.observe(containerRef.current);

    return () => ro.disconnect();
  }, [value, tabs, size]);

  useLayoutEffect(() => {
    if (indicator && !hasAnimated.current) {
      requestAnimationFrame(() => {
        hasAnimated.current = true;
      });
    }
  }, [indicator]);

  return (
    <div className="flex justify-center">
      <div
        ref={containerRef}
        className={`relative flex gap-1 bg-gray-300/30 border border-gray-300/30 rounded-full shadow-sm ${styles.padding}`}
      >
        {indicator && (
          <div
            className={`absolute bg-zinc-900 rounded-full shadow ease-out ${styles.inset}`}
            style={{
              left: `${indicator.left}px`,
              width: `${indicator.width}px`,
              // eslint-disable-next-line react-hooks/refs
              transition: hasAnimated.current
                ? 'left 300ms ease-out, width 300ms ease-out'
                : 'none',
            }}
          />
        )}

        {tabs.map((t) => (
          <button
            key={t.key}
            ref={(el) => {
              buttonRefs.current[t.key] = el;
            }}
            onClick={() => onChange(t.key)}
            className={`relative z-10 rounded-full font-bold transition-colors duration-300 cursor-pointer ${styles.button} ${
              value === t.key
                ? 'text-white'
                : 'text-zinc-500 hover:text-zinc-800'
            }`}
          >
            {t.label}
          </button>
        ))}
      </div>
    </div>
  );
};
