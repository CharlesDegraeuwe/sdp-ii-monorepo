import { proxyGET } from '@/lib/backendFetch';

export async function GET() {
  return proxyGET('/teams/sites');
}
