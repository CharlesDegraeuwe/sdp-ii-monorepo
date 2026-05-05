'use client';
import Image from 'next/image';
import { Input } from '@/components/design-system/Input';
import { useRef, useState } from 'react';
import { Button } from '@/components/design-system/Button';
import { MdCheck } from 'react-icons/md';
import { IoCalendarOutline } from 'react-icons/io5';
import { IoIosAirplane } from 'react-icons/io';
import { FormHelper } from '@/components/design-system/Form';
import ChatMessage from '@/components/app/chat/Message';
import { FaPlus } from 'react-icons/fa6';
import ChatInput from '@/components/app/chat/ChatInput';

interface Message {
  id: string;
  role: 'user' | 'assistant';
  content: string;
  timestamp: Date;
}

const SUGGESTIONS = [
  { icon: <MdCheck />, label: 'Wat zijn mijn taken?' },
  { icon: <IoCalendarOutline />, label: 'Wat is mijn planning?' },
  { icon: <IoIosAirplane />, label: 'Hoeveel verlof heb ik?' },
];

const ChatClient = () => {
  const [input, setInput] = useState('');
  const [messages, setMessages] = useState<Message[]>([]);
  const inputRef = useRef<HTMLTextAreaElement>(null);

  const hasMessages = messages.length > 0;

  const sendMessage = async (text: string) => {
    const trimmed = text.trim();
    if (!trimmed) return;

    const userMessage: Message = {
      id: crypto.randomUUID(),
      role: 'user',
      content: trimmed,
      timestamp: new Date(),
    };

    setMessages((prev) => [...prev, userMessage]);
    setInput('');
  };

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    sendMessage(input);
  };

  const handleSuggestion = (label: string) => {
    sendMessage(label);
  };

  return (
    <FormHelper onSubmit={handleSubmit}>
      <div className="flex flex-col items-center justify-center w-full h-full">
        {!hasMessages && (
          <div
            className={
              'min-w-full h-full flex items-center pt-75 flex-col gap-5'
            }
          >
            <Image
              alt="Benoit Logo"
              src={'ben.svg'}
              width={100}
              height={100}
              className="h-36 w-auto"
            />
            <div
              className={
                'min-w-1/3 flex flex-col gap-2 items-center justify-center'
              }
            >
              <div
                className={
                  'w-fit flex flex-col gap-0 items-center justify-center mb-3'
                }
              >
                <span className={'font-bold'}>Chat met Benoit</span>
                <span className={'text-sm text-zinc-400'}>
                  je rechterhand binnen Delaware Suite
                </span>
              </div>
              <div className={'w-full'}>
                <ChatInput
                  autoFocus={true}
                  placeholder={'Stel een vraag'}
                  value={input}
                  onValueChange={setInput}
                  onSubmit={() => sendMessage(input)}
                  textareaRef={inputRef}
                />
              </div>
              <div className={'w-full flex flex-row gap-3 mt-3'}>
                {SUGGESTIONS.map((s) => (
                  <Button
                    key={s.label}
                    type="button"
                    iconLeft={s.icon}
                    variant="prompt"
                    textSize="sm"
                    label={s.label}
                    color="zinc-400"
                    onClick={() => handleSuggestion(s.label)}
                  />
                ))}
              </div>
            </div>
          </div>
        )}

        {hasMessages && (
          <div className="w-full h-full flex flex-col items-center justify-between py-10">
            <div className="flex flex-col w-1/3 flex-1 overflow-y-auto">
              {messages.map((message) => (
                <ChatMessage key={message.id} message={message} />
              ))}
            </div>
            <div className="min-w-2/5 flex flex-col gap-1">
              <ChatInput
                autoFocus={true}
                placeholder={'Stel een vraag'}
                value={input}
                onValueChange={setInput}
                onSubmit={() => sendMessage(input)}
                textareaRef={inputRef}
              />

              <span className="px-6 w-full text-xs text-zinc-400">
                Benoit kan fouten maken*
              </span>
            </div>
          </div>
        )}
      </div>
    </FormHelper>
  );
};

ChatClient.displayName = 'ChatClient';
export default ChatClient;
