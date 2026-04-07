import { PageContainer } from '@/components/design system/PageContainer';
import { Container } from '@/components/design system/Container';
import { Label } from '@/components/design system/Label';
import LoginForm from '@/components/auth/login/LoginForm';
import { Suspense } from 'react';
import type { Metadata } from 'next';

export const metadata: Metadata = {
  title: 'Login | Delaware Suite',
};

export default function Page() {
  return (
    <PageContainer>
      <Container
        pointer={true}
        padding="10"
        gap={5}
        className={'p-5 max-w-1/4 max-h-1/3'}
        flexDirection="col"
        flex="flex"
      >
        <div>
          <Label px={3} text="Login" />
        </div>
        <Suspense fallback={<div>Laden...</div>}>
          <LoginForm />
        </Suspense>
      </Container>
    </PageContainer>
  );
}
