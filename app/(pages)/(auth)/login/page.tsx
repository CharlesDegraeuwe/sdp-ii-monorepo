import { PageContainer } from '@/components/design system/PageContainer';
import { Container } from '@/components/design system/Container';
import { Label } from '@/components/design system/Label';
import LoginForm from '@/components/auth/login/LoginForm';

export default function Page() {
  return (
    <PageContainer>
      <Container
        width="1/2"
        height="1/3"
        pointer={true}
        padding="10"
        gap={5}
        flexDirection="col"
        flex="flex"
      >
        <div>
          <Label px={3} text="Login" />
        </div>
        <LoginForm />
      </Container>
    </PageContainer>
  );
}
