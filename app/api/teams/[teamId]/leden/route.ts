import { proxyGET } from '@/lib/backendFetch';

export async function GET(
  _req: Request,
  { params }: { params: Promise<{ teamId: string }> },
) {
  const { teamId } = await params;
  return proxyGET(`/teams/${teamId}/leden`);
}
