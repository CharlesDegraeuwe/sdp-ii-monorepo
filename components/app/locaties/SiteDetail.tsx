import { Site } from '@/types/types';
import { StatusBadge } from './StatusBadge';
import BreadcrumbInit from '@/components/app/structuur/breadcrumb/BreadCrumbInit';
import { Button } from '@/components/design system/Button';
import { IoArrowBack, IoChevronDown, IoChevronUp } from 'react-icons/io5';
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
  return (
    <div className="flex flex-col h-full">
      <div className="p-4 border-b border-gray-100 gap-3 bg-gray-50/50 shrink-0 flex flex-row justify-between items-center">
        <div className={'flex items-center gap-3'}>
          <Button
            variant={'outline'}
            icon={<IoArrowBack />}
            onClick={onBack}
            className="p-2 hover:bg-gray-200 rounded-full transition-colors flex items-center justify-center"
          />
          <h2 className="font-bold text-gray-800 text-lg">Locatie Details</h2>
        </div>
        <div className={'flex flex-row items-center gap-3'}>
          <Button
            variant={'outline'}
            disabled={currentIndex === 0}
            px={'px-3'}
            icon={<IoChevronUp />}
            onClick={toggleUp}
          />
          <Button
            variant={'outline'}
            disabled={currentIndex === sites.length - 1}
            px={'px-3'}
            icon={<IoChevronDown />}
            onClick={toggleDown}
          />
        </div>
      </div>

      <div className="p-6 flex-1 overflow-y-auto flex flex-col gap-6">
        <div>
          <h3 className="text-2xl font-extrabold text-gray-900 leading-tight">
            {site.naam}
          </h3>
          <div className="mt-3 inline-block">
            <StatusBadge status={site.status} />
          </div>
        </div>

        <div className="w-full h-px bg-gray-100" />

        <div className="flex flex-col gap-6">
          <InfoRow
            icon="M17.657 16.657L13.414 20.9a1.998 1.998 0 01-2.827 0l-4.243-4.243a8 8 0 1111.314 0z"
            icon2="M15 11a3 3 0 11-6 0 3 3 0 016 0z"
            label="Adres / Locatie"
            value={site.locatie}
          />

          <InfoRow
            icon="M12 4.354a4 4 0 110 5.292M15 21H3v-1a6 6 0 0112 0v1zm0 0h6v-1a6 6 0 00-9-5.197M13 7a4 4 0 11-8 0 4 4 0 018 0z"
            label="Capaciteit"
            value={
              site.capaciteit
                ? `${site.capaciteit} personen`
                : 'Geen data beschikbaar'
            }
          />

          <div className="flex gap-3 items-start">
            <SectionIcon
              d="M10.325 4.317c.426-1.756 2.924-1.756 3.35 0a1.724 1.724 0 002.573 1.066c1.543-.94 3.31.826 2.37 2.37a1.724 1.724 0 001.065 2.572c1.756.426 1.756 2.924 0 3.35a1.724 1.724 0 00-1.066 2.573c.94 1.543-.826 3.31-2.37 2.37a1.724 1.724 0 00-2.572 1.065c-.426 1.756-2.924 1.756-3.35 0a1.724 1.724 0 00-2.573-1.066c-1.543.94-3.31-.826-2.37-2.37a1.724 1.724 0 00-1.065-2.572c-1.756-.426-1.756-2.924 0-3.35a1.724 1.724 0 001.066-2.573c-.94-1.543.826-3.31 2.37-2.37.996.608 2.296.07 2.572-1.065z"
              d2="M15 12a3 3 0 11-6 0 3 3 0 016 0z"
            />
            <div className="w-full">
              <p className="text-xs text-gray-400 uppercase tracking-wider font-semibold mb-2">
                Machines
              </p>
              {isLoading ? (
                <p className="text-sm text-gray-400 italic bg-gray-50 p-2.5 rounded-lg border border-gray-100 animate-pulse">
                  Machines ophalen...
                </p>
              ) : site.machines && site.machines.length > 0 ? (
                <div className="flex flex-col gap-2">
                  {site.machines.map((machine) => (
                    <div
                      key={machine.id}
                      className="flex justify-between items-center bg-gray-50 border border-gray-100 p-2.5 rounded-lg shadow-sm"
                    >
                      <span className="text-sm font-semibold text-gray-700">
                        {machine.naam}
                      </span>
                      <StatusBadge status={machine.status} size="xs" />
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
            <SectionIcon d="M17 20h5v-2a3 3 0 00-5.356-1.857M17 20H7m10 0v-2c0-.656-.126-1.283-.356-1.857M7 20H2v-2a3 3 0 015.356-1.857M7 20v-2c0-.656.126-1.283.356-1.857m0 0a5.002 5.002 0 019.288 0M15 7a3 3 0 11-6 0 3 3 0 016 0zm6 3a2 2 0 11-4 0 2 2 0 014 0zM7 10a2 2 0 11-4 0 2 2 0 014 0z" />
            <div className="w-full">
              <p className="text-xs text-gray-400 uppercase tracking-wider font-semibold mb-2">
                Gekoppelde Teams
              </p>
              {isLoading ? (
                <p className="text-sm text-gray-400 italic bg-gray-50 p-2.5 rounded-lg border border-gray-100 animate-pulse">
                  Teams ophalen...
                </p>
              ) : site.teams && site.teams.length > 0 ? (
                <div className="flex flex-wrap gap-2">
                  {site.teams.map((team, idx) => (
                    <span
                      key={team.id || idx}
                      className="px-3 py-1.5 bg-blue-50 text-blue-700 border border-blue-100 rounded-lg text-sm font-semibold shadow-sm flex items-center gap-1.5"
                    >
                      <div className="w-1.5 h-1.5 rounded-full bg-blue-500" />
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
        strokeWidth={2}
        d={d}
      />
      {d2 && (
        <path
          strokeLinecap="round"
          strokeLinejoin="round"
          strokeWidth={2}
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
        <p className="text-xs text-gray-400 uppercase tracking-wider font-semibold">
          {label}
        </p>
        <p className="text-gray-800 font-medium mt-1 leading-relaxed">
          {value}
        </p>
      </div>
    </div>
  );
}
