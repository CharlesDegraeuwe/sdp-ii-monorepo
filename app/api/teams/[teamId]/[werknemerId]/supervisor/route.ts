import { proxyPUT } from '@/lib/backendFetch';

export async function PUT(
  _req: Request,
  { params }: { params: Promise<{ teamId: string; werknemerId: string }> },
) {
  const { teamId, werknemerId } = await params;
  return proxyPUT(`/teams/${teamId}/${werknemerId}/supervisor`);
}
