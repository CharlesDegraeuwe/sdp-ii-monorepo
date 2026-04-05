import { WerknemerUser } from './types';

declare module 'next-auth' {
  // eslint-disable-next-line @typescript-eslint/no-empty-object-type
  interface User extends WerknemerUser {}

  interface Session {
    user: Omit<WerknemerUser, 'accessToken'>;
    accessToken: string;
  }
}

declare module 'next-auth/jwt' {
  interface JWT {
    accessToken: string;
    user: Omit<WerknemerUser, 'accessToken'>;
  }
}
