'use client';
import { useEffect, useRef } from 'react';
import { useSession } from 'next-auth/react';
import { useToast } from '@/providers/ToastProvider';

const STORAGE_KEY = 'sdp2_settings';

function playNotificationSound() {
  try {
    const ctx = new AudioContext();
    const osc = ctx.createOscillator();
    const gain = ctx.createGain();
    osc.connect(gain);
    gain.connect(ctx.destination);
    osc.frequency.value = 880;
    osc.type = 'sine';
    gain.gain.setValueAtTime(0.15, ctx.currentTime);
    gain.gain.exponentialRampToValueAtTime(0.001, ctx.currentTime + 0.3);
    osc.start(ctx.currentTime);
    osc.stop(ctx.currentTime + 0.3);
  } catch {
    // AudioContext not available
  }
}

function isSoundEnabled(): boolean {
  try {
    const s = JSON.parse(localStorage.getItem(STORAGE_KEY) ?? '{}');
    return Boolean(s['notify-sound']);
  } catch {
    return false;
  }
}

interface SsePayload {
  titel?: string;
  bericht?: string;
  type?: 'success' | 'error' | 'warning' | 'info';
}

export function useSseNotifications() {
  const { data: session } = useSession();
  const toast = useToast();
  const toastRef = useRef(toast);
  toastRef.current = toast;

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
        toastRef.current.show(message);
      } catch {
        toastRef.current.show(event.data || 'Nieuwe melding');
      }

      if (isSoundEnabled()) {
        playNotificationSound();
      }

      if (isSoundEnabled()) {
        playNotificationSound();
      }
    };

    es.onerror = () => {
      console.warn('SSE verbinding onderbroken, herverbinding...');
    };

    return () => es.close();
  }, [session?.accessToken, session?.user?.id]); // toast niet als dep — gebruik ref om reconnects te voorkomen
}
