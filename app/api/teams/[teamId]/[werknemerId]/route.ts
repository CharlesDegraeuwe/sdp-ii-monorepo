import { proxyPUT, proxyDELETE } from '@/lib/backendFetch';

export async function PUT(
  _req: Request,
  { params }: { params: Promise<{ teamId: string; werknemerId: string }> },
) {
  const { teamId, werknemerId } = await params;
  return proxyPUT(`/teams/${teamId}/${werknemerId}`);
}

export async function DELETE(
  _req: Request,
  { params }: { params: Promise<{ teamId: string; werknemerId: string }> },
) {
  const { teamId, werknemerId } = await params;
  return proxyDELETE(`/teams/${teamId}/${werknemerId}`);
}
