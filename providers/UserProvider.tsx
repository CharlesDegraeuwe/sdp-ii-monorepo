'use client';
import { createContext, useContext, ReactNode } from 'react';
import { useSession } from 'next-auth/react';

interface User {
  id: string;
  naam: string;
  voornaam: string;
  email: string;
  telefoonnummer: string;
  geboortedatum: string;
  rol: string;
  status: string;
}

interface UserContextType {
  user: User | null;
  isLoading: boolean;
  isAdmin: boolean;
  isManager: boolean;
  isSupervisor: boolean;
  isModerator: boolean;
}

const UserContext = createContext<UserContextType>({
  user: null,
  isLoading: true,
  isAdmin: false,
  isManager: false,
  isSupervisor: false,
  isModerator: false,
});

export function UserProvider({ children }: { children: ReactNode }) {
  const { data: session, status } = useSession();

  const user = session?.user ?? null;
  const isAdmin = user?.rol === 'Admin';
  const isManager = user?.rol === 'Manager';
  const isSupervisor = user?.rol === 'Supervisor';
  const isModerator = isAdmin || isManager;

  return (
    <UserContext.Provider
      value={{
        user,
        isLoading: status === 'loading',
        isAdmin,
        isManager,
        isSupervisor,
        isModerator,
      }}
    >
      {children}
    </UserContext.Provider>
  );
}

export const useUser = () => useContext(UserContext);
