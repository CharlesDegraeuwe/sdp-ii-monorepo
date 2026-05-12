'use client';
import Container from '../../../../../components/design-system/Container/Container';
import Button from '../../../../../components/design-system/Button/Button';
import { useMemo, useState } from 'react';
import { useSession } from 'next-auth/react';
import { TabSwitcher } from '@/components/design-system/TabSwitcher/TabSwitcher';
import VerlofTab from '@/components/app/afwezigheden/VerlofTab';
import ZiekteTab from '@/components/app/afwezigheden/ZiekteTab';
import { useToast } from '@/providers/ToastProvider';

type Tab = 'verlof' | 'ziekte';

const tabs: { key: Tab; label: string }[] = [
  { key: 'verlof', label: 'Verlof' },
  { key: 'ziekte', label: 'Ziekte' },
];

export default function MeldenPage() {
  const { data: session } = useSession();
  const token = session?.accessToken;
  const user = session?.user;
  const [tab, setTab] = useState<Tab>('verlof');
  const toast = useToast();

  //verlof
  const [verlofStart, setVerlofStart] = useState('');
  const [verlofEind, setVerlofEind] = useState('');
  const [verlofType, setVerlofType] = useState('Jaarlijks verlof');

  //ziekte
  const [reden, setReden] = useState('');
  const [ziekteStart, setZiekteStart] = useState('');
  const [ziekteEind, setZiekteEind] = useState('');
  const [certificaat, setCertificaat] = useState<File | null>(null);

  const BASE = process.env.NEXT_PUBLIC_API_URL ?? 'http://localhost:8080/api';

  const authHeader = useMemo(
    () => ({ Authorization: `Bearer ${token}` }),
    [token],
  );

  async function submitVerlof() {
    try {
      await fetch(`${BASE}/verlof`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json', ...authHeader },
        body: JSON.stringify({
          werknemerId: Number(user?.id),
          startDatum: verlofStart,
          eindDatum: verlofEind,
          type: verlofType,
        }),
      });
      setVerlofStart('');
      setVerlofEind('');
      toast.success('Verlof aangevraagd');
    } catch {
      toast.error('Verlof aanvragen mislukt');
    }
  }

  async function submitZiekte() {
    try {
      const formData = new FormData();
      formData.append('werknemerId', String(user?.id));
      formData.append('startDatum', ziekteStart);
      formData.append('eindDatum', ziekteEind);
      formData.append('reden', reden);
      if (certificaat) formData.append('certificaat', certificaat);
      await fetch(`${BASE}/afwezigheid`, {
        method: 'POST',
        headers: authHeader,
        body: formData,
      });
      setReden('');
      setZiekteStart('');
      setZiekteEind('');
      setCertificaat(null);
      toast.success('Ziekte gemeld');
    } catch {
      toast.error('Ziekte melden mislukt');
    }
  }

  return (
    <div className={'w-full h-fit'}>
      <Container>
        <div className="flex flex-col gap-6 p-2">
          <div className="flex flex-row items-center justify-between">
            <span className="text-xl font-bold text-zinc-900">
              {tab == 'verlof' ? 'Verlof aanvragen' : 'Ziekte melden'}
            </span>
            <TabSwitcher
              size={'sm'}
              tabs={tabs}
              value={tab}
              onChange={(key) => {
                setTab(key as Tab);
              }}
            />
          </div>
          <div
            className={`${tab === 'verlof' ? 'min-h-fit' : 'min-h-100'} flex flex-col gap-5`}
          >
            {tab === 'verlof' ? (
              <VerlofTab
                verlofStart={verlofStart}
                verlofEind={verlofEind}
                verlofType={verlofType}
                setVerlofStart={setVerlofStart}
                setVerlofEind={setVerlofEind}
                setVerlofType={setVerlofType}
              />
            ) : (
              <ZiekteTab
                reden={reden}
                ziekteStart={ziekteStart}
                ziekteEind={ziekteEind}
                certificaat={certificaat}
                setReden={setReden}
                setZiekteStart={setZiekteStart}
                setZiekteEind={setZiekteEind}
                setCertificaat={setCertificaat}
              />
            )}
          </div>
          <div className="w-full flex items-center justify-end">
            <Button
              label="Aanvragen"
              color="zinc-900"
              textColor="white"
              onClick={tab === 'verlof' ? submitVerlof : submitZiekte}
            />
          </div>
        </div>
      </Container>
    </div>
  );
}
