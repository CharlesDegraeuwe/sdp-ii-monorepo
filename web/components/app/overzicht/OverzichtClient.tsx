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
    <div className="w-full flex flex-col xl:flex-row gap-5 pb-5">
      {/* Left column: SnelleActies + GeplandUren */}
      <div className="flex flex-col gap-5 xl:w-1/2">
        <SnelleActies />
        <div className="flex-1 min-h-80">
          <GeplandUren />
        </div>
      </div>

      {/* Right column: 2x2 widgets + LocatieInfo */}
      <div className="flex flex-col gap-5 xl:w-1/2">
        <div className="grid grid-cols-1 sm:grid-cols-2 gap-5">
          <div className="min-h-48">
            <MiniKalender afwezigheden={afwezigheden} />
          </div>
          <div className="min-h-48">
            <NotificatiesWidget
              notificaties={notificaties}
              onRefresh={refreshNotificaties}
            />
          </div>
          <div className="min-h-48">
            <OpenTaken taken={taken} />
          </div>
          <div className="min-h-48">
            <AfwezighedenWidget
              afwezigVandaag={afwezigVandaag}
              inAfwachting={inAfwachting}
              aantalOngelezen={aantalOngelezen}
            />
          </div>
        </div>
        <div className="min-h-24">
          <LocatieInfo />
        </div>
      </div>
    </div>
  );
}
