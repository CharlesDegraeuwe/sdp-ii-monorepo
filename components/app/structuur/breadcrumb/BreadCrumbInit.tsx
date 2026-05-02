'use client';
import { useBreadCrumbs } from '@/providers/BreadCrumbProvider';
import { useEffect } from 'react';

interface BreadCrumbInitProps {
  pages: string[];
}

const BreadcrumbInit = (props: BreadCrumbInitProps) => {
  const pages = props.pages;
  const { setPages } = useBreadCrumbs();

  useEffect(() => {
    setPages((prev) => [...prev, ...pages]);
    return () => setPages((prev) => prev.filter((p) => !pages.includes(p)));
  }, []);

  return null;
};

BreadcrumbInit.displayName = 'BreadcrumbInit';
export default BreadcrumbInit;
