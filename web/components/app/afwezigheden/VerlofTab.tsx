'use client';

import { Label } from '@/components/design-system/Label';
import { Input } from '@/components/design-system/Input';
import Select from '@/components/design-system/Select/Select';

interface VerlofTabProps {
  verlofStart: string;
  setVerlofStart: (verlofStart: string) => void;
  verlofEind: string;
  setVerlofEind: (verlofEind: string) => void;
  verlofType: string;
  setVerlofType: (verlofType: string) => void;
}

const VerlofTab = (props: VerlofTabProps) => {
  const {
    setVerlofStart,
    setVerlofType,
    verlofEind,
    setVerlofEind,
    verlofType,
    verlofStart,
  } = props;

  return (
    <>
      <div className="flex flex-col sm:flex-row gap-4">
        <div className="flex flex-col gap-1 flex-1">
          <Label text={'Startdatum'} verplicht />
          <Input
            type="date"
            value={verlofStart}
            onChange={(e) => setVerlofStart(e.target.value)}
          />
        </div>
        <div className="flex flex-col gap-1 flex-1">
          <Label text={'Einddatum'} verplicht />
          <Input
            type="date"
            value={verlofEind}
            onChange={(e) => setVerlofEind(e.target.value)}
          />
        </div>
      </div>
      <div className="flex flex-col gap-1">
        <Label text={'Type Verlof'} verplicht />
        <Select
          value={verlofType}
          onChange={(v) => setVerlofType(String(v))}
          placeholder="Kies een land"
          options={[
            { value: 'jaarlijks_verlof', label: 'Jaarlijks verlof' },
            { value: 'onbetaald_verlof', label: 'Onbetaald verlof' },
            { value: 'bijzonder_verlof', label: 'Bijzonder verlof' },
          ]}
        />
      </div>
    </>
  );
};

VerlofTab.displayName = 'VerlofTab';
export default VerlofTab;
