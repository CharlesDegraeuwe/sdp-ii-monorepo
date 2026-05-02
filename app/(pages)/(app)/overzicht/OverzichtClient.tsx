'use client';

import { afwezighedenOpDag } from '../planner/components/utils';
import { useOverzichtData } from './components/useOverzichtData';
import { SnelleActies } from './components/SnelleActies';
import { GeplandUren } from './components/GeplandUren';
import { MiniKalender } from './components/MiniKalender';
import { NotificatiesWidget } from './components/NotificatiesWidget';
import { OpenTaken } from './components/OpenTaken';
import { AfwezighedenWidget } from './components/AfwezighedenWidget';
import { LocatieInfo } from './components/LocatieInfo';

export default function OverzichtClient() {
  const { afwezigheden, notificaties } = useOverzichtData();

  const vandaag = new Date();
  const afwezigVandaag = afwezighedenOpDag(afwezigheden, vandaag);
  const inAfwachting = afwezigheden.filter((a) => a.status === 'In afwachting');
  const aantalOngelezen = notificaties.filter(
    (n) => n.gelezen === 'Nee',
  ).length;

  return (
    <div className="flex flex-row gap-4 w-full h-full p-4 overflow-hidden">
      {/* LINKER KOLOM */}
      <div className="flex flex-col gap-4 flex-1 min-w-0 overflow-hidden">
        <SnelleActies />
        <GeplandUren afwezigheden={afwezigheden} />
      </div>

      {/* RECHTER KOLOM */}
      <div className="flex flex-col gap-4 flex-1 min-w-0 overflow-hidden">
        {/* Rij 1: Kalender + Notificaties */}
        <div className="grid grid-cols-2 gap-4">
          <MiniKalender afwezigheden={afwezigheden} />
          <NotificatiesWidget notificaties={notificaties} />
        </div>

        {/* Rij 2: Open Taken + Afwezigheden */}
        <div className="grid grid-cols-2 gap-4">
          <OpenTaken inAfwachting={inAfwachting} />
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
