import { NextRequest, NextResponse } from 'next/server';
import { auth } from '@/auth';

export async function GET(request: NextRequest) {
  const session = await auth();
  console.log('[planning] session:', JSON.stringify(session, null, 2));

  if (!session?.accessToken || !session?.user?.id) {
    console.log('[planning] No session/accessToken/userId — returning 401');
    return NextResponse.json({ error: 'Unauthorized' }, { status: 401 });
  }

  const { searchParams } = request.nextUrl;
  const van = searchParams.get('van');
  const tot = searchParams.get('tot');

  let res: Response;
  try {
    res = await fetch(
      `${process.env.AUTH_API_URL}/planning/team/${session.user.id}?van=${van}&tot=${tot}`,
      {
        headers: {
          Authorization: `Bearer ${session.accessToken}`,
          'Content-Type': 'application/json',
        },
      },
    );
  } catch {
    return NextResponse.json({ error: 'Backend unreachable' }, { status: 502 });
  }

  if (res.status === 401 || res.status === 403) {
    return NextResponse.json({ error: 'token_expired' }, { status: 401 });
  }

  if (!res.ok) {
    return NextResponse.json({ error: 'server_error' }, { status: res.status });
  }

  const data = await res.json();
  return NextResponse.json(data);
}
