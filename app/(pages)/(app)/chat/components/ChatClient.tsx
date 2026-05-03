'use client';
import Image from 'next/image';
import { Input } from '@/components/design system/Input';
import { useState } from 'react';
import { Button } from '@/components/design system/Button';
import { MdCheck } from 'react-icons/md';
import { IoCalendarOutline } from 'react-icons/io5';
import { IoIosAirplane } from 'react-icons/io';
const ChatClient = () => {
  const [questionAsked, setQuestionAsked] = useState(false);
  return (
    <div className="relative flex flex-col items-center justify-center w-full h-full ">
      <div
        className={'min-w-full h-full flex items-center pt-75 flex-col gap-5'}
      >
        <Image
          alt="ben-logo"
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
            <span className={'font-bold'}>Chat met Frederik</span>
            <span className={'text-sm text-zinc-400'}>
              je rechterhand binnen Delaware Suite
            </span>
          </div>
          <div className={'w-full'}>
            <Input
              width={'full'}
              placeholder={'Stel een vraag'}
              errorOption={false}
            />
          </div>
          <div className={'w-full flex flex-row gap-3 mt-3'}>
            <Button
              iconLeft={<MdCheck />}
              variant="prompt"
              textSize={'sm'}
              label={'Wat zijn mijn taken?'}
              color={'zinc-400'}
            />
            <Button
              iconLeft={<IoCalendarOutline />}
              variant="prompt"
              textSize={'sm'}
              label={'Wat is mijn planning?'}
              color={'zinc-400'}
            />
            <Button
              iconLeft={<IoIosAirplane />}
              variant="prompt"
              textSize={'sm'}
              label={'Hoeveel verlof heb ik?'}
              color={'zinc-400'}
            />
          </div>
        </div>
      </div>
      <div
        className={`absolute ${!questionAsked && 'hidden'} bottom-0 max-h-fit left-1/2 -translate-x-1/2 w-1/3 flex flex-col gap-1`}
      >
        <Input
          width={'full'}
          placeholder={'Stel een vraag'}
          errorOption={false}
        />
        <span className={'px-6 w-full text-xs text-zinc-300'}>
          Frederik kan fouten maken*
        </span>
      </div>
    </div>
  );
};

ChatClient.displayName = 'ChatClient';
export default ChatClient;
