'use client';

import React, { useState, useEffect, useCallback } from 'react';
import { useSession } from 'next-auth/react';
import { Container } from '@/components/design-system/Container';
import {
  IoAddCircleOutline,
  IoServerOutline,
  IoPeopleOutline,
  IoTimeOutline,
  IoBusinessOutline,
} from 'react-icons/io5';
import Link from '@/components/design-system/Link/Link';
import CreeerGebruikerModal from '@/components/app/admin/creeer-gebruiker/CreeerGebruikerModal';
import BreadcrumbInit from '@/components/overig/structuur/breadcrumb/BreadCrumbInit';

const links = [
  {
    isModal: true,
    href: '#',
    label: 'Creëer gebruiker',
    subtext: 'Vlug een nieuwe werknemer of manager toevoegen',
    icon: <IoAddCircleOutline className="w-5 h-5" />,
  },
  {
    isModal: false,
    href: '/admin/beheer-gebruikers',
    label: 'Beheer werknemers',
    subtext: 'Rechten, details en rollen wijzigen',
    icon: <IoServerOutline className="w-5 h-5" />,
  },
];

interface ActiviteitLog {
  id: number;
  werknemer: { voornaam?: string; naam?: string };
  type: string;
  tabel: string;
  timestamp: string;
  beschrijving: string;
}

