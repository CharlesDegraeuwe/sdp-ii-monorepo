import { Input } from '@/components/design system/Input';
import { Site } from '@/types/types';

export function SiteList({
  sites,
  isLoading,
  onSiteClick,
}: {
  sites: Site[];
  isLoading: boolean;
  onSiteClick: (site: Site) => void;
}) {
  return (
    <>
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
              onClick={() => onSiteClick(site)}
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
    </>
  );
}
