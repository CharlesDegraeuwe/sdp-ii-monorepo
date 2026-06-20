'use client';
import Image from 'next/image';
import { Dispatch, SetStateAction, useEffect, useRef, useState } from 'react';
import { Button } from '@/components/design-system/Button';
import { MdCheck } from 'react-icons/md';
import { IoCalendarOutline } from 'react-icons/io5';
import { IoIosAirplane } from 'react-icons/io';
import { FormHelper } from '@/components/design-system/Form';
import ChatMessage from '@/components/app/chat/Message';
import ChatInput from '@/components/app/chat/ChatInput';
import { useChatSocket } from '@/hooks/useChatSocket';
import { AnimateOnMount } from '@/components/design-system/AnimateOnMount';
import { AiOutlineWechatWork } from 'react-icons/ai';
import BaseTooltip from '@/components/design-system/Tooltip/Tooltip';

const SUGGESTIONS = [
  { icon: <MdCheck />, label: 'Wat zijn mijn taken?' },
  { icon: <IoCalendarOutline />, label: 'Wat is mijn planning?' },
  { icon: <IoIosAirplane />, label: 'Hoeveel verlof heb ik?' },
];

interface ChatCLientProps {
  isOpen: boolean;
  setIsOpen: Dispatch<SetStateAction<boolean>>;
}
const ChatClient = (props: ChatCLientProps) => {
  const { isOpen, setIsOpen } = props;
  const [input, setInput] = useState('');
  const inputRef = useRef<HTMLTextAreaElement>(null);
  const scrollRef = useRef<HTMLDivElement>(null);
  const { messages, fileList, setFileList, sendMessage, isAgentic, streaming } =
    useChatSocket();
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
      <div className={'relative min-h-0 flex-1 w-full h-full bg-bg-white'}>
        <div
          className={`absolute ${isAgentic ? 'opacity-0' : 'opacity-100 animate-pulse'} transition-opacity duration-250 w-full h-full rounded-3xl overflow-hidden flex items-center justify-center bg-white`}
          style={{
            boxShadow: `
            inset 200px 200px 250px -150px #60a5fa,
            inset -200px 200px 250px -150px #fbbf24,
            inset 200px -200px 250px -150px #fb7185,
            inset -200px -200px 250px -150px #a855f7
        `,
          }}
        />

        <div className="relative z-20 w-full h-full flex items-center justify-center">
          <div className={'absolute top-2 left-1/2 -translate-x-1/2 z-20'}>
            <BaseTooltip content={isOpen ? 'samenvouwen' : 'uitvouwen'}>
              <Button
                variant={'ghost'}
                form={'square'}
                icon={<AiOutlineWechatWork size={20} />}
                onClick={() => setIsOpen((prevState) => !prevState)}
              />
            </BaseTooltip>
          </div>
          <div className="z-10 flex flex-col items-center justify-center w-full h-full">
            {!hasMessages && (
              <div
                className={
                  'w-full h-full flex items-center pt-20 sm:pt-32 lg:pt-50 flex-col gap-5 px-4'
                }
              >
                <Image
                  alt="Benoit Logo"
                  src={'ben.svg'}
                  width={100}
                  height={100}
                  className="h-24 sm:h-36 w-auto"
                />
                <div
                  className={
                    'w-full sm:w-4/5 md:w-4/5 lg:w-1/2 flex flex-col gap-2 items-center justify-center'
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
                      isReceiving={streaming}
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
                  <div
                    className={
                      'w-full flex flex-col justify-center sm:flex-row gap-2 mt-3'
                    }
                  >
                    {SUGGESTIONS.map((s, i) => (
                      <AnimateOnMount key={s.label} delay={100 * i}>
                        <Button
                          type="button"
                          iconLeft={s.icon}
                          variant="prompt"
                          textSize="sm"
                          label={s.label}
                          color="zinc-400"
                          onClick={() => handleSuggestion(s.label)}
                        />
                      </AnimateOnMount>
                    ))}
                  </div>
                </div>
              </div>
            )}

            {hasMessages && (
              <div className="w-full h-full flex flex-col items-center justify-between py-6 sm:py-10 px-4">
                <div
                  ref={scrollRef}
                  className="flex flex-col w-full sm:w-4/5 md:w-3/5 lg:w-2/5 flex-1 overflow-y-auto scroll-hidden pb-10"
                >
                  {messages.map((message) => (
                    <ChatMessage key={message.id} message={message} />
                  ))}
                </div>
                <div className="w-full sm:w-4/5 md:w-3/5 lg:w-2/5 flex flex-col gap-1">
                  <ChatInput
                    isReceiving={streaming}
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
        </div>
      </div>
    </FormHelper>
  );
};

ChatClient.displayName = 'ChatClient';
export default ChatClient;
