import { proxyDELETE } from '@/lib/backendFetch';

export async function DELETE(
  _req: Request,
  { params }: { params: Promise<{ id: string }> },
) {
  const { id } = await params;
  return proxyDELETE(`/taken/${id}`);
}
