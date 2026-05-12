'use client';
import React, { useMemo, useState } from 'react';
import Splash from '@/components/overig/splash/Splash';

type SplashContextType = {
  splashOpen: boolean;
  setSplashOpen: React.Dispatch<React.SetStateAction<boolean>>;
};

const SplashContext = React.createContext<SplashContextType | null>(null);
export const SplashProvider = ({ children }: { children: React.ReactNode }) => {
  const [splashOpen, setSplashOpen] = useState<boolean>(false);

  const value = useMemo(
    () => ({
      splashOpen,
      setSplashOpen,
    }),
    [splashOpen],
  );

  return (
    <SplashContext.Provider value={value}>
      {splashOpen && <Splash />}
      {children}
    </SplashContext.Provider>
  );
};

export const useSplash = () => {
  const ctx = React.useContext(SplashContext);
  if (!ctx) {
    throw new Error('useSplash moet binnen Splashprovider gebruikt worden');
  }
  return ctx;
};
