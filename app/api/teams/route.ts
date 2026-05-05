import { NextRequest } from 'next/server';
import { proxyGET, proxyPOST } from '@/lib/backendFetch';

export async function GET() {
  return proxyGET('/teams');
}

export async function POST(req: NextRequest) {
  const body = await req.json();
  return proxyPOST('/teams', body);
}
