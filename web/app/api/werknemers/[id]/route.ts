import { NextRequest } from 'next/server';
import { proxyGET, proxyPUT } from '@/lib/backendFetch';

export async function GET(
  _req: NextRequest,
  { params }: { params: Promise<{ id: string }> },
) {
  const { id } = await params;
  return proxyGET(`/werknemers/${id}`);
}

export async function PUT(
  req: NextRequest,
  { params }: { params: Promise<{ id: string }> },
) {
  const { id } = await params;
  const body = await req.json();
  return proxyPUT(`/werknemers/${id}`, body);
}
