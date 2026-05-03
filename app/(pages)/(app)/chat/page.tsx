import { AppContainer } from '@/components/design system/AppContainer';
import { PageContainer } from '@/components/design system/PageContainer';
import ChatClient from '@/app/(pages)/(app)/chat/components/ChatClient';
import BreadcrumbInit from '@/components/app/structuur/breadcrumb/BreadCrumbInit';

export default function ChatPage() {
  return (
    <PageContainer>
      <AppContainer>
        <BreadcrumbInit pages={['chat']} />
        <ChatClient />
      </AppContainer>
    </PageContainer>
  );
}
