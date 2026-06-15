import { NextRequest } from 'next/server';
import { proxyGET } from '@/lib/backendFetch';

export async function GET(
  req: NextRequest,
  { params }: { params: Promise<{ teamId: string }> },
) {
  const { teamId } = await params;
  const datum = req.nextUrl.searchParams.get('datum') ?? '';
  return proxyGET(`/shifts/team/${teamId}?datum=${datum}`);
}
