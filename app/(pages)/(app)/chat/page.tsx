import { AppContainer } from '@/components/design-system/AppContainer';
import { PageContainer } from '@/components/design-system/PageContainer';
import ChatClient from '@/components/app/chat/ChatClient';
import BreadcrumbInit from '@/components/app/structuur/breadcrumb/BreadCrumbInit';

export default function ChatPage() {
  return (
    <PageContainer>
      <AppContainer px={'px-0'} py={'py-0'}>
        <BreadcrumbInit pages={['chat']} />
        <ChatClient />
      </AppContainer>
    </PageContainer>
  );
}
