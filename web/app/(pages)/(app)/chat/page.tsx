'use client';
import ChatClient from '@/components/app/chat/ChatClient';
import BreadcrumbInit from '@/components/overig/structuur/breadcrumb/BreadCrumbInit';
import React, { useState } from 'react';
import ChatSidecar from '@/components/app/chat/ChatSidecar';

export default function ChatPage() {
  const [isOpen, setIsOpen] = useState<boolean>(false);
  return (
    <main className="w-full h-full flex flex-col md:flex-row gap-2">
      <BreadcrumbInit pages={['chat']} />
      {isOpen && <ChatSidecar />}
      <div className="w-full md:w-5/6 flex-1 min-h-64 sm:min-h-80 md:h-full border border-zinc-300 overflow-hidden rounded-3xl">
        <ChatClient isOpen={isOpen} setIsOpen={setIsOpen} />
      </div>
    </main>
  );
}
