export async function POST(req: Request) {
  const body = await req.json();

  const res = await fetch(`${process.env.AUTH_API_URL}/werknemers/login-mail`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(body),
  });

  if (!res.ok) return new Response(null, { status: res.status });
  return new Response(null, { status: 200 });
}
