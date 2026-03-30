// proxy.ts
import { NextRequest, NextResponse } from 'next/server';
import { jwtVerify } from 'jose';

const secret = new TextEncoder().encode(process.env.AUTH_SECRET);

async function getSessionToken(request: NextRequest) {
    const cookieName = process.env.NODE_ENV === 'production'
        ? '__Secure-authjs.session-token'
        : 'authjs.session-token';
    const token = request.cookies.get(cookieName)?.value;
    if (!token) return null;

    try {
        const { payload } = await jwtVerify(token, secret, {
            algorithms: ['HS256'], // pas aan als je iets anders gebruikt
        });
        return payload;
    } catch {
        return null;
    }
}

export async function proxy(request: NextRequest) {
    const token = await getSessionToken(request);
    const { pathname } = request.nextUrl;

    const publicRoutes = ['/login', '/activeer', '/'];
    const authRoutes = ['/login', '/activeer'];

    const isPublicRoute = publicRoutes.some(route =>
        route === '/' ? pathname === '/' : pathname === route || pathname.startsWith(route + '/')
    );
    const isAuthRoute = authRoutes.some(route =>
        pathname === route || pathname.startsWith(route + '/')
    );

    const isLoggedIn = !!token;

    if (isAuthRoute && isLoggedIn) {
        return NextResponse.redirect(new URL('/overzicht', request.url));
    }

    if (!isPublicRoute && !isLoggedIn) {
        const loginUrl = new URL('/login', request.url);
        loginUrl.searchParams.set('callbackUrl', pathname);
        return NextResponse.redirect(loginUrl);
    }

    return NextResponse.next();
}

export const config = {
    matcher: ['/((?!api|_next/static|_next/image|favicon.ico|.*\\..*).*)',],
};