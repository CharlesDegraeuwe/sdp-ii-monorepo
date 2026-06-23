import { useCallback, useEffect, useRef, useState } from 'react';
import { useSession } from 'next-auth/react';

const BASE = process.env.NEXT_PUBLIC_API_URL ?? 'http://localhost:8080/api';

interface FileAttachment {
  id: string;
  name: string;
  mimeType: string;
  size: number;
}

interface Message {
  id: string;
  role: 'user' | 'assistant';
  content: string;
  timestamp: Date;
  files?: File[];
}

type WSStatus = 'connecting' | 'open' | 'closed' | 'error';

async function uploadFiles(
  files: File[],
  token: string,
): Promise<FileAttachment[]> {
  if (files.length === 0) return [];
  const formData = new FormData();
  files.forEach((f) => formData.append('files', f));
  const res = await fetch(`${BASE}/chat/files`, {
    method: 'POST',
    headers: { Authorization: `Bearer ${token}` },
    body: formData,
  });
  if (!res.ok) throw new Error('File upload failed');
  return res.json();
}

export function useChatSocket() {
  const { data: session } = useSession();
  const token = session?.accessToken;

  const wsRef = useRef<WebSocket | null>(null);
  const currentAssistantId = useRef<string | null>(null);

  const [messages, setMessages] = useState<Message[]>([]);
  const [fileList, setFileList] = useState<File[]>([]);
  const [isAgentic, setIsAgentic] = useState<boolean>(false);
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
          if (data.isAgentic !== undefined && data.isAgentic !== null) {
            setIsAgentic(data.isAgentic);
          }
          setMessages((prev) =>
            prev.map((m) =>
              m.id === id ? { ...m, content: m.content + data.content } : m,
            ),
          );
        }

        if (data.type === 'done') {
          currentAssistantId.current = null;
          setStreaming(false);
          setIsAgentic(false);
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
    async (text: string) => {
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
        files: fileList.length > 0 ? [...fileList] : undefined,
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

      try {
        const attachments = token ? await uploadFiles(fileList, token) : [];
        const fileIds = attachments.map((a) => a.id);
        wsRef.current?.send(JSON.stringify({ content: trimmed, fileIds }));
      } catch (err) {
        console.error('File upload error:', err);
        setMessages((prev) =>
          prev.map((m) =>
            m.id === assistantId
              ? { ...m, content: 'Bestand uploaden mislukt. Probeer opnieuw.' }
              : m,
          ),
        );
        currentAssistantId.current = null;
        setStreaming(false);
      }
    },
    [streaming, fileList, token],
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
    setIsAgentic,
  };
}
