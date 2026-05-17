import { NextRequest } from 'next/server';
import { proxyPUT } from '@/lib/backendFetch';

export async function PUT(req: NextRequest) {
  const body = await req.json();
  return proxyPUT('/werknemers/wachtwoord', body);
}
