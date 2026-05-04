'use client';
import { useEffect, useRef, useState } from 'react';
import { TabSwitcherProps } from '@/components/design system/TabSwitcher/TabSwitcher.types';

export const TabSwitcher = <T extends string = string>({
  tabs,
  value,
  onChange,
}: TabSwitcherProps<T>) => {
  const containerRef = useRef<HTMLDivElement>(null);
  const buttonRefs = useRef<Record<string, HTMLButtonElement | null>>({});
  const [indicator, setIndicator] = useState({ left: 0, width: 0 });

  useEffect(() => {
    const activeBtn = buttonRefs.current[value];
    const container = containerRef.current;
    if (!activeBtn || !container) return;

    const containerRect = container.getBoundingClientRect();
    const btnRect = activeBtn.getBoundingClientRect();

    setIndicator({
      left: btnRect.left - containerRect.left,
      width: btnRect.width,
    });
  }, [value, tabs]);

  return (
    <div className="flex justify-center">
      <div
        ref={containerRef}
        className="relative flex gap-1 bg-gray-300/30 border border-gray-300/30 rounded-full p-1 shadow-sm"
      >
        <div
          className="absolute top-1 bottom-1 bg-zinc-900 rounded-full shadow transition-all duration-300 ease-out"
          style={{
            left: `${indicator.left}px`,
            width: `${indicator.width}px`,
          }}
        />

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
