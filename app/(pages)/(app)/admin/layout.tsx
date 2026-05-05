import { PageContainer } from '@/components/design-system/PageContainer';
import { AppContainer } from '@/components/design-system/AppContainer';

export default function AdminLayout({
  children,
}: Readonly<{
  children: React.ReactNode;
}>) {
  return (
    <PageContainer className="h-full">
      <AppContainer>{children}</AppContainer>
    </PageContainer>
  );
}
