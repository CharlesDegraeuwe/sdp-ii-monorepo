import NextAuth from 'next-auth';
import Credentials from 'next-auth/providers/credentials';

export const {
    handlers,
    signIn,
    signOut,
    auth,
} = NextAuth({
    providers: [
        Credentials({
            credentials: {
                email: { label: 'Email', type: 'email' },
                password: { label: 'Password', type: 'password' },
            },
            authorize: async (credentials) => {
                try {
                    const loginResponse = await fetch(`${process.env.AUTH_API_URL}/sessions`, {
                        method: 'POST',
                        headers: { 'Content-Type': 'application/json' },
                        body: JSON.stringify({
                            email: credentials?.email,
                            password: credentials?.password,
                        }),
                    });

                    if (!loginResponse.ok) return null;

                    const { token } = await loginResponse.json();

                    const userResponse = await fetch(`${process.env.AUTH_API_URL}/users/me`, {
                        headers: { 'Authorization': `Bearer ${token}` },
                    });

                    if (!userResponse.ok) return null;

                    const user = await userResponse.json();
                    return { ...user, accessToken: token };

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
        jwt: async ({ token, user, account }) => {
            if (user && account?.provider === 'credentials') {
                token.accessToken = (user as any).accessToken;
                token.user = {
                    id: user.id!,
                    email: user.email!,
                    naam: (user as any).naam,
                    voornaam: (user as any).voornaam,
                    telefoonnummer: (user as any).telefoonnummer,
                    geboortedatum: (user as any).geboortedatum,
                    rol: (user as any).rol,
                    status: (user as any).status,
                };
            }
            return token;
        },

        session: async ({ session, token }) => {
            return {
                ...session,
                user: token.user as {
                    id: string;
                    email: string;
                    naam: string;
                    voornaam: string;
                    telefoonnummer: string;
                    geboortedatum: string;
                    rol: string;
                    status: string;
                },
                accessToken: token.accessToken,
            };
        },
    },
});