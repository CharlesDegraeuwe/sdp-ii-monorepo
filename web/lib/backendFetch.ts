import { auth } from '@/auth';
import { NextResponse } from 'next/server';

const API_URL = process.env.AUTH_API_URL ?? 'http://localhost:8080/api';

function safeJsonResponse(text: string) {
  if (!text) return NextResponse.json({ ok: true });
  try {
    return NextResponse.json(JSON.parse(text));
  } catch {
    return NextResponse.json({ ok: true, message: text });
  }
}

export async function backendFetch(
  path: string,
  options?: RequestInit,
): Promise<Response> {
  const session = await auth();
  const headers: Record<string, string> = {
    'Content-Type': 'application/json',
    ...(session?.accessToken
      ? { Authorization: `Bearer ${session.accessToken}` }
      : {}),
  };

  return fetch(`${API_URL}${path}`, {
    ...options,
    headers: { ...headers, ...(options?.headers as Record<string, string>) },
  });
}

export async function proxyGET(path: string) {
  try {
    const res = await backendFetch(path);
    if (!res.ok)
      return NextResponse.json(
        { error: 'backend_error' },
        { status: res.status },
      );
    const data = await res.json();
    return NextResponse.json(data);
  } catch {
    return NextResponse.json({ error: 'Backend unreachable' }, { status: 502 });
  }
}

export async function proxyPOST(path: string, body: unknown) {
  try {
    const res = await backendFetch(path, {
      method: 'POST',
      body: JSON.stringify(body),
    });
    if (!res.ok) {
      const text = await res.text();
      let message = 'backend_error';
      try {
        const json = JSON.parse(text);
        if (json.message) message = json.message;
      } catch {}
      return NextResponse.json(
        { error: 'backend_error', message },
        { status: res.status },
      );
    }
    const text = await res.text();
    return safeJsonResponse(text);
  } catch {
    return NextResponse.json({ error: 'Backend unreachable' }, { status: 502 });
  }
}

export async function proxyPUT(path: string, body?: unknown) {
  try {
    const res = await backendFetch(path, {
      method: 'PUT',
      body: body ? JSON.stringify(body) : undefined,
    });
    if (!res.ok) {
      const text = await res.text();
      let message = 'backend_error';
      try {
        const json = JSON.parse(text);
        if (json.message) message = json.message;
      } catch {}
      return NextResponse.json(
        { error: 'backend_error', message },
        { status: res.status },
      );
    }
    const text = await res.text();
    return safeJsonResponse(text);
  } catch {
    return NextResponse.json({ error: 'Backend unreachable' }, { status: 502 });
  }
}

export async function proxyDELETE(path: string) {
  try {
    const res = await backendFetch(path, { method: 'DELETE' });
    if (!res.ok)
      return NextResponse.json(
        { error: 'backend_error' },
        { status: res.status },
      );
    const text = await res.text();
    return safeJsonResponse(text);
  } catch {
    return NextResponse.json({ error: 'Backend unreachable' }, { status: 502 });
  }
}
