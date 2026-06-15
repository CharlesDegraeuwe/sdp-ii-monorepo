import { Site } from '@/types/types';
import { StatusBadge } from './StatusBadge';
import { Button } from '@/components/design-system/Button';
import {
  IoArrowBack,
  IoChevronDown,
  IoChevronUp,
  IoConstructOutline,
  IoPeopleOutline,
  IoTimeOutline,
} from 'react-icons/io5';
import { useState } from 'react';

interface SiteDetailProps {
  site: Site;
  sites: Site[];
  setSelectedSite: (selectedSite: Site) => void;
  isLoading: boolean;
  onBack: () => void;
}

export function SiteDetail(props: SiteDetailProps) {
  const { site, sites, setSelectedSite, isLoading, onBack } = props;
  const [currentIndex, setCurrentIndex] = useState(sites.indexOf(site));

  const toggleDown = () => {
    if (currentIndex < sites.length - 1) {
      setSelectedSite(sites[currentIndex + 1]);
      setCurrentIndex((prev) => prev + 1);
    }
  };

  const toggleUp = () => {
    if (currentIndex > 0) {
      setSelectedSite(sites[currentIndex - 1]);
      setCurrentIndex((prev) => prev - 1);
    }
  };

  // Helper om te berekenen hoeveel machines kapot of in onderhoud zijn
  const activeMachines =
    site.machines?.filter((m) => m.status === 'Actief').length || 0;
  const totalMachines = site.machines?.length || 0;

  return (
    <div className="flex flex-col h-full bg-white rounded-2xl overflow-hidden shadow-xl ring-1 ring-black/5">
      {/* HEADER */}
      <div className="p-4 border-b border-gray-100 gap-3 bg-gray-50/80 shrink-0 flex flex-row justify-between items-center backdrop-blur-sm sticky top-0 z-10">
        <div className={'flex items-center gap-3'}>
          <Button
            variant={'outline'}
            icon={<IoArrowBack className="w-5 h-5 text-gray-600" />}
            onClick={onBack}
            className="p-2 hover:bg-gray-200 border-gray-200 rounded-full transition-colors flex items-center justify-center shadow-sm"
          />
          <h2 className="font-bold text-gray-800 text-lg tracking-tight">
            Locatie Details
          </h2>
        </div>
        <div className={'flex flex-row items-center gap-2'}>
          <Button
            variant={'outline'}
            disabled={currentIndex === 0}
            px={'px-3'}
            icon={<IoChevronUp className="w-5 h-5 text-gray-600" />}
            onClick={toggleUp}
            className="bg-white border-gray-200 hover:bg-gray-100 shadow-sm"
          />
          <Button
            variant={'outline'}
            disabled={currentIndex === sites.length - 1}
            px={'px-3'}
            icon={<IoChevronDown className="w-5 h-5 text-gray-600" />}
            onClick={toggleDown}
            className="bg-white border-gray-200 hover:bg-gray-100 shadow-sm"
          />
        </div>
      </div>

      {/* CONTENT */}
      <div className="p-6 flex-1 overflow-y-auto flex flex-col gap-8 custom-scrollbar">
        {/* Titel & Status */}
        <div className="flex flex-col gap-2">
          <h3 className="text-3xl font-extrabold text-gray-900 leading-tight tracking-tight">
            {site.naam}
          </h3>
          <div className="inline-flex">
            <StatusBadge status={site.status} />
          </div>
        </div>

        {/* MICRO KPI BORDJE (Nieuw!) */}
        <div className="grid grid-cols-2 gap-4">
          <div className="bg-blue-50/50 border border-blue-100 rounded-2xl p-4 flex flex-col justify-between">
            <div className="flex justify-between items-start mb-2">
              <div className="p-2 bg-blue-100 text-blue-600 rounded-lg">
                <IoConstructOutline className="w-5 h-5" />
              </div>
            </div>
            <div>
              <p className="text-xs text-blue-600/80 uppercase tracking-wider font-bold mb-1">
                Machines
              </p>
              {isLoading ? (
                <div className="h-7 w-12 bg-blue-200/50 rounded animate-pulse"></div>
              ) : (
                <div className="flex items-baseline gap-1.5">
                  <span className="text-2xl font-black text-blue-900 leading-none">
                    {totalMachines}
                  </span>
                  {totalMachines > 0 && (
                    <span className="text-xs font-medium text-blue-700 bg-blue-200/50 px-1.5 py-0.5 rounded-md">
                      {activeMachines} actief
                    </span>
                  )}
                </div>
              )}
            </div>
          </div>

          <div className="bg-purple-50/50 border border-purple-100 rounded-2xl p-4 flex flex-col justify-between">
            <div className="flex justify-between items-start mb-2">
              <div className="p-2 bg-purple-100 text-purple-600 rounded-lg">
                <IoPeopleOutline className="w-5 h-5" />
              </div>
            </div>
            <div>
              <p className="text-xs text-purple-600/80 uppercase tracking-wider font-bold mb-1">
                Teams
              </p>
              {isLoading ? (
                <div className="h-7 w-12 bg-purple-200/50 rounded animate-pulse"></div>
              ) : (
                <div className="flex items-baseline gap-1.5">
                  <span className="text-2xl font-black text-purple-900 leading-none">
                    {site.teams?.length || 0}
                  </span>
                </div>
              )}
            </div>
          </div>
        </div>

        <div className="w-full h-px bg-gray-100" />

        {/* DETAILS LIJST */}
        <div className="flex flex-col gap-6">
          <InfoRow
            icon="M17.657 16.657L13.414 20.9a1.998 1.998 0 01-2.827 0l-4.243-4.243a8 8 0 1111.314 0z"
            icon2="M15 11a3 3 0 11-6 0 3 3 0 016 0z"
            label="Adres / Locatie"
            value={site.locatie}
          />

          <InfoRow
            icon="M12 4.354a4 4 0 110 5.292M15 21H3v-1a6 6 0 0112 0v1zm0 0h6v-1a6 6 0 00-9-5.197M13 7a4 4 0 11-8 0 4 4 0 018 0z"
            label="Max Capaciteit"
            value={
              site.capaciteit
                ? `${site.capaciteit} personen`
                : 'Geen data beschikbaar'
            }
          />

          {/* MACHINES LIJST */}
          <div className="flex gap-3 items-start">
            <SectionIcon
              d="M10.325 4.317c.426-1.756 2.924-1.756 3.35 0a1.724 1.724 0 002.573 1.066c1.543-.94 3.31.826 2.37 2.37a1.724 1.724 0 001.065 2.572c1.756.426 1.756 2.924 0 3.35a1.724 1.724 0 00-1.066 2.573c.94 1.543-.826 3.31-2.37 2.37a1.724 1.724 0 00-2.572 1.065c-.426 1.756-2.924 1.756-3.35 0a1.724 1.724 0 00-2.573-1.066c-1.543.94-3.31-.826-2.37-2.37a1.724 1.724 0 00-1.065-2.572c-1.756-.426-1.756-2.924 0-3.35a1.724 1.724 0 001.066-2.573c-.94-1.543.826-3.31 2.37-2.37.996.608 2.296.07 2.572-1.065z"
              d2="M15 12a3 3 0 11-6 0 3 3 0 016 0z"
            />
            <div className="w-full">
              <p className="text-xs text-gray-400 uppercase tracking-wider font-bold mb-3">
                Inventaris Machines
              </p>
              {isLoading ? (
                <div className="flex items-center gap-2 text-sm text-gray-400 italic bg-gray-50 p-3 rounded-xl border border-gray-100 animate-pulse">
                  <IoTimeOutline className="animate-spin w-4 h-4" /> Machines
                  ophalen...
                </div>
              ) : site.machines && site.machines.length > 0 ? (
                <div className="flex flex-col gap-2">
                  {site.machines.map((machine, idx) => (
                    <div
                      key={machine.id || `machine-${idx}`}
                      className="flex justify-between items-center bg-white hover:bg-gray-50 transition-colors border border-gray-200 p-3 rounded-xl shadow-sm"
                    >
                      <span className="text-sm font-semibold text-gray-800">
                        {machine.naam}
                      </span>
                      <StatusBadge status={machine.status} size="xs" />
                    </div>
                  ))}
                </div>
              ) : (
                <p className="text-sm text-gray-500 italic bg-gray-50 p-3 rounded-xl border border-gray-100">
                  Geen machines geregistreerd.
                </p>
              )}
            </div>
          </div>

          {/* TEAMS LIJST */}
          <div className="flex gap-3 items-start pb-4">
            <SectionIcon d="M17 20h5v-2a3 3 0 00-5.356-1.857M17 20H7m10 0v-2c0-.656-.126-1.283-.356-1.857M7 20H2v-2a3 3 0 015.356-1.857M7 20v-2c0-.656.126-1.283.356-1.857m0 0a5.002 5.002 0 019.288 0M15 7a3 3 0 11-6 0 3 3 0 016 0zm6 3a2 2 0 11-4 0 2 2 0 014 0zM7 10a2 2 0 11-4 0 2 2 0 014 0z" />
            <div className="w-full">
              <p className="text-xs text-gray-400 uppercase tracking-wider font-bold mb-3">
                Gekoppelde Teams
              </p>
              {isLoading ? (
                <div className="flex items-center gap-2 text-sm text-gray-400 italic bg-gray-50 p-3 rounded-xl border border-gray-100 animate-pulse">
                  <IoTimeOutline className="animate-spin w-4 h-4" /> Teams
                  ophalen...
                </div>
              ) : site.teams && site.teams.length > 0 ? (
                <div className="flex flex-wrap gap-2">
                  {site.teams.map((team, idx) => (
                    <span
                      key={team.id || idx}
                      className="px-3 py-1.5 bg-white hover:bg-purple-50 hover:border-purple-200 transition-colors text-gray-700 border border-gray-200 rounded-lg text-sm font-semibold shadow-sm flex items-center gap-2 cursor-default"
                    >
                      <div className="w-2 h-2 rounded-full bg-purple-500" />
                      {team.naam || `Team #${team.id}`}
                    </span>
                  ))}
                </div>
              ) : (
                <p className="text-sm text-gray-500 italic bg-gray-50 p-3 rounded-xl border border-gray-100">
                  Geen teams aan deze site gekoppeld.
                </p>
              )}
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}

function SectionIcon({ d, d2 }: { d: string; d2?: string }) {
  return (
    <svg
      className="w-5 h-5 text-gray-400 mt-0.5 shrink-0"
      fill="none"
      stroke="currentColor"
      viewBox="0 0 24 24"
    >
      <path
        strokeLinecap="round"
        strokeLinejoin="round"
        strokeWidth={2.5}
        d={d}
      />
      {d2 && (
        <path
          strokeLinecap="round"
          strokeLinejoin="round"
          strokeWidth={2.5}
          d={d2}
        />
      )}
    </svg>
  );
}

function InfoRow({
  icon,
  icon2,
  label,
  value,
}: {
  icon: string;
  icon2?: string;
  label: string;
  value: string;
}) {
  return (
    <div className="flex gap-3 items-start">
      <SectionIcon d={icon} d2={icon2} />
      <div>
        <p className="text-xs text-gray-400 uppercase tracking-wider font-bold">
          {label}
        </p>
        <p className="text-gray-800 font-semibold mt-1 leading-relaxed">
          {value}
        </p>
      </div>
    </div>
  );
}
