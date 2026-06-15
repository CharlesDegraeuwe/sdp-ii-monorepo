import { proxyGET } from '@/lib/backendFetch';

export async function GET(
  _req: Request,
  { params }: { params: Promise<{ werknemerId: string }> },
) {
  const { werknemerId } = await params;
  return proxyGET(`/teams/werknemer/${werknemerId}`);
}
