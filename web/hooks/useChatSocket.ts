import { useCallback, useEffect, useRef, useState } from 'react';
import { useSession } from 'next-auth/react';

interface Message {
  id: string;
  role: 'user' | 'assistant';
  content: string;
  timestamp: Date;
  files?: File[];
}

type WSStatus = 'connecting' | 'open' | 'closed' | 'error';

export function useChatSocket() {
  const { data: session } = useSession();
  const token = session?.accessToken;

  const wsRef = useRef<WebSocket | null>(null);
  const currentAssistantId = useRef<string | null>(null);

  const [messages, setMessages] = useState<Message[]>([]);
  const [fileList, setFileList] = useState<File[]>([]);
  const isAgentic = useState<boolean>(false);
  const [status, setStatus] = useState<WSStatus>(
    token ? 'connecting' : 'closed',
  );
  const [streaming, setStreaming] = useState(false);

  useEffect(() => {
    if (!token) return;

    const wsUrl =
      process.env.NEXT_PUBLIC_WS_URL ?? 'ws://localhost:8080/ws/chat';
    const ws = new WebSocket(`${wsUrl}?token=${token}`);
    wsRef.current = ws;

    ws.onopen = () => setStatus('open');
    ws.onerror = () => setStatus('error');
    ws.onclose = () => setStatus('closed');

    ws.onmessage = (event) => {
      try {
        const data = JSON.parse(event.data);
        const id = currentAssistantId.current;

        if (data.type === 'chunk' && id) {
          setMessages((prev) =>
            prev.map((m) =>
              m.id === id ? { ...m, content: m.content + data.content } : m,
            ),
          );
        }

        if (data.type === 'done') {
          currentAssistantId.current = null;
          setStreaming(false);
        }

        if (data.type === 'error') {
          console.error('Chat error:', data.content);
          if (id) {
            setMessages((prev) =>
              prev.map((m) =>
                m.id === id
                  ? {
                      ...m,
                      content: 'Sorry, er ging iets mis. Probeer opnieuw.',
                    }
                  : m,
              ),
            );
          }
          currentAssistantId.current = null;
          setStreaming(false);
        }
      } catch (err) {
        console.error('WS parse error', err);
      }
    };

    return () => {
      ws.close();
      wsRef.current = null;
    };
  }, [token]);

  const sendMessage = useCallback(
    (text: string) => {
      const trimmed = text.trim();
      if (!trimmed) return;
      if (wsRef.current?.readyState !== WebSocket.OPEN) {
        console.warn('WS not open, cannot send');
        return;
      }
      if (streaming) {
        console.warn('Already streaming, please wait');
        return;
      }

      const userMessage: Message = {
        id: crypto.randomUUID(),
        role: 'user',
        content: trimmed,
        timestamp: new Date(),
        files: fileList.length > 0 ? fileList : undefined,
      };

      const assistantId = crypto.randomUUID();
      const assistantMessage: Message = {
        id: assistantId,
        role: 'assistant',
        content: '',
        timestamp: new Date(),
      };

      currentAssistantId.current = assistantId;
      setMessages((prev) => [...prev, userMessage, assistantMessage]);
      setStreaming(true);

      wsRef.current.send(JSON.stringify({ content: trimmed }));
    },
    [streaming, fileList],
  );

  return {
    messages,
    setMessages,
    fileList,
    setFileList,
    sendMessage,
    status,
    streaming,
    isAgentic,
  };
}
