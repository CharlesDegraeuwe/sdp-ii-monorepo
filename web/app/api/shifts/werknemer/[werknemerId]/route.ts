import { NextRequest } from 'next/server';
import { proxyGET } from '@/lib/backendFetch';

export async function GET(
  req: NextRequest,
  { params }: { params: Promise<{ werknemerId: string }> },
) {
  const { werknemerId } = await params;
  const datum = req.nextUrl.searchParams.get('datum') ?? '';
  return proxyGET(`/shifts/werknemer/${werknemerId}?datum=${datum}`);
}
