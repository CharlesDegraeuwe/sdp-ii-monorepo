'use client';
import { useSseNotifications } from '@/hooks/useSseNotifications';

export function SseInitializer() {
  useSseNotifications();
  return null;
}
