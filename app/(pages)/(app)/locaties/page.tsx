'use client';

import React, { useState, useEffect } from 'react';
import { useSession } from 'next-auth/react';
import GoogleMaps from '@/app/(pages)/(app)/locaties/components/googlemaps';
import { SiteList } from '@/app/(pages)/(app)/locaties/components/SiteList';
import { SiteDetail } from '@/app/(pages)/(app)/locaties/components/SiteDetail';
import {
  CustomSession,
  Machine,
  Site,
  SiteTeamResponse,
  Team,
} from '@/types/types';

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
      const [machineRes, siteteamRes] = await Promise.all([
        fetch('http://localhost:8080/api/machines', {
          headers: { Authorization: `Bearer ${jwtToken}` },
        }),
        fetch('http://localhost:8080/api/siteteams', {
          headers: { Authorization: `Bearer ${jwtToken}` },
        }),
      ]);

      let machinesForThisSite: Machine[] = [];
      if (machineRes.ok) {
        const allMachines: Machine[] = await machineRes.json();
        machinesForThisSite = allMachines.filter((m) => m.site?.id === site.id);
      }

      let teamsForThisSite: Team[] = [];
      if (siteteamRes.ok) {
        const allSiteteams: SiteTeamResponse[] = await siteteamRes.json();
        teamsForThisSite = allSiteteams
          .filter((st) => st.site?.id === site.id)
          .map((st) => st.team);
      }

      setSelectedSite((prev) =>
        prev
          ? { ...prev, machines: machinesForThisSite, teams: teamsForThisSite }
          : null,
      );
    } catch (error) {
      console.error(error);
    } finally {
      setIsLoadingDetails(false);
    }
  };

  return (
    <div className="absolute inset-0 overflow-hidden">
      <div className="relative w-full h-full z-999 pointer-events-none">
        <div className="absolute top-6 left-6 w-80 bottom-6 bg-white rounded-2xl shadow-lg border border-gray-200 overflow-hidden flex flex-col pointer-events-auto">
          <div
            className={`absolute inset-0 bg-white z-20 flex flex-col rounded-2xl transition-transform duration-300 ease-in-out ${selectedSite ? 'translate-x-0' : '-translate-x-[calc(100%+2rem)]'}`}
          >
            {selectedSite && (
              <SiteDetail
                site={selectedSite}
                isLoading={isLoadingDetails}
                onBack={() => setSelectedSite(null)}
              />
            )}
          </div>

          <SiteList
            sites={sites}
            isLoading={isLoading}
            onSiteClick={handleSiteClick}
          />
        </div>
      </div>
      <div className="absolute inset-0 z-10">
        <GoogleMaps
          sites={sites}
          onMarkerClick={(site) => handleSiteClick(site as Site)}
        />
      </div>
    </div>
  );
}
