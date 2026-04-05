import Credentials from 'next-auth/providers/credentials';
import NextAuth from 'next-auth';

export const { handlers, signIn, signOut, auth } = NextAuth({
  providers: [
    Credentials({
      credentials: {
        email: { label: 'Email', type: 'email' },
        password: { label: 'Password', type: 'password' },
      },
      authorize: async (credentials) => {
        try {
          const loginResponse = await fetch(
            `${process.env.AUTH_API_URL}/werknemers/login`,
            {
              method: 'POST',
              headers: { 'Content-Type': 'application/json' },
              body: JSON.stringify({
                email: credentials?.email,
                wachtwoord: credentials?.password,
              }),
            },
          );

          if (!loginResponse.ok) return null;

          const data = await loginResponse.json();

          return {
            id: String(data.werknemer.id),
            email: data.werknemer.email,
            naam: data.werknemer.naam,
            voornaam: data.werknemer.voornaam,
            telefoonnummer: data.werknemer.telefoonnummer,
            geboortedatum: data.werknemer.geboortedatum,
            rol: data.werknemer.rol,
            status: data.werknemer.status,
            accessToken: data.token,
          };
        } catch (error) {
          console.error('Auth error:', error);
          return null;
        }
      },
    }),
  ],

  pages: {
    signIn: '/login',
  },

  session: {
    strategy: 'jwt',
    maxAge: 7 * 24 * 60 * 60,
  },

  callbacks: {
    authorized: async ({ auth, request }) => {
      const { pathname } = request.nextUrl;
      const isLoggedIn = !!auth;
      const authRoutes = ['/login', '/activeer'];
      const publicRoutes = ['/login', '/activeer', '/'];

      const isAuthRoute = authRoutes.some(
        (r) => pathname === r || pathname.startsWith(r + '/'),
      );
      const isPublicRoute = publicRoutes.some((r) =>
        r === '/'
          ? pathname === '/'
          : pathname === r || pathname.startsWith(r + '/'),
      );

      if (isAuthRoute && isLoggedIn) {
        return Response.redirect(new URL('/overzicht', request.url));
      }

      if (!isPublicRoute && !isLoggedIn) return false;

      return true;
    },

    jwt: async ({ token, user, account }) => {
      if (user && account?.provider === 'credentials') {
        token.accessToken = user.accessToken;
        token.user = {
          id: user.id!,
          email: user.email!,
          naam: user.naam,
          voornaam: user.voornaam,
          telefoonnummer: user.telefoonnummer,
          geboortedatum: user.geboortedatum,
          rol: user.rol,
          status: user.status,
        };
      }
      return token;
    },

    session: async ({ session, token }) => {
      session.user = token.user as typeof session.user;
      session.accessToken = token.accessToken as string;
      return session;
    },
  },
});
