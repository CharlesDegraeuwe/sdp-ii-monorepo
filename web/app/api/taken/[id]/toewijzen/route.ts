import { NextRequest } from 'next/server';
import { proxyPUT } from '@/lib/backendFetch';

export async function PUT(
  req: NextRequest,
  { params }: { params: Promise<{ id: string }> },
) {
  const { id } = await params;
  const { werknemerId } = await req.json();
  return proxyPUT(`/taken/${id}/toewijzen?werknemerId=${werknemerId}`);
}
