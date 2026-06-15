import FileItem from '@/components/app/chat/FileItem';
import { MdOutlineReplay } from 'react-icons/md';
import ReactMarkdown from 'react-markdown';

interface Chat {
  id: string;
  role: 'user' | 'assistant';
  content: string;
  timestamp: Date;
  files?: File[];
}

interface ChatMessageProps {
  message: Chat;
}

const ChatMessage = (props: ChatMessageProps) => {
  const { message } = props;
  const hour = HourParser(message.timestamp);
  if (message.role === 'user') {
    return (
      <div className="min-w-full w-full flex flex-col gap-2 items-end justify-center pb-3">
        <div className={'flex flex-row max-w-1/4 overflow-x-scroll gap-2'}>
          {message.files?.map((file, key) => (
            <FileItem key={key} file={file} display />
          ))}
        </div>
        <div className="flex flex-col items-end gap-1 justify-center max-w-[20rem] w-full">
          <div
            className={`w-fit px-4 min-w-fit py-2 rounded-3xl bg-zinc-900 text-white`}
          >
            {message.content}
          </div>
          <span className={'w-full text-xs text-zinc-400 text-end px-4'}>
            {hour}
          </span>
        </div>
      </div>
    );
  }

  return (
    <div className="min-w-full w-full flex flex-col gap-1 items-start justify-center pb-3">
      <div className="flex flex-col items-start gap-1 max-w-[80%]">
        <div className="w-fit px-1 py-2 text-zinc-900">
          <p className="whitespace-pre-wrap">
            <ReactMarkdown>{message.content}</ReactMarkdown>
            {message.content === '' && (
              <span className="inline-block w-4 h-4 bg-zinc-400 rounded-full align-middle animate-pulse ml-0.5" />
            )}
          </p>
        </div>
        <div className={'flex flex-row gap-2 items-center text-zinc-400'}>
          <span className="text-xs px-1">{hour}</span> <MdOutlineReplay />
        </div>
      </div>
    </div>
  );
};

function HourParser(time: Date) {
  const hour = time.getHours().toPrecision();
  let minutes = time.getMinutes().toPrecision();
  if (parseInt(minutes) < 10) {
    minutes = '0' + minutes;
  }

  return hour + ':' + minutes;
}

ChatMessage.displayName = 'ChatMessage';
export default ChatMessage;
