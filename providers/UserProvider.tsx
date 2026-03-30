"use client"
import { createContext, useContext, ReactNode } from "react";
import { useSession } from "next-auth/react";

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
    isModerator: boolean;
}

const UserContext = createContext<UserContextType>({ user: null, isLoading: true, isModerator: false });

export function UserProvider({ children }: { children: ReactNode }) {
    const { data: session, status } = useSession();

    const user = session?.user ?? null;
    const isModerator = !!user?.rol && ["Admin", "Manager"].includes(user.rol);

    return (
        <UserContext.Provider value={{
            user,
            isLoading: status === "loading",
            isModerator
        }}>
            {children}
        </UserContext.Provider>
    );
}


export const useUser = () => useContext(UserContext);