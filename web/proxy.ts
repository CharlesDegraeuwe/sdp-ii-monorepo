import { auth } from '@/auth';
import { NextResponse } from 'next/server';

export const proxy = auth((request) => {
  const { pathname } = request.nextUrl;
  const isLoggedIn = !!request.auth;

  const publicRoutes = ['/login', '/activeer', '/'];
  const authRoutes = ['/login', '/activeer'];

  const isPublicRoute = publicRoutes.some((route) =>
    route === '/'
      ? pathname === '/'
      : pathname === route || pathname.startsWith(route + '/'),
  );
  const isAuthRoute = authRoutes.some(
    (route) => pathname === route || pathname.startsWith(route + '/'),
  );

  if (isAuthRoute && isLoggedIn) {
    return NextResponse.redirect(new URL('/overzicht', request.url));
  }

  if (!isPublicRoute && !isLoggedIn) {
    const loginUrl = new URL('/login', request.url);
    loginUrl.searchParams.set('callbackUrl', pathname);
    return NextResponse.redirect(loginUrl);
  }

  return NextResponse.next();
});

export const config = {
  matcher: ['/((?!api|_next/static|_next/image|favicon.ico|.*\\..*).*)'],
};
