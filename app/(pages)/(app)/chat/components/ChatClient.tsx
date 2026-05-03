import Image from 'next/image';
import { Input } from '@/components/design system/Input';
const ChatClient = () => {
  return (
    <div className="relative flex flex-col items-center justify-center w-full h-full ">
      <Image
        alt="ben-logo"
        src={'ben.svg'}
        width={100}
        height={100}
        className="h-36 w-auto"
      />
      <span>Chat met Ben</span>
      <div className="absolute bottom-0 max-h-fit left-1/2 -translate-x-1/2 w-1/2 flex flex-col gap-1">
        <Input
          width={'full'}
          placeholder={'Chat met Ben'}
          errorOption={false}
        />
        <span className={'px-6 w-full text-xs text-zinc-300'}>
          Ben kan fouten maken*
        </span>
      </div>
    </div>
  );
};

ChatClient.displayName = 'ChatClient';
export default ChatClient;
