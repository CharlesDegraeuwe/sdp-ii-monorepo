'use client';

import React, { useState, useEffect, useMemo } from 'react';
import { useSession } from 'next-auth/react';
import GoogleMaps from '@/components/app/locaties/googlemaps';
import { SiteList } from '@/components/app/locaties/SiteList';
import { SiteDetail } from '@/components/app/locaties/SiteDetail';
import { Machine, Site, Team } from '@/types/types';
import BreadcrumbInit from '@/components/overig/structuur/breadcrumb/BreadCrumbInit';
import {
  IoLocationOutline,
  IoCheckmarkCircleOutline,
  IoPeopleOutline,
  IoWarningOutline,
} from 'react-icons/io5';

export default function Page() {
  const [sites, setSites] = useState<Site[]>([]);
  const [isLoading, setIsLoading] = useState(true);
  const [selectedSite, setSelectedSite] = useState<Site | null>(null);
  const [isLoadingDetails, setIsLoadingDetails] = useState(false);

  const [totalAssignedEmployees, setTotalAssignedEmployees] = useState<
    number | string
  >('...');
  const [sitesMetTekort, setSitesMetTekort] = useState<number | string>('...');

  const { data: session, status } = useSession();

  useEffect(() => {
    if (status === 'loading') return;

    if (status === 'unauthenticated') {
      setIsLoading(false);
      return;
    }

    const jwtToken = (session as unknown as { accessToken?: string })
      ?.accessToken;
    const werknemerId = (session as unknown as { user?: { id?: number } })?.user
      ?.id;

    if (!jwtToken || !werknemerId) {
      setIsLoading(false);
      return;
    }

    Promise.all([
      fetch(`http://localhost:8080/api/sites/werknemer/${werknemerId}`, {
        headers: { Authorization: `Bearer ${jwtToken}` },
      }),
      fetch(`http://localhost:8080/api/sites/werknemer/${werknemerId}/stats`, {
        headers: { Authorization: `Bearer ${jwtToken}` },
      }),
    ])
      .then(async ([sitesRes, statsRes]) => {
        if (sitesRes.ok) setSites(await sitesRes.json());

        if (statsRes.ok) {
          const stats = await statsRes.json();
          setTotalAssignedEmployees(stats.totaleBezetting);
          setSitesMetTekort(stats.huidigeAfwezigen);
        }
        setIsLoading(false);
      })
      .catch((error) => {
        console.error(error);
        setIsLoading(false);
      });
  }, [session, status]);

  useEffect(() => {
    if (!selectedSite || selectedSite.machines !== undefined) return;

    const jwtToken = (session as unknown as { accessToken?: string })
      ?.accessToken;
    if (!jwtToken) return;

    let isMounted = true;
    setIsLoadingDetails(true);

    const fetchDetails = async () => {
      try {
        const [machineRes, teamRes] = await Promise.all([
          fetch(`http://localhost:8080/api/sites/${selectedSite.id}/machines`, {
            headers: { Authorization: `Bearer ${jwtToken}` },
          }),
          fetch(`http://localhost:8080/api/teams/site/${selectedSite.id}`, {
            headers: { Authorization: `Bearer ${jwtToken}` },
          }),
        ]);

        let machinesForThisSite: Machine[] = [];
        if (machineRes.ok) {
          machinesForThisSite = await machineRes.json();
        }

        let teamsForThisSite: Team[] = [];
        if (teamRes.ok) {
          teamsForThisSite = await teamRes.json();
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
  }, [selectedSite, session]);

  const handleSiteClick = (site: Site) => {
    setSelectedSite(site);
  };

  const totalSites = sites.length;

  const activeSites = useMemo(() => {
    const active = sites.filter(
      (s: Site) => s.status?.toLowerCase() === 'actief',
    ).length;
    return active > 0 ? active : sites.length;
  }, [sites]);

  return (
    <main className="w-full h-full flex flex-col gap-5">
      <div className="shrink-0">
        <BreadcrumbInit
          pages={selectedSite ? ['locaties', selectedSite.naam] : ['locaties']}
        />
      </div>

      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-5 shrink-0">
        <div className="bg-white p-5 rounded-3xl border border-zinc-300 shadow-sm flex items-center gap-4">
          <div className="p-3 bg-blue-50 text-blue-600 rounded-xl shrink-0">
            <IoLocationOutline className="w-6 h-6" />
          </div>
          <div>
            <p className="text-sm text-gray-500 font-medium">Totale Locaties</p>
            <p className="text-2xl font-bold text-gray-800">
              {isLoading ? '...' : totalSites}
            </p>
          </div>
        </div>

        <div className="bg-white p-5 rounded-3xl border border-zinc-300 shadow-sm flex items-center gap-4">
          <div className="p-3 bg-emerald-50 text-emerald-600 rounded-xl shrink-0">
            <IoCheckmarkCircleOutline className="w-6 h-6" />
          </div>
          <div>
            <p className="text-sm text-gray-500 font-medium">
              Actief Operationeel
            </p>
            <p className="text-2xl font-bold text-gray-800">
              {isLoading ? '...' : activeSites}
            </p>
          </div>
        </div>

        <div className="bg-white p-5 rounded-3xl border border-zinc-300 shadow-sm flex items-center gap-4">
          <div className="p-3 bg-purple-50 text-purple-600 rounded-xl shrink-0">
            <IoPeopleOutline className="w-6 h-6" />
          </div>
          <div>
            <p className="text-sm text-gray-500 font-medium">
              Werknemers (Totaal)
            </p>
            <p className="text-2xl font-bold text-gray-800">
              {totalAssignedEmployees}
            </p>
          </div>
        </div>

        <div className="bg-white p-5 rounded-3xl border border-zinc-300 shadow-sm flex items-center gap-4">
          <div className="p-3 bg-amber-50 text-amber-600 rounded-xl shrink-0">
            <IoWarningOutline className="w-6 h-6" />
          </div>
          <div>
            <p className="text-sm text-gray-500 font-medium">Afwezigen</p>
            <p className="text-2xl font-bold text-gray-800">{sitesMetTekort}</p>
          </div>
        </div>
      </div>

      <div className="flex flex-col md:flex-row gap-5 flex-1 min-h-0">
        <div className="w-full md:w-1/4 h-64 md:h-full border border-zinc-300 overflow-hidden rounded-3xl relative bg-white flex-shrink-0">
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

        <div className="w-full md:w-3/4 h-72 md:h-full border border-zinc-300 overflow-hidden rounded-3xl">
          <GoogleMaps
            selectedSite={selectedSite}
            sites={sites}
            onMarkerClick={(site) => handleSiteClick(site as Site)}
          />
        </div>
      </div>
    </main>
  );
}
