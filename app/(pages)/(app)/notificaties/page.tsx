'use client';

import { useSession } from 'next-auth/react';
import { useEffect, useMemo, useState, useCallback } from 'react';
import PageHeader from '@/components/design-system/PageHeader/PageHeader';
import { AppContainer } from '@/components/design-system/AppContainer';
import { PageContainer } from '@/components/design-system/PageContainer';
import BreadcrumbInit from '@/components/app/structuur/breadcrumb/BreadCrumbInit';

type Filter = 'Alles' | 'Werk' | 'Afwezigheid' | 'Verlof';

interface Notificatie {
  id: number;
  werknemerId: number;
  titel: string;
  bericht: string;
  gelezen: string;
  datum: string;
  referentieId: number | null;
}

interface VerlofStatus {
  [verlofId: number]: string;
}

const BASE = process.env.NEXT_PUBLIC_API_URL ?? 'http://localhost:8080/api';

export default function NotificatiesPage() {
  const { data: session } = useSession();
  const token = session?.accessToken;
  const user = session?.user;

  const [notificaties, setNotificaties] = useState<Notificatie[]>([]);
  const [filter, setFilter] = useState<Filter>('Alles');
  const [verlofStatussen, setVerlofStatussen] = useState<VerlofStatus>({});
  const [bezig, setBezig] = useState<number[]>([]);

  const authHeader = useMemo(
    () => ({ Authorization: `Bearer ${token}` }),
    [token],
  );

  const laadNotificaties = useCallback(async () => {
    if (!user?.id) return;
    try {
      const res = await fetch(`${BASE}/notificaties/${user.id}`, {
        headers: authHeader,
      });
      const data: Notificatie[] = await res.json();
      setNotificaties(data);

      const verlofIds = data
        .filter(
          (n) =>
            (n.titel === 'Nieuwe verlofaanvraag' ||
              n.titel === 'Verlof goedgekeurd') &&
            n.referentieId,
        )
        .map((n) => n.referentieId!);

      const unieke = [...new Set(verlofIds)];
      const statussen: VerlofStatus = {};
      await Promise.all(
        unieke.map(async (id) => {
          try {
            const r = await fetch(`${BASE}/verlof/${id}/status`, {
              headers: authHeader,
            });
            statussen[id] = await r.text();
          } catch {
            statussen[id] = '';
          }
        }),
      );
      setVerlofStatussen(statussen);
    } catch (e) {
      console.error(e);
    }
  }, [user, authHeader]);

  useEffect(() => {
    laadNotificaties();
  }, [laadNotificaties]);

  async function markeerGelezen(id: number) {
    await fetch(`${BASE}/notificaties/${id}/gelezen`, {
      method: 'PUT',
      headers: authHeader,
    });
    laadNotificaties();
  }

  async function verwijder(id: number) {
    await fetch(`${BASE}/notificaties/${id}`, {
      method: 'DELETE',
      headers: authHeader,
    });
    laadNotificaties();
  }

  async function keurGoed(n: Notificatie) {
    if (!n.referentieId) return;
    setBezig((b) => [...b, n.id]);
    try {
      await fetch(`${BASE}/verlof/${n.referentieId}/goedkeuren`, {
        method: 'PUT',
        headers: authHeader,
      });
      await fetch(`${BASE}/notificaties/${n.id}/gelezen`, {
        method: 'PUT',
        headers: authHeader,
      });
      laadNotificaties();
    } finally {
      setBezig((b) => b.filter((x) => x !== n.id));
    }
  }

  async function wijsAf(n: Notificatie) {
    if (!n.referentieId) return;
    setBezig((b) => [...b, n.id]);
    try {
      await fetch(`${BASE}/verlof/${n.referentieId}/afwijzen`, {
        method: 'PUT',
        headers: authHeader,
      });
      await fetch(`${BASE}/notificaties/${n.id}/gelezen`, {
        method: 'PUT',
        headers: authHeader,
      });
      laadNotificaties();
    } finally {
      setBezig((b) => b.filter((x) => x !== n.id));
    }
  }

  async function annuleer(n: Notificatie) {
    if (!n.referentieId) return;
    setBezig((b) => [...b, n.id]);
    try {
      await fetch(`${BASE}/verlof/${n.referentieId}/annuleren`, {
        method: 'PUT',
        headers: authHeader,
      });
      laadNotificaties();
    } finally {
      setBezig((b) => b.filter((x) => x !== n.id));
    }
  }

  function gefilterd() {
    switch (filter) {
      case 'Werk':
        return notificaties.filter(
          (n) =>
            !n.titel.toLowerCase().includes('verlof') &&
            !n.titel.toLowerCase().includes('afwezig'),
        );
      case 'Afwezigheid':
        return notificaties.filter(
          (n) =>
            n.titel.toLowerCase().includes('afwezig') ||
            n.titel.toLowerCase().includes('teamlid'),
        );
      case 'Verlof':
        return notificaties.filter((n) =>
          n.titel.toLowerCase().includes('verlof'),
        );
      default:
        return notificaties;
    }
  }

  function formatDatum(d: string) {
    return new Date(d).toLocaleDateString('nl-BE', {
      day: 'numeric',
      month: 'short',
      year: 'numeric',
    });
  }

  const alle = gefilterd();
  const ongelezen = alle.filter((n) => n.gelezen === 'Nee');
  const filters: Filter[] = ['Alles', 'Werk', 'Afwezigheid', 'Verlof'];

  return (
    <PageContainer className="h-full">
      <AppContainer>
        <div className="w-full max-w-3xl mx-auto flex flex-col gap-6">
          <BreadcrumbInit pages={['notificaties']} />
          <div className="flex justify-center">
            <div className="flex gap-1 bg-gray-300/30 border border-gray-300/30 rounded-full p-1 shadow-sm">
              {filters.map((f) => (
                <button
                  key={f}
                  onClick={() => setFilter(f)}
                  className={`px-4 py-2 rounded-full text-sm font-bold transition-all duration-300 ${filter === f ? 'bg-zinc-900 text-white shadow' : 'text-zinc-500 hover:text-zinc-800 hover:bg-zinc-200/50'}`}
                >
                  {f}
                </button>
              ))}
            </div>
          </div>

          {/* Ongelezen */}
          {ongelezen.length > 0 && (
            <div className="flex flex-col gap-3">
              <span className="text-xs font-bold text-zinc-400 uppercase tracking-wide">
                Ongelezen ({ongelezen.length})
              </span>
              {ongelezen.map((n) => (
                <NotificatieRij
                  key={n.id}
                  n={n}
                  verlofStatus={
                    n.referentieId ? verlofStatussen[n.referentieId] : undefined
                  }
                  bezig={bezig.includes(n.id)}
                  onGelezen={() => markeerGelezen(n.id)}
                  onVerwijder={() => verwijder(n.id)}
                  onGoedkeuren={() => keurGoed(n)}
                  onAfwijzen={() => wijsAf(n)}
                  onAnnuleer={() => annuleer(n)}
                  formatDatum={formatDatum}
                />
              ))}
            </div>
          )}

          {/* Alle */}
          <div className="flex flex-col gap-3">
            <span className="text-xs font-bold text-zinc-400 uppercase tracking-wide">
              Alle notificaties
            </span>
            {alle.length === 0 && (
              <p className="text-sm text-zinc-400">Geen notificaties.</p>
            )}
            {alle.map((n) => (
              <NotificatieRij
                key={n.id}
                n={n}
                verlofStatus={
                  n.referentieId ? verlofStatussen[n.referentieId] : undefined
                }
                bezig={bezig.includes(n.id)}
                onGelezen={() => markeerGelezen(n.id)}
                onVerwijder={() => verwijder(n.id)}
                onGoedkeuren={() => keurGoed(n)}
                onAfwijzen={() => wijsAf(n)}
                onAnnuleer={() => annuleer(n)}
                formatDatum={formatDatum}
              />
            ))}
          </div>
        </div>
      </AppContainer>
    </PageContainer>
  );
}

