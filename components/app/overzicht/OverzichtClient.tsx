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
    <div className="w-full h-full grid grid-cols-4 grid-rows-3 gap-5">
      <div className="col-span-2 row-span-3 flex flex-col gap-5">
        <SnelleActies />
        <GeplandUren />
      </div>

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
