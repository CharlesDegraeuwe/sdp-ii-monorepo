'use client';

import { useSession } from 'next-auth/react';
import { useEffect, useMemo, useState } from 'react';
import PageHeader from '@/components/design system/PageHeader/PageHeader';
import Container from '@/components/design system/Container/Container';
import Button from '@/components/design system/Button/Button';
import { AppContainer } from '@/components/design system/AppContainer';
import { PageContainer } from '@/components/design system/PageContainer';
import { useBreadCrumbs } from '@/providers/BreadCrumbProvider';
import BreadcrumbInit from '@/components/app/structuur/breadcrumb/BreadCrumbInit';

type Tab = 'verlof' | 'ziekte' | 'geschiedenis';

interface GeschiedenisItem {
  id: number;
  type: string;
  startDatum: string;
  eindDatum: string;
  status: string | null;
  omschrijving: string | null;
}

interface Teamlid {
  id: number;
  naam: string;
  voornaam: string;
  rol: string;
}

const BASE = process.env.NEXT_PUBLIC_API_URL ?? 'http://localhost:8080/api';

export default function AfwezighedenPage() {
  const { data: session } = useSession();
  const token = session?.accessToken;
  const user = session?.user;

  const [tab, setTab] = useState<Tab>('verlof');
  const [feedback, setFeedback] = useState<{
    type: 'ok' | 'fout';
    bericht: string;
  } | null>(null);

  const [verlofStart, setVerlofStart] = useState('');
  const [verlofEind, setVerlofEind] = useState('');
  const [verlofType, setVerlofType] = useState('Jaarlijks verlof');

  const [reden, setReden] = useState('');
  const [ziekteStart, setZiekteStart] = useState('');
  const [ziekteEind, setZiekteEind] = useState('');
  const [certificaat, setCertificaat] = useState<File | null>(null);
  const [isDragging, setIsDragging] = useState(false);

  const [teamleden, setTeamleden] = useState<Teamlid[]>([]);
  const [geselecteerdLid, setGeselecteerdLid] = useState<Teamlid | null>(null);
  const [geschiedenis, setGeschiedenis] = useState<GeschiedenisItem[]>([]);
  const [geschiedenisLaden, setGeschiedenisLaden] = useState(false);

  const isMgr = user?.rol === 'Manager' || user?.rol === 'Supervisor';
  const authHeader = useMemo(
    () => ({ Authorization: `Bearer ${token}` }),
    [token],
  );

  function toonFeedback(type: 'ok' | 'fout', bericht: string) {
    setFeedback({ type, bericht });
    setTimeout(() => setFeedback(null), 4000);
  }

  function formatDatum(d: string) {
    return new Date(d).toLocaleDateString('nl-BE', {
      day: 'numeric',
      month: 'short',
      year: 'numeric',
    });
  }

  async function submitVerlof() {
    if (!verlofStart || !verlofEind)
      return toonFeedback('fout', 'Vul alle datums in.');
    try {
      const res = await fetch(`${BASE}/verlof`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json', ...authHeader },
        body: JSON.stringify({
          werknemerId: Number(user?.id),
          startDatum: verlofStart,
          eindDatum: verlofEind,
          type: verlofType,
        }),
      });
      if (!res.ok) throw new Error();
      toonFeedback('ok', 'Verlofaanvraag ingediend!');
      setVerlofStart('');
      setVerlofEind('');
    } catch {
      toonFeedback('fout', 'Er is iets misgegaan.');
    }
  }

  async function submitZiekte() {
    if (!reden || !ziekteStart || !ziekteEind)
      return toonFeedback('fout', 'Vul alle velden in.');
    try {
      const formData = new FormData();
      formData.append('werknemerId', String(user?.id));
      formData.append('startDatum', ziekteStart);
      formData.append('eindDatum', ziekteEind);
      formData.append('reden', reden);
      if (certificaat) formData.append('certificaat', certificaat);
      const res = await fetch(`${BASE}/afwezigheid`, {
        method: 'POST',
        headers: authHeader,
        body: formData,
      });
      if (!res.ok) throw new Error();
      toonFeedback('ok', 'Ziekte gemeld!');
      setReden('');
      setZiekteStart('');
      setZiekteEind('');
      setCertificaat(null);
    } catch {
      toonFeedback('fout', 'Er is iets misgegaan.');
    }
  }

  useEffect(() => {
    if (tab !== 'geschiedenis' || !user?.id) return;
    fetch(`${BASE}/geschiedenis/team/${user.id}`, { headers: authHeader })
      .then((r) => r.json())
      .then(setTeamleden)
      .catch(console.error);
  }, [tab, user?.id, authHeader]);

  async function laadGeschiedenis(lid: Teamlid) {
    setGeselecteerdLid(lid);
    setGeschiedenisLaden(true);
    try {
      const res = await fetch(`${BASE}/geschiedenis/werknemer/${lid.id}`, {
        headers: authHeader,
      });
      setGeschiedenis(await res.json());
    } catch {
      setGeschiedenis([]);
    } finally {
      setGeschiedenisLaden(false);
    }
  }

  function statusKleur(status: string | null) {
    switch (status) {
      case 'Goedgekeurd':
        return 'text-emerald-600 bg-emerald-50';
      case 'Afgewezen':
        return 'text-red-500 bg-red-50';
      case 'Geannuleerd':
        return 'text-zinc-400 bg-zinc-100';
      case 'In afwachting':
        return 'text-amber-600 bg-amber-50';
      default:
        return 'text-zinc-500 bg-zinc-100';
    }
  }

  const tabs: { key: Tab; label: string }[] = [
    { key: 'verlof', label: 'Verlof aanvragen' },
    { key: 'ziekte', label: 'Ziekte melden' },
    ...(isMgr ? [{ key: 'geschiedenis' as Tab, label: 'Geschiedenis' }] : []),
  ];

  return (
    <PageContainer className="h-full">
      <BreadcrumbInit pages={['afwezigheden']} />
      <AppContainer>
        <div className="w-full max-w-4xl mx-auto flex flex-col gap-6">
          {/* Feedback */}
          {feedback && (
            <div
              className={`px-4 py-3 rounded-2xl text-sm font-medium border ${feedback.type === 'ok' ? 'bg-emerald-50 text-emerald-700 border-emerald-200' : 'bg-red-50 text-red-600 border-red-200'}`}
            >
              {feedback.bericht}
            </div>
          )}

          {/* Tabs — gecentreerd */}
          <div className="flex justify-center">
            <div className="flex gap-1 bg-gray-300/30 border border-gray-300/30 rounded-full p-1 shadow-sm">
              {tabs.map((t) => (
                <button
                  key={t.key}
                  onClick={() => setTab(t.key)}
                  className={`px-5 py-2 rounded-full text-sm font-bold transition-all duration-300 ${tab === t.key ? 'bg-zinc-900 text-white shadow' : 'text-zinc-500 hover:text-zinc-800 hover:bg-zinc-200/50'}`}
                >
                  {t.label}
                </button>
              ))}
            </div>
          </div>

          {/* ── VERLOF ── */}
          {tab === 'verlof' && (
            <div className="w-full">
              <Container>
                <div className="flex flex-col gap-6 p-2">
                  <span className="text-xl font-bold text-zinc-900">
                    Verlof aanvragen
                  </span>
                  <div className="flex gap-4">
                    <div className="flex flex-col gap-1 flex-1">
                      <label className="text-xs font-semibold text-zinc-500 uppercase tracking-wide">
                        Startdatum
                      </label>
                      <input
                        type="date"
                        value={verlofStart}
                        onChange={(e) => setVerlofStart(e.target.value)}
                        className="border border-gray-300/30 rounded-2xl px-4 py-2.5 text-sm bg-gray-300/20 focus:outline-none focus:ring-2 focus:ring-zinc-900/20 w-full"
                      />
                    </div>
                    <div className="flex flex-col gap-1 flex-1">
                      <label className="text-xs font-semibold text-zinc-500 uppercase tracking-wide">
                        Einddatum
                      </label>
                      <input
                        type="date"
                        value={verlofEind}
                        onChange={(e) => setVerlofEind(e.target.value)}
                        className="border border-gray-300/30 rounded-2xl px-4 py-2.5 text-sm bg-gray-300/20 focus:outline-none focus:ring-2 focus:ring-zinc-900/20 w-full"
                      />
                    </div>
                  </div>
                  <div className="flex flex-col gap-1">
                    <label className="text-xs font-semibold text-zinc-500 uppercase tracking-wide">
                      Type verlof
                    </label>
                    <select
                      value={verlofType}
                      onChange={(e) => setVerlofType(e.target.value)}
                      className="border border-gray-300/30 rounded-2xl px-4 py-2.5 text-sm bg-gray-300/20 focus:outline-none focus:ring-2 focus:ring-zinc-900/20 w-full"
                    >
                      <option>Jaarlijks verlof</option>
                      <option>Onbetaald verlof</option>
                      <option>Bijzonder verlof</option>
                    </select>
                  </div>
                  <div className="w-40">
                    <Button
                      label="Aanvragen"
                      color="zinc-900"
                      textColor="white"
                      onClick={submitVerlof}
                    />
                  </div>
                </div>
              </Container>
            </div>
          )}

          {/* ── ZIEKTE ── */}
          {tab === 'ziekte' && (
            <div className="w-full">
              <Container>
                <div className="flex flex-col gap-6 p-2">
                  <span className="text-xl font-bold text-zinc-900">
                    Ziekte melden
                  </span>
                  <div className="flex flex-col gap-1">
                    <label className="text-xs font-semibold text-zinc-500 uppercase tracking-wide">
                      Reden
                    </label>
                    <input
                      type="text"
                      value={reden}
                      onChange={(e) => setReden(e.target.value)}
                      placeholder="Reden voor afwezigheid..."
                      className="border border-gray-300/30 rounded-2xl px-4 py-2.5 text-sm bg-gray-300/20 focus:outline-none focus:ring-2 focus:ring-zinc-900/20 w-full"
                    />
                  </div>
                  <div className="flex gap-4">
                    <div className="flex flex-col gap-1 flex-1">
                      <label className="text-xs font-semibold text-zinc-500 uppercase tracking-wide">
                        Startdatum
                      </label>
                      <input
                        type="date"
                        value={ziekteStart}
                        onChange={(e) => setZiekteStart(e.target.value)}
                        className="border border-gray-300/30 rounded-2xl px-4 py-2.5 text-sm bg-gray-300/20 focus:outline-none focus:ring-2 focus:ring-zinc-900/20 w-full"
                      />
                    </div>
                    <div className="flex flex-col gap-1 flex-1">
                      <label className="text-xs font-semibold text-zinc-500 uppercase tracking-wide">
                        Einddatum
                      </label>
                      <input
                        type="date"
                        value={ziekteEind}
                        onChange={(e) => setZiekteEind(e.target.value)}
                        className="border border-gray-300/30 rounded-2xl px-4 py-2.5 text-sm bg-gray-300/20 focus:outline-none focus:ring-2 focus:ring-zinc-900/20 w-full"
                      />
                    </div>
                  </div>

                  {/* Upload */}
                  <div className="flex flex-col gap-1">
                    <label className="text-xs font-semibold text-zinc-500 uppercase tracking-wide">
                      Ziektebriefje
                    </label>
                    <div
                      onDragOver={(e) => {
                        e.preventDefault();
                        setIsDragging(true);
                      }}
                      onDragLeave={() => setIsDragging(false)}
                      onDrop={(e) => {
                        e.preventDefault();
                        setIsDragging(false);
                        const f = e.dataTransfer.files[0];
                        if (f) setCertificaat(f);
                      }}
                      onClick={() =>
                        document.getElementById('fileInput')?.click()
                      }
                      className={`border-2 border-dashed rounded-2xl p-8 flex flex-col items-center justify-center gap-2 cursor-pointer transition-all duration-300 ${isDragging ? 'border-zinc-900 bg-zinc-100' : 'border-gray-300/50 bg-gray-300/10 hover:border-gray-400/50'}`}
                    >
                      <svg
                        xmlns="http://www.w3.org/2000/svg"
                        className="w-6 h-6 text-zinc-800"
                        fill="none"
                        viewBox="0 0 24 24"
                        stroke="currentColor"
                        strokeWidth={2}
                      >
                        <path
                          strokeLinecap="round"
                          strokeLinejoin="round"
                          d="M4 16v2a2 2 0 002 2h12a2 2 0 002-2v-2M12 12V4m0 0l-3 3m3-3l3 3"
                        />
                      </svg>
                      <span className="text-sm text-zinc-400">
                        {certificaat
                          ? certificaat.name
                          : 'Sleep bestand hierheen of klik om te bladeren'}
                      </span>
                      <input
                        id="fileInput"
                        type="file"
                        accept=".pdf,.png,.jpg,.jpeg"
                        className="hidden"
                        onChange={(e) => {
                          if (e.target.files?.[0])
                            setCertificaat(e.target.files[0]);
                        }}
                      />
                    </div>
                  </div>

                  <div className="w-40">
                    <Button
                      label="Melden"
                      color="zinc-900"
                      textColor="white"
                      onClick={submitZiekte}
                    />
                  </div>
                </div>
              </Container>
            </div>
          )}

          {/* ── GESCHIEDENIS ── */}
          {tab === 'geschiedenis' && (
            <div className="flex gap-4 min-h-[500px] w-full">
              <div className="w-56 flex-shrink-0">
                <Container height="full">
                  <div className="flex flex-col gap-2 p-1">
                    <span className="text-xs font-bold text-zinc-400 uppercase tracking-wide px-2">
                      Teamleden
                    </span>
                    {teamleden.length === 0 && (
                      <p className="text-xs text-zinc-400 px-2">
                        Geen teamleden.
                      </p>
                    )}
                    {teamleden.map((lid) => (
                      <button
                        key={lid.id}
                        onClick={() => laadGeschiedenis(lid)}
                        className={`w-full text-left px-3 py-2.5 rounded-2xl text-sm font-medium transition-all duration-200 ${geselecteerdLid?.id === lid.id ? 'bg-zinc-900 text-white' : 'hover:bg-gray-300/30 text-zinc-700'}`}
                      >
                        {lid.voornaam} {lid.naam}
                        <span
                          className={`block text-xs mt-0.5 ${geselecteerdLid?.id === lid.id ? 'text-zinc-300' : 'text-zinc-400'}`}
                        >
                          {lid.rol}
                        </span>
                      </button>
                    ))}
                  </div>
                </Container>
              </div>
              <div className="flex-1 min-w-0">
                <Container height="full">
                  <div className="flex flex-col gap-3 p-1">
                    {!geselecteerdLid && (
                      <div className="flex items-center justify-center py-20 text-zinc-300 text-sm">
                        Selecteer een teamlid
                      </div>
                    )}
                    {geselecteerdLid && (
                      <>
                        <span className="text-base font-bold text-zinc-900">
                          {geselecteerdLid.voornaam} {geselecteerdLid.naam}
                        </span>
                        {geschiedenisLaden && (
                          <p className="text-sm text-zinc-400">Laden...</p>
                        )}
                        {!geschiedenisLaden && geschiedenis.length === 0 && (
                          <p className="text-sm text-zinc-400">
                            Geen geschiedenis gevonden.
                          </p>
                        )}
                        {geschiedenis.map((item) => (
                          <div
                            key={item.id}
                            className={`flex items-center justify-between px-4 py-3 rounded-2xl border ${item.type === 'Ziekte' ? 'bg-red-50/50 border-red-100' : item.status === 'In afwachting' ? 'bg-amber-50/50 border-amber-100' : item.status === 'Afgewezen' || item.status === 'Geannuleerd' ? 'bg-zinc-50 border-zinc-200' : 'bg-emerald-50/50 border-emerald-100'}`}
                          >
                            <div className="flex flex-col gap-0.5">
                              <span className="text-sm font-semibold text-zinc-800">
                                {formatDatum(item.startDatum)}
                                {item.startDatum !== item.eindDatum &&
                                  ` – ${formatDatum(item.eindDatum)}`}
                              </span>
                              {item.omschrijving && (
                                <span className="text-xs text-zinc-500">
                                  {item.omschrijving}
                                </span>
                              )}
                            </div>
                            <div className="flex items-center gap-2">
                              <span
                                className={`text-xs font-bold px-2 py-1 rounded-full ${item.type === 'Ziekte' ? 'bg-red-100 text-red-500' : 'bg-zinc-100 text-zinc-600'}`}
                              >
                                {item.type}
                              </span>
                              {item.status && (
                                <span
                                  className={`text-xs font-bold px-2 py-1 rounded-full ${statusKleur(item.status)}`}
                                >
                                  {item.status}
                                </span>
                              )}
                            </div>
                          </div>
                        ))}
                      </>
                    )}
                  </div>
                </Container>
              </div>
            </div>
          )}
        </div>
      </AppContainer>
    </PageContainer>
  );
}
