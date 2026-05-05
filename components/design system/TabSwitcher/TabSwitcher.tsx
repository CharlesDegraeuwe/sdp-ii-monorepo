'use client';
import { useLayoutEffect, useRef, useState } from 'react';
import { TabSwitcherProps } from '@/components/design system/TabSwitcher/TabSwitcher.types';

export const TabSwitcher = <T extends string = string>({
  tabs,
  value,
  onChange,
}: TabSwitcherProps<T>) => {
  const containerRef = useRef<HTMLDivElement>(null);
  const buttonRefs = useRef<Record<string, HTMLButtonElement | null>>({});
  const [indicator, setIndicator] = useState<{
    left: number;
    width: number;
  } | null>(null);
  const hasAnimated = useRef(false);

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

    // re-meten na fonts geladen, lost shifts op door late font load
    if (document.fonts?.status !== 'loaded') {
      document.fonts?.ready.then(measure);
    }

    // re-meten bij resize van container
    const ro = new ResizeObserver(measure);
    if (containerRef.current) ro.observe(containerRef.current);

    return () => ro.disconnect();
  }, [value, tabs]);

  // pas na eerste paint mag er gesliedd worden
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
        className="relative flex gap-1 bg-gray-300/30 border border-gray-300/30 rounded-full p-1 shadow-sm"
      >
        {indicator && (
          <div
            className="absolute top-1 bottom-1 bg-zinc-900 rounded-full shadow ease-out"
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
            className={`relative z-10 px-5 py-2 rounded-full text-sm font-bold transition-colors duration-300 cursor-pointer ${
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
