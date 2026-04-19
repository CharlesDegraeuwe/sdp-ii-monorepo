import NextAuth from 'next-auth';

declare module 'next-auth' {
  interface Session {
    user: {
      id: string;
      naam: string;
      voornaam: string;
      email: string;
      telefoonnummer: string;
      geboortedatum: string;
      rol: string;
      status: string;
    };
    accessToken: string;
  }

  interface User {
    id: string;
    naam: string;
    voornaam: string;
    email: string;
    telefoonnummer: string;
    geboortedatum: string;
    rol: string;
    status: string;
    accessToken: string;
  }
}