interface RijProps {
  n: Notificatie;
  verlofStatus: string | undefined;
  bezig: boolean;
  onGelezen: () => void;
  onVerwijder: () => void;
  onGoedkeuren: () => void;
  onAfwijzen: () => void;
  onAnnuleer: () => void;
  formatDatum: (d: string) => string;
}

function NotificatieRij(props: RijProps) {
  const {
    n,
    verlofStatus,
    bezig,
    onGelezen,
    onVerwijder,
    onGoedkeuren,
    onAfwijzen,
    onAnnuleer,
    formatDatum,
  } = props;
  if (!n) return null;
  const isOngelezen = n?.gelezen === 'Nee';

  return (
    <div
      className={`flex items-start gap-4 px-5 py-4 rounded-4xl border transition-all duration-300 bg-gray-300/20 hover:border-gray-400/30 ${isOngelezen ? 'border-gray-300/40 shadow-sm' : 'border-gray-300/20'}`}
    >
      <div
        className={`mt-1.5 w-2 h-2 rounded-full ${isOngelezen ? 'bg-red-400' : 'bg-emerald-400'}`}
      />
      <div className="flex-1 min-w-0">
        <div className="flex items-start justify-between gap-2">
          <span className="text-sm font-bold text-zinc-900">{n.titel}</span>
          <span className="text-xs text-zinc-400 flex-shrink-0">
            {formatDatum(n.datum)}
          </span>
        </div>
        <p className="text-sm text-zinc-500 mt-0.5">{n.bericht}</p>
        <div className="flex items-center gap-2 mt-3 flex-wrap">
          {n.titel === 'Nieuwe verlofaanvraag' &&
            n.referentieId &&
            (verlofStatus === 'In afwachting' ? (
              <>
                <button
                  onClick={onGoedkeuren}
                  disabled={bezig}
                  className="px-3 py-1.5 rounded-full bg-emerald-500 text-white text-xs font-bold hover:bg-emerald-600 active:scale-95 transition-all duration-200 disabled:opacity-50"
                >
                  Goedkeuren
                </button>
                <button
                  onClick={onAfwijzen}
                  disabled={bezig}
                  className="px-3 py-1.5 rounded-full bg-red-500 text-white text-xs font-bold hover:bg-red-600 active:scale-95 transition-all duration-200 disabled:opacity-50"
                >
                  Afwijzen
                </button>
              </>
            ) : verlofStatus ? (
              <span
                className={`text-xs font-bold px-3 py-1 rounded-full ${verlofStatus === 'Goedgekeurd' ? 'bg-emerald-50 text-emerald-600' : verlofStatus === 'Afgewezen' ? 'bg-red-50 text-red-500' : 'bg-zinc-100 text-zinc-500'}`}
              >
                {verlofStatus === 'Goedgekeurd'
                  ? '✓ Goedgekeurd'
                  : verlofStatus === 'Afgewezen'
                    ? '✗ Afgewezen'
                    : verlofStatus}
              </span>
            ) : null)}
          {n.titel === 'Verlof goedgekeurd' &&
            n.referentieId &&
            (verlofStatus === 'Goedgekeurd' ? (
              <button
                onClick={onAnnuleer}
                disabled={bezig}
                className="px-3 py-1.5 rounded-full bg-red-500 text-white text-xs font-bold hover:bg-red-600 active:scale-95 transition-all duration-200 disabled:opacity-50"
              >
                Annuleren
              </button>
            ) : verlofStatus === 'Geannuleerd' ? (
              <span className="text-xs font-bold px-3 py-1 rounded-full bg-zinc-100 text-zinc-500">
                ✗ Geannuleerd
              </span>
            ) : null)}
          {isOngelezen && (
            <button
              onClick={onGelezen}
              className="px-3 py-1.5 rounded-full bg-gray-300/40 text-zinc-600 text-xs font-bold hover:bg-gray-300/60 active:scale-95 transition-all duration-200"
            >
              ✓
            </button>
          )}
          <button
            onClick={onVerwijder}
            className="px-3 py-1.5 rounded-full bg-gray-300/40 text-zinc-600 text-xs font-bold hover:bg-gray-300/60 active:scale-95 transition-all duration-200"
          >
            ✕
          </button>
        </div>
      </div>
    </div>
  );
}
