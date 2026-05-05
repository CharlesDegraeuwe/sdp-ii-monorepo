import { proxyDELETE } from '@/lib/backendFetch';

export async function DELETE(
  _req: Request,
  { params }: { params: Promise<{ teamId: string }> },
) {
  const { teamId } = await params;
  return proxyDELETE(`/teams/${teamId}`);
}
