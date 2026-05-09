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
    <div className="flex flex-row gap-3 w-full h-full p-3 overflow-hidden">
      <div className="flex flex-col gap-3 flex-1 min-w-0 overflow-hidden scroll-hidden">
        <SnelleActies />
        <GeplandUren afwezigheden={afwezigheden} />
      </div>

      <div className="flex flex-col gap-3 flex-1 min-w-0 overflow-hidden scroll-hidden">
        {/* Rij 1: Kalender + Notificaties */}
        <div className="grid grid-cols-2 gap-3">
          <MiniKalender afwezigheden={afwezigheden} />
          <NotificatiesWidget
            notificaties={notificaties}
            onRefresh={refreshNotificaties}
          />
        </div>

        {/* Rij 2: Open Taken + Afwezigheden */}
        <div className="grid grid-cols-2 gap-3">
          <OpenTaken taken={taken} />
          <AfwezighedenWidget
            afwezigVandaag={afwezigVandaag}
            inAfwachting={inAfwachting}
            aantalOngelezen={aantalOngelezen}
          />
        </div>

        {/* Rij 3: Locatie Info */}
        <LocatieInfo />
      </div>
    </div>
  );
}
