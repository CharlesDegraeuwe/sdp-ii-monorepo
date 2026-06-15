import { redirect } from 'next/navigation';
import { auth } from '@/auth';
import { cookies } from 'next/headers';

const startViewMap: Record<string, string> = {
  dashboard: '/overzicht',
  employees: '/teams/werknemers',
  planning: '/planner',
  teams: '/teams',
};

export default async function Rootpage() {
  const session = await auth();

  if (session) {
    const cookieStore = await cookies();
    const startView = cookieStore.get('sdp2_start_view')?.value ?? 'dashboard';
    redirect(startViewMap[startView] ?? '/overzicht');
  } else {
    redirect('/login');
  }
}
