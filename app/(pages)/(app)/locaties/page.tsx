/* eslint-disable react-hooks/set-state-in-effect */
'use client';

import React, { useState, useEffect } from 'react';
import { useSession } from 'next-auth/react';
import GoogleMaps from '@/app/(pages)/(app)/locaties/components/googlemaps';
import { Container } from '@/components/design system/Container';
import { Input } from '@/components/design system/Input';

interface Site {
  id: number;
  naam: string;
  locatie: string;
  capaciteit: number;
  status: string;
}

export default function Page() {
  const [sites, setSites] = useState<Site[]>([]);
  const [isLoading, setIsLoading] = useState(true);

  const { data: session, status } = useSession();

  useEffect(() => {
    if (status === 'loading') return;

    if (status === 'unauthenticated') {
      console.error('Niet ingelogd!');
      setIsLoading(false);
      return;
    }
    interface CustomSession {
      user?: { token?: string };
      accessToken?: string;
    }

    const currentSession = session as CustomSession | null;
    const jwtToken = currentSession?.user?.token || currentSession?.accessToken;

    if (!jwtToken) {
      console.error('Geen JWT token gevonden in de NextAuth sessie!');
      setIsLoading(false);
      return;
    }

    fetch('http://localhost:8080/api/sites', {
      method: 'GET',
      headers: {
        'Content-Type': 'application/json',
        Authorization: `Bearer ${jwtToken}`,
      },
    })
      .then((response) => {
        if (!response.ok) throw new Error(`Foutcode: ${response.status}`);
        return response.json();
      })
      .then((data) => {
        setSites(data);
        setIsLoading(false);
      })
      .catch((error) => {
        console.error('Fout bij ophalen:', error);
        setIsLoading(false);
      });
  }, [session, status]);

  return (
    <div className={'relative z-0 w-full h-full flex'}>
      <div className="flex flex-col gap-5 absolute h-full w-[20%] min-w-[250px] z-40 left-5 top-30">
        <Container
          className={
            'bg-white h-[75%] pt-5 flex flex-col gap-4 overflow-hidden'
          }
        >
          <div className="px-4 shrink-0">
            <Input placeholder={'zoeken...'} color={'white'} />
          </div>

          <div className="flex flex-col gap-3 px-4 pb-4 overflow-y-auto">
            {}
            {isLoading ? (
              <p className="text-gray-500 text-sm text-center">
                Locaties laden...
              </p>
            ) : (
              sites.map((site) => (
                <div
                  key={site.id}
                  onClick={() => console.log(`Je klikte op: ${site.naam}`)}
                  className="p-3 border border-gray-200 rounded-lg cursor-pointer hover:bg-gray-100 transition-colors shadow-sm"
                >
                  <h3 className="font-bold text-gray-800 text-base">
                    {site.naam}
                  </h3>
                  <p
                    className={`text-sm mt-1 font-medium ${
                      site.status === 'Actief'
                        ? 'text-green-600'
                        : site.status === 'In onderhoud'
                          ? 'text-orange-500'
                          : 'text-red-500'
                    }`}
                  >
                    {site.status}
                  </p>
                </div>
              ))
            )}
          </div>
        </Container>
      </div>

      <GoogleMaps />
    </div>
  );
}
