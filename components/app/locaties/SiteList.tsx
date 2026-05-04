import { Input } from '@/components/design system/Input';
import { Site } from '@/types/types';
import { StatusBadge } from '@/components/app/locaties/StatusBadge';

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
              className="p-3 border border-gray-200 rounded-xl cursor-pointer hover:bg-gray-50 hover:border-zinc-300 transition-all bg-white group"
            >
              <div className="flex justify-between items-center">
                <h3 className="font-bold text-gray-800 text-base group-hover:text-zinc-600 transition-colors">
                  {site.naam}
                </h3>
              </div>
              <StatusBadge status={site.status} size="xs" />
            </div>
          ))
        )}
      </div>
    </>
  );
}
