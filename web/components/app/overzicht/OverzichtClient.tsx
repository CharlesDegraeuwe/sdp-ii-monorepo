'use client';

import { afwezighedenOpDag } from '../planner/utils';
import { SnelleActies } from './SnelleActies';
import { GeplandUren } from './GeplandUren';
import { MiniKalender } from './MiniKalender';
import { NotificatiesWidget } from './NotificatiesWidget';
import { OpenTaken } from './OpenTaken';
import { AfwezighedenWidget } from './AfwezighedenWidget';
import { LocatieInfo } from './LocatieInfo';
import { useOverzichtData } from '@/hooks/useOverzichtData';

export default function OverzichtClient() {
  const { afwezigheden, notificaties, taken, refreshNotificaties } =
    useOverzichtData();

  const vandaag = new Date();
  const afwezigVandaag = afwezighedenOpDag(afwezigheden, vandaag);
  const inAfwachting = afwezigheden.filter((a) => a.status === 'In afwachting');
  const aantalOngelezen = notificaties.filter(
    (n) => n.gelezen === 'Nee',
  ).length;

  return (
    <div
      className="w-full grid grid-cols-4 gap-5"
      style={{ gridTemplateRows: 'repeat(5, minmax(180px, auto))' }}
    >
      <SnelleActies />
      <GeplandUren />
      <MiniKalender afwezigheden={afwezigheden} />
      <NotificatiesWidget
        notificaties={notificaties}
        onRefresh={refreshNotificaties}
      />
      <OpenTaken taken={taken} />
      <AfwezighedenWidget
        afwezigVandaag={afwezigVandaag}
        inAfwachting={inAfwachting}
        aantalOngelezen={aantalOngelezen}
      />
      <LocatieInfo />
    </div>
  );
}
