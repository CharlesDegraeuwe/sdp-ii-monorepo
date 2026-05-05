'use client';

import React, { useState, useEffect } from 'react';
import { useSession } from 'next-auth/react';
import GoogleMaps from '@/components/app/locaties/googlemaps';
import { SiteList } from '@/components/app/locaties/SiteList';
import { SiteDetail } from '@/components/app/locaties/SiteDetail';
import { Machine, Site, Team } from '@/types/types';
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

  useEffect(() => {
    if (!selectedSite || selectedSite.machines !== undefined) return;

    const jwtToken = session?.accessToken;
    const werknemerId = session?.user?.id;

    if (!jwtToken || !werknemerId) return;

    let isMounted = true;
    setIsLoadingDetails(true);

    const fetchDetails = async () => {
      try {
        const [machineRes, teamRes, werknemerTeamsRes] = await Promise.all([
          fetch(`http://localhost:8080/api/sites/${selectedSite.id}/machines`, {
            headers: { Authorization: `Bearer ${jwtToken}` },
          }),
          fetch(`http://localhost:8080/api/teams/site/${selectedSite.id}`, {
            headers: { Authorization: `Bearer ${jwtToken}` },
          }),
          fetch(`http://localhost:8080/api/teams/werknemer/${werknemerId}`, {
            headers: { Authorization: `Bearer ${jwtToken}` },
          }),
        ]);

        let machinesForThisSite: Machine[] = [];
        if (machineRes.ok) {
          machinesForThisSite = await machineRes.json();
        }

        let teamsForThisSite: Team[] = [];
        if (teamRes.ok && werknemerTeamsRes.ok) {
          const siteTeams: Team[] = await teamRes.json();
          const werknemerTeams: Team[] = await werknemerTeamsRes.json();

          teamsForThisSite = siteTeams.filter((st) =>
            werknemerTeams.some((wt) => wt.id === st.id),
          );
        }

        if (isMounted) {
          setSelectedSite((prev) =>
            prev && prev.id === selectedSite.id
              ? {
                  ...prev,
                  machines: machinesForThisSite,
                  teams: teamsForThisSite,
                }
              : prev,
          );
        }
      } catch (error) {
        console.error(error);
      } finally {
        if (isMounted) setIsLoadingDetails(false);
      }
    };

    fetchDetails();

    return () => {
      isMounted = false;
    };
  }, [selectedSite, session?.accessToken, session?.user?.id]);

  const handleSiteClick = (site: Site) => {
    setSelectedSite(site);
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
