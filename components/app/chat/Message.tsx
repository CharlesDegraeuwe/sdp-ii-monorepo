interface Chat {
  id: string;
  role: 'user' | 'assistant';
  content: string;
  timestamp: Date;
}

interface ChatMessageProps {
  message: Chat;
}
const ChatMessage = (props: ChatMessageProps) => {
  const { message } = props;
  const hour = HourParser(message.timestamp);
  if (message.role === 'user') {
    return (
      <div className="min-w-full w-full flex flex-row items-center justify-end pb-3">
        <div className="flex flex-col items-center gap-1 justify-end max-w-[20rem]">
          <div
            className={`w-fit px-4 min-w-fit py-2 rounded-4xl bg-zinc-900 text-white`}
          >
            <p>{message.content}</p>
          </div>
          <span className={'w-full text-xs text-zinc-400 text-end px-4'}>
            {hour}
          </span>
        </div>
      </div>
    );
  }
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
