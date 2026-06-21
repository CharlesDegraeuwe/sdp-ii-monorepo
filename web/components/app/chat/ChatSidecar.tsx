import React from 'react';
import { FaPencilAlt } from 'react-icons/fa';
import { Button } from '@/components/design-system/Button';

const ChatSidecar = () => {
  return (
    <div className="w-full md:w-1/6 h-72 sm:h-96 md:h-full border border-zinc-300 p-4 overflow-hidden rounded-3xl relative bg-white shrink-0">
      <div className={'flex flex-row justify-between items-center'}>
        <span className={'font-semibold'}>Chat Geschiedenis</span>
        <Button
          type={'button'}
          size={'sm'}
          variant={'ghost'}
          icon={<FaPencilAlt />}
        />
      </div>
    </div>
  );
};

ChatSidecar.displayName = 'ChatSidecar';
export default ChatSidecar;
