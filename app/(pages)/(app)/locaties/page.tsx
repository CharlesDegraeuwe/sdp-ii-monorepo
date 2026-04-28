'use client';

import React, { useState, useEffect } from 'react';
import { useSession } from 'next-auth/react';
import GoogleMaps from '@/app/(pages)/(app)/locaties/components/googlemaps';
import { SiteList } from '@/app/(pages)/(app)/locaties/components/SiteList';
import { SiteDetail } from '@/app/(pages)/(app)/locaties/components/SiteDetail';
import { Machine, Site, SiteTeamResponse, Team } from '@/types/types';
import BreadcrumbInit from '@/components/app/structuur/breadcrumb/BreadCrumbInit';

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

    const jwtToken = session?.accessToken;
    const werknemerId = session?.user?.id;

    console.log('TOKEN:', jwtToken);
    console.log('WERKNEMER ID:', werknemerId);

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

    const jwtToken = session?.accessToken;
    if (!jwtToken) {
      setIsLoadingDetails(false);
      return;
    }

    console.log(jwtToken);
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
    <main className="w-full h-full flex flex-row gap-5">
      <BreadcrumbInit
        pages={selectedSite ? ['locaties', selectedSite.naam] : ['locaties']}
      />
      <div className="w-1/4 border border-zinc-300 overflow-hidden rounded-3xl relative bg-white">
        <div
          className={`absolute inset-0 bg-white z-20 flex flex-col rounded-2xl transition-transform duration-300 ease-in-out ${selectedSite ? 'translate-x-0' : '-translate-x-full'}`}
        >
          {selectedSite && (
            <SiteDetail
              site={selectedSite}
              setSelectedSite={setSelectedSite}
              sites={sites}
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
      <div className="w-3/4 border border-zinc-300 overflow-hidden rounded-3xl">
        <GoogleMaps
          selectedSite={selectedSite}
          sites={sites}
          onMarkerClick={(site) => handleSiteClick(site as Site)}
        />
      </div>
    </main>
  );
}
