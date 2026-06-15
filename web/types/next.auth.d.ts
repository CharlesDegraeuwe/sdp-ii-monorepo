import 'next-auth';
import 'next-auth/jwt';
import '@auth/core/types';

declare module '@auth/core/types' {
  interface User {
    naam?: string;
    voornaam?: string;
    telefoonnummer?: string;
    geboortedatum?: string;
    rol?: string;
    status?: string;
    accessToken?: string;
  }

  interface Session {
    user: {
      id: string;
      email: string;
      naam: string;
      voornaam: string;
      telefoonnummer: string;
      geboortedatum: string;
      rol: string;
      status: string;
    };
    accessToken: string;
  }
}

declare module 'next-auth' {
  interface User {
    naam?: string;
    voornaam?: string;
    telefoonnummer?: string;
    geboortedatum?: string;
    rol?: string;
    status?: string;
    accessToken?: string;
  }

  interface Session {
    user: {
      id: string;
      email: string;
      naam: string;
      voornaam: string;
      telefoonnummer: string;
      geboortedatum: string;
      rol: string;
      status: string;
    };
    accessToken: string;
  }
}

declare module 'next-auth/jwt' {
  interface JWT {
    accessToken?: string;
    user?: {
      id: string;
      email: string;
      naam: string;
      voornaam: string;
      telefoonnummer: string;
      geboortedatum: string;
      rol: string;
      status: string;
    };
  }
}
