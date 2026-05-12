import { NextRequest } from 'next/server';
import { proxyPUT } from '@/lib/backendFetch';

export async function PUT(
  req: NextRequest,
  { params }: { params: Promise<{ shiftId: string }> },
) {
  const { shiftId } = await params;
  const body = await req.json();
  return proxyPUT(`/shifts/${shiftId}`, body);
}
