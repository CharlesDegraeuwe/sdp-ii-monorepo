import { proxyPUT } from '@/lib/backendFetch';

export async function PUT(
  _req: Request,
  { params }: { params: Promise<{ id: string }> },
) {
  const { id } = await params;
  return proxyPUT(`/taken/${id}/afgewerkt`);
}
