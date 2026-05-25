import { MdOutlineLocationOn } from 'react-icons/md';
import { Container } from '@/components/design-system/Container';
import Link from '@/components/design-system/Link/Link';

export function LocatieInfo() {
  return (
    <Container label={'Locatie Info'} className="col-span-2 flex-1 min-h-100 ">
      <div className="relative flex-1 flex flex-col h-full gap-2">
        <div className="flex items-center gap-2 px-1">
          <MdOutlineLocationOn size={16} className="text-zinc-500" />
          <span className="text-xs font-semibold text-zinc-600">
            Delaware Consulting
          </span>
        </div>
        <div className="flex flex-col gap-1.5 flex-1">
          <div className="flex items-center justify-between px-2.5 py-2 rounded-xl border border-gray-200/40 bg-white/30">
            <span className="text-xs text-zinc-600">Hoofdkantoor</span>
            <span className="text-xs font-semibold text-zinc-800">
              Gent, België
            </span>
          </div>
          <div className="flex items-center justify-between px-2.5 py-2 rounded-xl border border-gray-200/40 bg-white/30">
            <span className="text-xs text-zinc-600">Teamleden op locatie</span>
            <span className="text-xs font-semibold text-zinc-800">—</span>
          </div>
          <div className={'absolute bottom-0 w-full '}>
            <Link
              label={'Bekijk kaart'}
              icon={<MdOutlineLocationOn size={14} />}
              href="/locaties"
            />
          </div>
        </div>
      </div>
    </Container>
  );
}
