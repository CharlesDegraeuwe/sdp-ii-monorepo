'use client';

import { useState } from 'react';
import { useSession } from 'next-auth/react';
import NextLink from 'next/link';
import { BellIcon } from '@phosphor-icons/react';
import type { Notificatie } from './types';
import { Container } from '@/components/design-system/Container';
import Button from '@/components/design-system/Button/Button';

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
  if (diffMin < 60) return `${diffMin}m`;
  if (diffUur < 24) return `${diffUur}u`;
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

  const ongelezen = notificaties.filter((n) => n.gelezen === 'Nee').slice(0, 2);

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
      // optimistische UI-update
    } finally {
      setActies((prev) => ({
        ...prev,
        [n.id]: type === 'goed' ? 'goedgekeurd' : 'afgewezen',
      }));
      setBezigIds((prev) => prev.filter((id) => id !== n.id));
    }
  }

  return (
    <Container label="Notificaties">
      <div className="flex flex-col h-full">
        <div className="flex-1 min-h-0 overflow-y-auto scroll-hidden flex flex-col gap-2">
          {ongelezen.length === 0 && (
            <div className="flex-1 flex items-center justify-center py-6">
              <p className="text-xs text-zinc-400">
                Geen ongelezen notificaties
              </p>
            </div>
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
                className="flex items-start gap-3 px-3 py-2.5 rounded-2xl border border-gray-300/30 bg-gray-300/20 transition-all duration-200"
              >
                <div
                  className={`mt-1 w-2 h-2 rounded-full shrink-0 ${badge.dot}`}
                />

                <div className="flex-1 min-w-0 flex flex-col gap-1">
                  <div className="flex items-center gap-2">
                    <span className="text-xs font-semibold text-zinc-800 truncate">
                      {n.titel}
                    </span>
                    {badge.label && (
                      <span
                        className={`text-xs font-semibold px-2 py-0.5 rounded-full shrink-0 ${badge.bg}`}
                      >
                        {badge.label}
                      </span>
                    )}
                    <span className="text-xs text-zinc-400 ml-auto shrink-0">
                      {formatDatum(n.datum)}
                    </span>
                  </div>

                  <p className="text-xs text-zinc-500 line-clamp-1">
                    {n.bericht}
                  </p>

                  {toonActie &&
                    (actie ? (
                      <span
                        className={`inline-flex text-xs font-semibold px-2 py-0.5 rounded-full w-fit ${
                          actie === 'goedgekeurd'
                            ? 'bg-emerald-100/80 text-emerald-700'
                            : 'bg-red-100/80 text-rose-700'
                        }`}
                      >
                        {actie === 'goedgekeurd'
                          ? '✓ Goedgekeurd'
                          : '✗ Afgewezen'}
                      </span>
                    ) : (
                      <div className="flex gap-1.5 mt-0.5">
                        <Button
                          label={bezig ? 'laden...' : 'Goedkeuren'}
                          onClick={() => voerActieUit(n, 'goed')}
                          disabled={bezig}
                          variant="approve"
                          size="xs"
                        />
                        <Button
                          label={bezig ? 'laden...' : 'Afwijzen'}
                          onClick={() => voerActieUit(n, 'af')}
                          disabled={bezig}
                          size="xs"
                        />
                      </div>
                    ))}
                </div>
              </div>
            );
          })}
        </div>

        <NextLink
          href="/notificaties"
          className="shrink-0 flex items-center justify-center gap-1.5 border-t border-gray-300/30 pt-3 mt-3 text-xs font-semibold text-zinc-400 hover:text-zinc-700 transition-colors duration-200"
        >
          <BellIcon size={13} />
          Bekijk alle notificaties
        </NextLink>
      </div>
    </Container>
  );
}
