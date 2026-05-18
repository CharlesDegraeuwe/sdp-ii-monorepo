import { NextRequest } from 'next/server';
import { proxyPOST } from '@/lib/backendFetch';

export async function POST(req: NextRequest) {
  const body = await req.json();
  return proxyPOST('/afwezigheid', body);
}
