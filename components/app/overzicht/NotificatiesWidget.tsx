'use client';

import { useState } from 'react';
import { useSession } from 'next-auth/react';
import NextLink from 'next/link';
import type { Notificatie } from './types';
import { Card, SectionTitle } from './Card';

interface NotificatiesWidgetProps {
  notificaties: Notificatie[];
  onRefresh?: () => void;
}

const BASE = process.env.NEXT_PUBLIC_API_URL ?? 'http://localhost:8080/api';

type Actie = 'goedgekeurd' | 'afgewezen';
type BadgeVariant = 'verlof' | 'afwezig' | 'taak' | 'default';

function formatDatum(d: string) {
  const datum = new Date(d);
  const nu = new Date();
  const diffMin = Math.floor((nu.getTime() - datum.getTime()) / 60000);
  const diffUur = Math.floor(diffMin / 60);
  const diffDag = Math.floor(diffUur / 24);
  if (diffMin < 60) return `${diffMin}m geleden`;
  if (diffUur < 24) return `${diffUur}u geleden`;
  if (diffDag === 1) return 'gisteren';
  return datum.toLocaleDateString('nl-BE', { day: 'numeric', month: 'short' });
}

function getBadge(titel: string): BadgeVariant {
  const t = titel.toLowerCase();
  if (t.includes('verlof')) return 'verlof';
  if (t.includes('afwezig') || t.includes('teamlid')) return 'afwezig';
  if (t.includes('taak')) return 'taak';
  return 'default';
}

const badgeStyles: Record<
  BadgeVariant,
  { dot: string; bg: string; label: string }
> = {
  verlof: {
    dot: 'bg-violet-400',
    bg: 'bg-violet-100/60 text-violet-700',
    label: 'Verlof',
  },
  afwezig: {
    dot: 'bg-orange-400',
    bg: 'bg-orange-100/60 text-orange-700',
    label: 'Afwezig',
  },
  taak: { dot: 'bg-sky-400', bg: 'bg-sky-100/60 text-sky-700', label: 'Taak' },
  default: {
    dot: 'bg-zinc-400',
    bg: 'bg-zinc-100/60 text-zinc-600',
    label: '',
  },
};

function heeftActie(titel: string): boolean {
  return titel === 'Nieuwe verlofaanvraag' || titel === 'Teamlid afwezig';
}

function buildEndpoints(titel: string, referentieId: number) {
  const base =
    titel === 'Nieuwe verlofaanvraag'
      ? `${BASE}/verlof/${referentieId}`
      : `${BASE}/afwezigheid/${referentieId}`;
  return { goed: `${base}/goedkeuren`, af: `${base}/afwijzen` };
}

