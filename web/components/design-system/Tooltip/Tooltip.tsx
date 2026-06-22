'use client';
import { useState, useRef, useEffect } from 'react';
import { createPortal } from 'react-dom';

interface BaseTooltipProps {
  children: React.ReactNode;
  content: string | number;
  position?: 'top' | 'bottom' | 'left' | 'right';
  delay?: number;
}

const BaseTooltip = ({
  children,
  content,
  position = 'top',
  delay = 200,
}: BaseTooltipProps) => {
  const [visible, setVisible] = useState(false);
  const [mounted, setMounted] = useState(false);
  const [coords, setCoords] = useState({ top: 0, left: 0 });
  const triggerRef = useRef<HTMLDivElement>(null);
  const timeoutRef = useRef<ReturnType<typeof setTimeout> | null>(null);

  const show = () => {
    timeoutRef.current = setTimeout(() => {
      if (!triggerRef.current) return;
      const rect = triggerRef.current.getBoundingClientRect();
      const posMap = {
        top: { top: rect.top - 8, left: rect.left + rect.width / 2 },
        bottom: { top: rect.bottom + 8, left: rect.left + rect.width / 2 },
        left: { top: rect.top + rect.height / 2, left: rect.left - 8 },
        right: { top: rect.top + rect.height / 2, left: rect.right + 8 },
      };

      let { top, left } = posMap[position];
      // viewport clamping
      const margin = 8;
      left = Math.min(Math.max(left, margin), window.innerWidth - margin);
      top = Math.min(Math.max(top, margin), window.innerHeight - margin);

      setCoords({ top, left });
      setMounted(true);
      // next frame zodat de transition kan starten vanuit de begin-staat
      requestAnimationFrame(() => setVisible(true));
    }, delay);
  };

  const hide = () => {
    if (timeoutRef.current) clearTimeout(timeoutRef.current);
    setVisible(false);
  };

  useEffect(() => {
    return () => {
      if (timeoutRef.current) clearTimeout(timeoutRef.current);
    };
  }, []);

  const transformMap = {
    top: 'translate(-50%, -100%)',
    bottom: 'translate(-50%, 0%)',
    left: 'translate(-100%, -50%)',
    right: 'translate(0%, -50%)',
  };

  // arrow zit aan de tegenovergestelde kant van waar de tooltip staat
  const arrowMap = {
    top: 'bottom-[-4px] left-1/2 -translate-x-1/2',
    bottom: 'top-[-4px] left-1/2 -translate-x-1/2',
    left: 'right-[-4px] top-1/2 -translate-y-1/2',
    right: 'left-[-4px] top-1/2 -translate-y-1/2',
  };

  return (
    <div
      ref={triggerRef}
      className="inline-flex"
      onMouseEnter={show}
      onMouseLeave={hide}
    >
      {children}
      {mounted &&
        createPortal(
          <div
            role="tooltip"
            className={`fixed z-[9999] whitespace-nowrap rounded-full border border-studoborder/30
              bg-zinc-900 px-3 py-1.5 text-sm font-medium text-white shadow-lg
              pointer-events-none backdrop-blur-sm
              transition-all duration-150 ease-out
              ${visible ? 'opacity-100 scale-100' : 'opacity-0 scale-95'}`}
            style={{
              top: coords.top,
              left: coords.left,
              transform: `${transformMap[position]} ${visible ? '' : ''}`,
            }}
            onTransitionEnd={() => {
              if (!visible) setMounted(false);
            }}
          >
            {content}
            <span
              className={`absolute h-2 w-2 rotate-45 bg-zinc-900
                border-studoborder/30 ${arrowMap[position]}`}
            />
          </div>,
          document.body,
        )}
    </div>
  );
};

BaseTooltip.displayName = 'BaseTooltip';
export default BaseTooltip;
