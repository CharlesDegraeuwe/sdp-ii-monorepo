import { auth } from '@/auth';
import { NextResponse } from 'next/server';

const API_URL = process.env.AUTH_API_URL ?? 'http://localhost:8080/api';

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
    if (!res.ok)
      return NextResponse.json(
        { error: 'backend_error' },
        { status: res.status },
      );
    const text = await res.text();
    return NextResponse.json(text ? JSON.parse(text) : { ok: true });
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
    if (!res.ok)
      return NextResponse.json(
        { error: 'backend_error' },
        { status: res.status },
      );
    const text = await res.text();
    return NextResponse.json(text ? JSON.parse(text) : { ok: true });
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
    return NextResponse.json(text ? JSON.parse(text) : { ok: true });
  } catch {
    return NextResponse.json({ error: 'Backend unreachable' }, { status: 502 });
  }
}
