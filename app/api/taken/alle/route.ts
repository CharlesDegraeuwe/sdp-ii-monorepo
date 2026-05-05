import { proxyGET } from '@/lib/backendFetch';

export async function GET() {
  return proxyGET('/taken/alle');
}
