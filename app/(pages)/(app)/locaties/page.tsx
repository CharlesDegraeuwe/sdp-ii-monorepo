'use client';

import React, { useState, useEffect } from 'react';
import { useSession } from 'next-auth/react';
import GoogleMaps from '@/app/(pages)/(app)/locaties/components/googlemaps';
import { Input } from '@/components/design system/Input';

interface Machine {
  id: number;
  naam: string;
  status: string;
  site?: { id: number };
}

interface Team {
  id: number;
  naam: string;
}

interface Site {
  id: number;
  naam: string;
  locatie: string;
  capaciteit: number;
  status: string;
  machines?: Machine[];
  teams?: Team[];
}

interface CustomSession {
  user?: { token?: string; id?: number };
  accessToken?: string;
}

interface SiteTeamResponse {
  site?: { id: number };
  team: Team;
}

export default function Page() {
  const [sites, setSites] = useState<Site[]>([]);
  const [isLoading, setIsLoading] = useState(true);
  const [selectedSite, setSelectedSite] = useState<Site | null>(null);
  const [isLoadingDetails, setIsLoadingDetails] = useState(false);

  const { data: session, status } = useSession();

  useEffect(() => {
    if (status === 'loading') return;

    if (status === 'unauthenticated') {
      setIsLoading(false);
      return;
    }

    const currentSession = session as CustomSession | null;
    const jwtToken = currentSession?.user?.token || currentSession?.accessToken;
    const werknemerId = currentSession?.user?.id;

    if (!jwtToken || !werknemerId) {
      setIsLoading(false);
      return;
    }

    fetch(`http://localhost:8080/api/sites/werknemer/${werknemerId}`, {
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
        console.error(error);
        setIsLoading(false);
      });
  }, [session, status]);

  const handleSiteClick = async (site: Site) => {
    setSelectedSite(site);
    setIsLoadingDetails(true);

    const currentSession = session as CustomSession | null;
    const jwtToken = currentSession?.user?.token || currentSession?.accessToken;

    if (!jwtToken) {
      setIsLoadingDetails(false);
      return;
    }

    try {
      let machinesForThisSite: Machine[] = [];
      const machineRes = await fetch('http://localhost:8080/api/machines', {
        headers: { Authorization: `Bearer ${jwtToken}` },
      });
      if (machineRes.ok) {
        const allMachines: Machine[] = await machineRes.json();
        machinesForThisSite = allMachines.filter((m) => m.site?.id === site.id);
      }

      let teamsForThisSite: Team[] = [];
      const siteteamRes = await fetch('http://localhost:8080/api/siteteams', {
        headers: { Authorization: `Bearer ${jwtToken}` },
      });
      if (siteteamRes.ok) {
        const allSiteteams: SiteTeamResponse[] = await siteteamRes.json();
        teamsForThisSite = allSiteteams
          .filter((st) => st.site?.id === site.id)
          .map((st) => st.team);
      }

      setSelectedSite((prev) =>
        prev
          ? {
              ...prev,
              machines: machinesForThisSite,
              teams: teamsForThisSite,
            }
          : null,
      );
    } catch (error) {
      console.error(error);
    } finally {
      setIsLoadingDetails(false);
    }
  };

  return (
    <div className="relative w-full h-[calc(100vh-80px)]">
      <div className="absolute top-[100px] bottom-6 left-6 w-[350px] bg-white flex flex-col rounded-2xl shadow-md border border-gray-200 overflow-hidden z-10">
        <div
          className={`absolute inset-0 bg-white z-20 flex flex-col transition-transform duration-300 ease-in-out ${
            selectedSite ? 'translate-x-0' : '-translate-x-full'
          }`}
        >
          {selectedSite && (
            <div className="flex flex-col h-full">
              <div className="p-4 border-b border-gray-100 flex items-center gap-3 bg-gray-50/50 shrink-0">
                <button
                  onClick={() => setSelectedSite(null)}
                  className="p-2 hover:bg-gray-200 rounded-full transition-colors flex items-center justify-center"
                >
                  <svg
                    className="w-5 h-5 text-gray-700"
                    fill="none"
                    stroke="currentColor"
                    viewBox="0 0 24 24"
                  >
                    <path
                      strokeLinecap="round"
                      strokeLinejoin="round"
                      strokeWidth={2}
                      d="M10 19l-7-7m0 0l7-7m-7 7h18"
                    />
                  </svg>
                </button>
                <h2 className="font-bold text-gray-800 text-lg">
                  Locatie Details
                </h2>
              </div>

              <div className="p-6 flex-1 overflow-y-auto flex flex-col gap-6">
                <div>
                  <h3 className="text-2xl font-extrabold text-gray-900 leading-tight">
                    {selectedSite.naam}
                  </h3>
                  <div className="mt-3 inline-block">
                    <span
                      className={`px-3 py-1 rounded-full text-xs font-bold uppercase tracking-wide ${
                        selectedSite.status === 'Actief'
                          ? 'bg-green-100 text-green-700 border border-green-200'
                          : selectedSite.status === 'In onderhoud'
                            ? 'bg-orange-100 text-orange-700 border border-orange-200'
                            : 'bg-red-100 text-red-700 border border-red-200'
                      }`}
                    >
                      {selectedSite.status}
                    </span>
                  </div>
                </div>

                <div className="w-full h-px bg-gray-100"></div>

                <div className="flex flex-col gap-6">
                  <div className="flex gap-3 items-start">
                    <svg
                      className="w-5 h-5 text-gray-400 mt-0.5 shrink-0"
                      fill="none"
                      stroke="currentColor"
                      viewBox="0 0 24 24"
                    >
                      <path
                        strokeLinecap="round"
                        strokeLinejoin="round"
                        strokeWidth={2}
                        d="M17.657 16.657L13.414 20.9a1.998 1.998 0 01-2.827 0l-4.243-4.243a8 8 0 1111.314 0z"
                      />
                      <path
                        strokeLinecap="round"
                        strokeLinejoin="round"
                        strokeWidth={2}
                        d="M15 11a3 3 0 11-6 0 3 3 0 016 0z"
                      />
                    </svg>
                    <div>
                      <p className="text-xs text-gray-400 uppercase tracking-wider font-semibold">
                        Adres / Locatie
                      </p>
                      <p className="text-gray-800 font-medium mt-1 leading-relaxed">
                        {selectedSite.locatie}
                      </p>
                    </div>
                  </div>

                  <div className="flex gap-3 items-start">
                    <svg
                      className="w-5 h-5 text-gray-400 mt-0.5 shrink-0"
                      fill="none"
                      stroke="currentColor"
                      viewBox="0 0 24 24"
                    >
                      <path
                        strokeLinecap="round"
                        strokeLinejoin="round"
                        strokeWidth={2}
                        d="M12 4.354a4 4 0 110 5.292M15 21H3v-1a6 6 0 0112 0v1zm0 0h6v-1a6 6 0 00-9-5.197M13 7a4 4 0 11-8 0 4 4 0 018 0z"
                      />
                    </svg>
                    <div>
                      <p className="text-xs text-gray-400 uppercase tracking-wider font-semibold">
                        Capaciteit
                      </p>
                      <p className="text-gray-800 font-medium mt-1">
                        {selectedSite.capaciteit
                          ? `${selectedSite.capaciteit} personen`
                          : 'Geen data beschikbaar'}
                      </p>
                    </div>
                  </div>

                  <div className="flex gap-3 items-start">
                    <svg
                      className="w-5 h-5 text-gray-400 mt-0.5 shrink-0"
                      fill="none"
                      stroke="currentColor"
                      viewBox="0 0 24 24"
                    >
                      <path
                        strokeLinecap="round"
                        strokeLinejoin="round"
                        strokeWidth={2}
                        d="M10.325 4.317c.426-1.756 2.924-1.756 3.35 0a1.724 1.724 0 002.573 1.066c1.543-.94 3.31.826 2.37 2.37a1.724 1.724 0 001.065 2.572c1.756.426 1.756 2.924 0 3.35a1.724 1.724 0 00-1.066 2.573c.94 1.543-.826 3.31-2.37 2.37a1.724 1.724 0 00-2.572 1.065c-.426 1.756-2.924 1.756-3.35 0a1.724 1.724 0 00-2.573-1.066c-1.543.94-3.31-.826-2.37-2.37a1.724 1.724 0 00-1.065-2.572c-1.756-.426-1.756-2.924 0-3.35a1.724 1.724 0 001.066-2.573c-.94-1.543.826-3.31 2.37-2.37.996.608 2.296.07 2.572-1.065z"
                      />
                      <path
                        strokeLinecap="round"
                        strokeLinejoin="round"
                        strokeWidth={2}
                        d="M15 12a3 3 0 11-6 0 3 3 0 016 0z"
                      />
                    </svg>
                    <div className="w-full">
                      <p className="text-xs text-gray-400 uppercase tracking-wider font-semibold mb-2">
                        Machines
                      </p>

                      {isLoadingDetails ? (
                        <p className="text-sm text-gray-400 italic bg-gray-50 p-2.5 rounded-lg border border-gray-100 animate-pulse">
                          Machines ophalen...
                        </p>
                      ) : selectedSite.machines &&
                        selectedSite.machines.length > 0 ? (
                        <div className="flex flex-col gap-2">
                          {selectedSite.machines.map((machine) => (
                            <div
                              key={machine.id}
                              className="flex justify-between items-center bg-gray-50 border border-gray-100 p-2.5 rounded-lg shadow-sm"
                            >
                              <span className="text-sm font-semibold text-gray-700">
                                {machine.naam}
                              </span>
                              <span
                                className={`px-2.5 py-1 rounded-full text-[10px] font-bold uppercase tracking-wide ${
                                  machine.status === 'Actief'
                                    ? 'bg-green-100 text-green-700'
                                    : machine.status === 'In onderhoud'
                                      ? 'bg-orange-100 text-orange-700'
                                      : 'bg-red-100 text-red-700'
                                }`}
                              >
                                {machine.status}
                              </span>
                            </div>
                          ))}
                        </div>
                      ) : (
                        <p className="text-sm text-gray-500 italic bg-gray-50 p-2.5 rounded-lg border border-gray-100">
                          Geen machines geregistreerd.
                        </p>
                      )}
                    </div>
                  </div>

                  <div className="flex gap-3 items-start">
                    <svg
                      className="w-5 h-5 text-gray-400 mt-0.5 shrink-0"
                      fill="none"
                      stroke="currentColor"
                      viewBox="0 0 24 24"
                    >
                      <path
                        strokeLinecap="round"
                        strokeLinejoin="round"
                        strokeWidth={2}
                        d="M17 20h5v-2a3 3 0 00-5.356-1.857M17 20H7m10 0v-2c0-.656-.126-1.283-.356-1.857M7 20H2v-2a3 3 0 015.356-1.857M7 20v-2c0-.656.126-1.283.356-1.857m0 0a5.002 5.002 0 019.288 0M15 7a3 3 0 11-6 0 3 3 0 016 0zm6 3a2 2 0 11-4 0 2 2 0 014 0zM7 10a2 2 0 11-4 0 2 2 0 014 0z"
                      />
                    </svg>
                    <div className="w-full">
                      <p className="text-xs text-gray-400 uppercase tracking-wider font-semibold mb-2">
                        Gekoppelde Teams
                      </p>

                      {isLoadingDetails ? (
                        <p className="text-sm text-gray-400 italic bg-gray-50 p-2.5 rounded-lg border border-gray-100 animate-pulse">
                          Teams ophalen...
                        </p>
                      ) : selectedSite.teams &&
                        selectedSite.teams.length > 0 ? (
                        <div className="flex flex-wrap gap-2">
                          {selectedSite.teams.map((team, idx) => (
                            <span
                              key={team.id || idx}
                              className="px-3 py-1.5 bg-blue-50 text-blue-700 border border-blue-100 rounded-lg text-sm font-semibold shadow-sm flex items-center gap-1.5"
                            >
                              <div className="w-1.5 h-1.5 rounded-full bg-blue-500"></div>
                              {team.naam || `Team #${team.id}`}
                            </span>
                          ))}
                        </div>
                      ) : (
                        <p className="text-sm text-gray-500 italic bg-gray-50 p-2.5 rounded-lg border border-gray-100">
                          Geen teams aan deze site gekoppeld.
                        </p>
                      )}
                    </div>
                  </div>
                </div>
              </div>
            </div>
          )}
        </div>

        <div className="p-4 border-b border-gray-100 shrink-0">
          <Input placeholder={'zoeken...'} color={'white'} />
        </div>

        <div className="flex-1 overflow-y-auto p-4 flex flex-col gap-3">
          {isLoading ? (
            <p className="text-gray-500 text-sm text-center mt-4">
              Locaties laden...
            </p>
          ) : (
            sites.map((site) => (
              <div
                key={site.id}
                onClick={() => handleSiteClick(site)}
                className="p-3 border border-gray-200 rounded-lg cursor-pointer hover:bg-gray-50 hover:border-blue-200 transition-all shadow-sm bg-white group"
              >
                <div className="flex justify-between items-center">
                  <h3 className="font-bold text-gray-800 text-base group-hover:text-blue-600 transition-colors">
                    {site.naam}
                  </h3>
                  <svg
                    className="w-4 h-4 text-gray-300 group-hover:text-blue-500 transition-colors"
                    fill="none"
                    stroke="currentColor"
                    viewBox="0 0 24 24"
                  >
                    <path
                      strokeLinecap="round"
                      strokeLinejoin="round"
                      strokeWidth={2}
                      d="M9 5l7 7-7 7"
                    />
                  </svg>
                </div>
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
      </div>

      <div className="absolute top-[100px] bottom-6 right-6 left-[400px] bg-gray-100 rounded-2xl shadow-md border border-gray-200 overflow-hidden z-10">
        <GoogleMaps
          sites={sites}
          onMarkerClick={(site) => handleSiteClick(site as Site)}
        />
      </div>
    </div>
  );
}
