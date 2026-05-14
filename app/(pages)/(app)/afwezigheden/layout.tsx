'use client';
import BreadcrumbInit from '@/components/overig/structuur/breadcrumb/BreadCrumbInit';
import { AppContainer } from '@/components/design-system/AppContainer';
import { PageContainer } from '@/components/design-system/PageContainer';
import { TabSwitcher } from '@/components/design-system/TabSwitcher/TabSwitcher';
import { useEffect, useState } from 'react';
import { useRouter } from 'next/navigation';

type Tab = 'melden' | 'geschiedenis';
const tabs: { key: Tab; label: string }[] = [
  { key: 'melden', label: 'Afwezigheid melden' },
  { key: 'geschiedenis' as Tab, label: 'Geschiedenis' },
];

export default function AfwezighedenLayout({
  children,
}: Readonly<{ children: React.ReactNode }>) {
  const [tab, setTab] = useState<Tab>('melden');
  const Router = useRouter();
  useEffect(() => {
    Router.push(`/afwezigheden/${tab}`);
  }, [tab, Router]);
  return (
    <PageContainer className="h-full">
      <BreadcrumbInit pages={['afwezigheden']} />
      <AppContainer>
        <div className="w-full max-w-4xl mx-auto flex flex-col gap-6">
          <TabSwitcher
            tabs={tabs}
            value={tab}
            onChange={(key) => {
              setTab(key as Tab);
            }}
          />
          {children}
        </div>
      </AppContainer>
    </PageContainer>
  );
}
