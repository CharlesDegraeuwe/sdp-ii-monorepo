'use client';
import { useEffect } from 'react';
import { useSession } from 'next-auth/react';
import { useToast } from '@/providers/ToastProvider';

interface SsePayload {
  titel?: string;
  bericht?: string;
  type?: 'success' | 'error' | 'warning' | 'info';
}

export function useSseNotifications() {
  const { data: session } = useSession();
  const toast = useToast();

  useEffect(() => {
    const token = session?.accessToken;
    const werknemerId = session?.user?.id;
    if (!token || !werknemerId) return;

    const apiUrl =
      process.env.NEXT_PUBLIC_API_URL ?? 'http://localhost:8080/api';
    const url = `${apiUrl}/sse/subscribe/${werknemerId}?token=${encodeURIComponent(token)}`;

    const es = new EventSource(url);

    es.onmessage = (event: MessageEvent) => {
      try {
        const data: SsePayload = JSON.parse(event.data);
        const message = data.titel ?? data.bericht ?? 'Nieuwe melding';
        toast.show(message);
      } catch {
        toast.show(event.data || 'Nieuwe melding');
      }
    };

    es.onerror = () => {
      console.warn('SSE verbinding onderbroken, herverbinding...');
    };

    return () => es.close();
  }, [session?.accessToken, session?.user?.id, toast]);
}
