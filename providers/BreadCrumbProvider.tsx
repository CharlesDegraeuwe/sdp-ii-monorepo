'use client';

import { createContext, ReactNode, useContext, useState } from 'react';

interface Props {
  children: ReactNode;
}

interface BreadCrumbContextType {
  pages: string[];
  setPages: React.Dispatch<React.SetStateAction<string[]>>;
}

const BreadCrumbContext = createContext<BreadCrumbContextType>({
  pages: [],
  setPages: () => {},
});

export default function BreadCrumbProvider({ children }: Props) {
  const [pages, setPages] = useState<string[]>([]);
  return (
    <BreadCrumbContext.Provider
      value={{
        pages,
        setPages,
      }}
    >
      {children}
    </BreadCrumbContext.Provider>
  );
}

export const useBreadCrumbs = () => useContext(BreadCrumbContext);
