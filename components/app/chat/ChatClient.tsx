'use client';
import Image from 'next/image';
import { useEffect, useRef, useState } from 'react';
import { Button } from '@/components/design-system/Button';
import { MdCheck } from 'react-icons/md';
import { IoCalendarOutline } from 'react-icons/io5';
import { IoIosAirplane } from 'react-icons/io';
import { FormHelper } from '@/components/design-system/Form';
import ChatMessage from '@/components/app/chat/Message';
import ChatInput from '@/components/app/chat/ChatInput';
import { useChatSocket } from '@/hooks/useChatSocket';

const SUGGESTIONS = [
  { icon: <MdCheck />, label: 'Wat zijn mijn taken?' },
  { icon: <IoCalendarOutline />, label: 'Wat is mijn planning?' },
  { icon: <IoIosAirplane />, label: 'Hoeveel verlof heb ik?' },
];

const ChatClient = () => {
  const [input, setInput] = useState('');
  const inputRef = useRef<HTMLTextAreaElement>(null);
  const scrollRef = useRef<HTMLDivElement>(null);
  const { messages, fileList, setFileList, sendMessage } = useChatSocket();

  const hasMessages = messages.length > 0;

  useEffect(() => {
    scrollRef.current?.scrollTo({
      top: scrollRef.current.scrollHeight,
      behavior: 'smooth',
    });
  }, [messages]);

  const handleMessage = async (text: string) => {
    sendMessage(text);
    setInput('');
    setFileList([]);
  };

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    handleMessage(input);
  };

  const handleSuggestion = (label: string) => {
    handleMessage(label);
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
                  autoFocus
                  placeholder={'Stel een vraag'}
                  value={input}
                  files={fileList}
                  setFiles={setFileList}
                  onValueChange={setInput}
                  onSubmit={() => handleMessage(input)}
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
            <div
              ref={scrollRef}
              className="flex flex-col w-2/5 flex-1 overflow-y-auto"
            >
              {messages.map((message) => (
                <ChatMessage key={message.id} message={message} />
              ))}
            </div>
            <div className="min-w-2/5 flex flex-col gap-1">
              <ChatInput
                autoFocus
                placeholder={'Stel een vraag'}
                value={input}
                files={fileList}
                setFiles={setFileList}
                onValueChange={setInput}
                onSubmit={() => handleMessage(input)}
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
