'use client';

import { useEffect } from 'react';

const STORAGE_KEY = 'sdp2_settings';

export function readSettings(): Record<string, unknown> {
  if (typeof window === 'undefined') return {};
  try {
    return JSON.parse(localStorage.getItem(STORAGE_KEY) ?? '{}');
  } catch {
    return {};
  }
}

export function applySettings(settings: Record<string, unknown>) {
  const root = document.documentElement;

  const theme = String(settings['theme'] ?? 'light');
  if (theme === 'system') {
    const prefersDark = window.matchMedia(
      '(prefers-color-scheme: dark)',
    ).matches;
    root.classList.toggle('dark', prefersDark);
  } else {
    root.classList.toggle('dark', theme === 'dark');
  }

  root.classList.toggle('compact', Boolean(settings['compact-mode']));
}

export function SettingsProvider({ children }: { children: React.ReactNode }) {
  useEffect(() => {
    const apply = () => applySettings(readSettings());

    apply();

    const mq = window.matchMedia('(prefers-color-scheme: dark)');
    mq.addEventListener('change', apply);
    window.addEventListener('storage', apply);

    return () => {
      mq.removeEventListener('change', apply);
      window.removeEventListener('storage', apply);
    };
  }, []);

  return <>{children}</>;
}
