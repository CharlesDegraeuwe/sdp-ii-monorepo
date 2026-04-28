import Link from 'next/link';
import type { Notificatie } from './types';
import { Card, SectionTitle } from './Card';

interface NotificatiesWidgetProps {
  notificaties: Notificatie[];
}

function formatDatum(d: string) {
  return new Date(d).toLocaleDateString('nl-BE', {
    day: 'numeric',
    month: 'short',
  });
}

export function NotificatiesWidget({ notificaties }: NotificatiesWidgetProps) {
  return (
    <div className="flex flex-col gap-2">
      <SectionTitle>Notificaties</SectionTitle>
      <Card className="h-[230px] overflow-hidden">
        <div className="flex flex-col gap-1.5 p-3 h-full overflow-y-auto">
          {notificaties.length === 0 && (
            <p className="text-xs text-zinc-400 px-1 pt-1">
              Geen notificaties.
            </p>
          )}
          {notificaties.slice(0, 7).map((n) => {
            const isOngelezen = n.gelezen === 'Nee';
            return (
              <div
                key={n.id}
                className={`flex items-start gap-2 px-2.5 py-2 rounded-xl border transition-all duration-200
                  ${isOngelezen ? 'border-gray-300/40 bg-white/40' : 'border-gray-200/20 bg-white/10'}`}
              >
                <div
                  className={`mt-1.5 w-1.5 h-1.5 rounded-full flex-shrink-0 ${isOngelezen ? 'bg-red-400' : 'bg-emerald-400'}`}
                />
                <div className="flex-1 min-w-0">
                  <span className="text-[11px] font-bold text-zinc-800 truncate block">
                    {n.titel}
                  </span>
                  <p className="text-[10px] text-zinc-500 line-clamp-1 mt-0.5">
                    {n.bericht}
                  </p>
                </div>
                <span className="text-[9px] text-zinc-400 flex-shrink-0 mt-0.5">
                  {formatDatum(n.datum)}
                </span>
              </div>
            );
          })}
          {notificaties.length > 7 && (
            <Link
              href="/notificaties"
              className="text-[10px] text-zinc-400 font-bold text-center hover:text-zinc-600 transition-colors mt-1"
            >
              + {notificaties.length - 7} meer
            </Link>
          )}
        </div>
      </Card>
    </div>
  );
}