export default function Page() {
  const { data: session, status } = useSession();

  const [totalEmployees, setTotalEmployees] = useState<number | null>(null);
  const [activeSitesPercentage, setActiveSitesPercentage] = useState<
    number | null
  >(null);
  const [absentEmployees, setAbsentEmployees] = useState<number | null>(null);
  const [recenteLogs, setRecenteLogs] = useState<ActiviteitLog[]>([]);

  const [isModalOpen, setIsModalOpen] = useState(false);

  const fetchDashboardData = useCallback(async () => {
    const jwtToken = (session as unknown as { accessToken?: string })
      ?.accessToken;
    if (!jwtToken) return;

    try {
      const [totaalRes, sitesRes, afwezigRes, logsRes] = await Promise.all([
        fetch('http://localhost:8080/api/werknemers/totaal', {
          headers: { Authorization: `Bearer ${jwtToken}` },
        }),
        fetch('http://localhost:8080/api/sites/actief-percentage', {
          headers: { Authorization: `Bearer ${jwtToken}` },
        }),
        fetch('http://localhost:8080/api/afwezigheid/huidig', {
          headers: { Authorization: `Bearer ${jwtToken}` },
        }),
        fetch('http://localhost:8080/api/logs/recent', {
          headers: { Authorization: `Bearer ${jwtToken}` },
        }),
      ]);

      if (totaalRes.ok) setTotalEmployees(await totaalRes.json());
      if (sitesRes.ok) setActiveSitesPercentage(await sitesRes.json());
      if (afwezigRes.ok) setAbsentEmployees(await afwezigRes.json());
      if (logsRes.ok) setRecenteLogs(await logsRes.json());
    } catch (error) {
      console.error(error);
    }
  }, [session]);

  useEffect(() => {
    const loadData = async () => {
      if (status === 'authenticated') {
        await fetchDashboardData();
      }
    };
    loadData();
  }, [status, fetchDashboardData]);

  const getLogColor = (type: string) => {
    switch (type?.toUpperCase()) {
      case 'CREATE':
        return 'bg-green-500';
      case 'DELETE':
        return 'bg-red-500';
      case 'UPDATE':
        return 'bg-blue-500';
      default:
        return 'bg-gray-400';
    }
  };

  const getActionText = (type: string) => {
    switch (type?.toUpperCase()) {
      case 'CREATE':
        return 'voegde toe: ';
      case 'DELETE':
        return 'verwijderde: ';
      case 'UPDATE':
        return 'deed een aanpassing: ';
      default:
        return 'actie: ';
    }
  };

  return (
    <div className="w-full h-full flex flex-col p-6 gap-6 relative">
      <BreadcrumbInit pages={['admin']} />

      <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
        <Container height="fit">
          <div className="flex items-center gap-4">
            <div className="p-3 bg-blue-50 text-blue-600 rounded-xl">
              <IoPeopleOutline className="w-6 h-6" />
            </div>
            <div>
              <p className="text-sm text-gray-500 font-medium">
                Totale Werknemers
              </p>
              <p className="text-2xl font-bold text-gray-800">
                {totalEmployees !== null ? totalEmployees : '...'}
              </p>
            </div>
          </div>
        </Container>

        <Container height="fit">
          <div className="flex items-center gap-4">
            <div className="p-3 bg-purple-50 text-purple-600 rounded-xl">
              <IoBusinessOutline className="w-6 h-6" />
            </div>
            <div>
              <p className="text-sm text-gray-500 font-medium">
                Actieve Locaties
              </p>
              <p className="text-2xl font-bold text-gray-800">
                {activeSitesPercentage !== null
                  ? `${activeSitesPercentage}%`
                  : '...'}
              </p>
            </div>
          </div>
        </Container>

        <Container height="fit">
          <div className="flex items-center gap-4">
            <div className="p-3 bg-orange-50 text-orange-600 rounded-xl">
              <IoTimeOutline className="w-6 h-6" />
            </div>
            <div>
              <p className="text-sm text-gray-500 font-medium">
                Afwezige Werknemers
              </p>
              <p className="text-2xl font-bold text-gray-800">
                {absentEmployees !== null ? absentEmployees : '...'}
              </p>
            </div>
          </div>
        </Container>
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
        <div className="lg:col-span-1">
          <Container label={'Snelle Acties'} width={'full'} height={'fit'}>
            <div className="flex flex-col gap-4 w-full mt-4">
              {links.map((link, index) => (
                <div
                  key={index}
                  className="flex flex-col"
                  onClickCapture={(e) => {
                    if (link.isModal) {
                      e.preventDefault();
                      setIsModalOpen(true);
                    }
                  }}
                >
                  <div className={link.isModal ? 'cursor-pointer' : ''}>
                    <Link
                      href={link.href}
                      label={link.label}
                      icon={link.icon}
                    />
                  </div>
                  <span className="text-xs text-gray-500 ml-4 mt-1.5 mb-2">
                    {link.subtext}
                  </span>
                </div>
              ))}
            </div>
          </Container>
        </div>

        <div className="lg:col-span-2">
          <Container label={'Recente Activiteit'} width={'full'} height={'fit'}>
            <div className="mt-2 max-h-96 lg:max-h-[500px] overflow-y-auto pr-1 scroll-hidden">
              {recenteLogs && recenteLogs.length > 0 ? (
                recenteLogs.map((log) => (
                  <div
                    key={log.id}
                    className="flex items-start gap-4 py-3 border-b border-gray-100 last:border-0 hover:bg-slate-50/50 transition-colors px-2 rounded-lg"
                  >
                    <div
                      className={`mt-1.5 w-2.5 h-2.5 rounded-full shrink-0 shadow-sm ${getLogColor(log.type)}`}
                    ></div>

                    <div className="flex flex-col w-full">
                      <div className="flex justify-between items-start">
                        <p className="text-sm text-gray-800 font-medium leading-tight">
                          <span className="inline-block px-1.5 py-0.5 bg-gray-100 text-gray-500 rounded text-[10px] font-bold uppercase tracking-wider mr-2 align-middle">
                            {log.tabel}
                          </span>
                          <span className="align-middle">
                            <span className="font-semibold text-slate-700">
                              {log.werknemer?.voornaam} {log.werknemer?.naam}
                            </span>
                            <span className="text-slate-500">
                              {' '}
                              {getActionText(log.type)}{' '}
                            </span>
                            <span className="text-slate-800">
                              {log.beschrijving}
                            </span>
                          </span>
                        </p>
                      </div>

                      <div className="flex justify-between items-center mt-1">
                        <span className="text-xs text-gray-400 font-medium">
                          Actie:{' '}
                          <span className="text-gray-500">{log.type}</span>
                        </span>
                        <span className="text-[11px] text-blue-600 font-semibold bg-blue-50 px-2 py-0.5 rounded-full">
                          {new Date(log.timestamp).toLocaleTimeString('nl-BE', {
                            hour: '2-digit',
                            minute: '2-digit',
                          })}
                          <span className="text-gray-400 font-normal ml-1">
                            (
                            {new Date(log.timestamp).toLocaleDateString(
                              'nl-BE',
                              { day: '2-digit', month: 'short' },
                            )}
                            )
                          </span>
                        </span>
                      </div>
                    </div>
                  </div>
                ))
              ) : (
                <div className="flex flex-col justify-center items-center h-full text-gray-400 italic">
                  Geen recente activiteit gevonden.
                </div>
              )}
            </div>
          </Container>
        </div>
      </div>

      <CreeerGebruikerModal
        isOpen={isModalOpen}
        onClose={() => setIsModalOpen(false)}
        jwtToken={
          (session as unknown as { accessToken?: string })?.accessToken || ''
        }
        onSuccess={() => {
          setIsModalOpen(false);
          fetchDashboardData();
        }}
      />
    </div>
  );
}
