import { NextRequest } from 'next/server';
import { proxyGET } from '@/lib/backendFetch';

export async function GET(
  req: NextRequest,
  { params }: { params: Promise<{ werknemerId: string }> },
) {
  const { werknemerId } = await params;
  const van = req.nextUrl.searchParams.get('van') ?? '';
  const tot = req.nextUrl.searchParams.get('tot') ?? '';
  return proxyGET(`/shifts/werknemer/${werknemerId}/bereik?van=${van}&tot=${tot}`);
}