export function NotificatiesWidget({
  notificaties,
  onRefresh,
}: NotificatiesWidgetProps) {
  const { data: session } = useSession();
  const token = session?.accessToken;

  const [bezigIds, setBezigIds] = useState<number[]>([]);
  const [acties, setActies] = useState<Record<number, Actie>>({});

  const ongelezen = notificaties.filter((n) => n.gelezen === 'Nee');
  const aantalOngelezen = ongelezen.length;

  async function voerActieUit(n: Notificatie, type: 'goed' | 'af') {
    if (!n.referentieId) return;
    setBezigIds((prev) => [...prev, n.id]);
    const { goed, af } = buildEndpoints(n.titel, n.referentieId);
    try {
      await fetch(type === 'goed' ? goed : af, {
        method: 'PUT',
        headers: { Authorization: `Bearer ${token}` },
      });
      await fetch(`${BASE}/notificaties/${n.id}/gelezen`, {
        method: 'PUT',
        headers: { Authorization: `Bearer ${token}` },
      });
      onRefresh?.();
    } catch {
      // Backend niet bereikbaar: optimistische UI-update
    } finally {
      setActies((prev) => ({
        ...prev,
        [n.id]: type === 'goed' ? 'goedgekeurd' : 'afgewezen',
      }));
      setBezigIds((prev) => prev.filter((id) => id !== n.id));
    }
  }

  return (
    <div className="flex flex-col gap-1">
      <div className="flex items-center justify-between px-1">
        <SectionTitle>Notificaties</SectionTitle>
        {aantalOngelezen > 0 && (
          <span className="text-[10px] font-bold bg-[var(--color-delaware_red)] text-white rounded-full px-2 py-0.5 leading-none">
            {aantalOngelezen}
          </span>
        )}
      </div>

      <Card className="h-[230px] overflow-hidden">
        <div className="flex flex-col h-full">
          {/* Scrollbare lijst */}
          <div className="flex-1 min-h-0 overflow-y-auto scroll-hidden flex flex-col gap-1 p-2.5">
            {ongelezen.length === 0 && (
              <p className="text-xs text-zinc-400 px-1 py-1">
                Geen ongelezen notificaties.
              </p>
            )}

            {ongelezen.map((n) => {
              const variant = getBadge(n.titel);
              const badge = badgeStyles[variant];
              const bezig = bezigIds.includes(n.id);
              const actie = acties[n.id];
              const toonActie = heeftActie(n.titel) && !!n.referentieId;

              return (
                <div
                  key={n.id}
                  className="flex items-start gap-2 px-2.5 py-2 rounded-xl border border-gray-300/50 bg-white/50 transition-all duration-300"
                >
                  <div
                    className={`mt-1.5 w-1.5 h-1.5 rounded-full flex-shrink-0 ${badge.dot}`}
                  />

                  <div className="flex-1 min-w-0">
                    <div className="flex items-center gap-1.5 mb-0.5">
                      <span className="text-[11px] font-bold text-zinc-800 truncate">
                        {n.titel}
                      </span>
                      {badge.label && (
                        <span
                          className={`text-[9px] font-bold px-1.5 py-0.5 rounded-full flex-shrink-0 ${badge.bg}`}
                        >
                          {badge.label}
                        </span>
                      )}
                      <span className="text-[9px] text-zinc-400 ml-auto flex-shrink-0 whitespace-nowrap">
                        {formatDatum(n.datum)}
                      </span>
                    </div>

                    <p className="text-[10px] text-zinc-500 line-clamp-1 mb-1.5">
                      {n.bericht}
                    </p>

                    {toonActie &&
                      (actie ? (
                        <span
                          className={`inline-flex text-[9px] font-bold px-2 py-0.5 rounded-full ${
                            actie === 'goedgekeurd'
                              ? 'bg-emerald-100/80 text-emerald-700'
                              : 'bg-red-100/80 text-red-600'
                          }`}
                        >
                          {actie === 'goedgekeurd'
                            ? '✓ Goedgekeurd'
                            : '✗ Afgewezen'}
                        </span>
                      ) : (
                        <div className="flex gap-1.5">
                          <button
                            onClick={() => voerActieUit(n, 'goed')}
                            disabled={bezig}
                            className="bg-emerald-500 text-white py-1 px-3 rounded-xl max-w-fit text-[9px] font-bold active:scale-95 transition-all duration-300 disabled:opacity-20 cursor-pointer"
                          >
                            {bezig ? 'laden...' : 'Goedkeuren'}
                          </button>
                          <button
                            onClick={() => voerActieUit(n, 'af')}
                            disabled={bezig}
                            className="bg-rose-500 text-white py-1 px-3 rounded-xl max-w-fit text-[9px] font-bold active:scale-95 transition-all duration-300 disabled:opacity-20 cursor-pointer"
                          >
                            {bezig ? 'laden...' : 'Afwijzen'}
                          </button>
                        </div>
                      ))}
                  </div>
                </div>
              );
            })}
          </div>

          {/* Vaste footer */}
          <NextLink
            href="/notificaties"
            className="flex items-center justify-center gap-1 border-t border-gray-300/30 py-2 text-[10px] font-bold text-zinc-400 hover:text-zinc-700 hover:bg-white/20 transition-all duration-200 flex-shrink-0"
          >
            Bekijk alle notificaties
            {aantalOngelezen > 0 && (
              <span className="bg-zinc-200 text-zinc-500 rounded-full px-1.5 py-0.5">
                {aantalOngelezen}
              </span>
            )}
          </NextLink>
        </div>
      </Card>
    </div>
  );
}
