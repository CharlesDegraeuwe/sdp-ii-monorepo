import Link from 'next/link';
import { MdOutlineLocationOn } from 'react-icons/md';
import { Container } from '@/components/design-system/Container';

export function LocatieInfo() {
  return (
    <Container
      label={'Locatie Info'}
      className="col-start-3 col-end-5 row-start-4 row-end-6"
    >
        <div className="flex flex-col gap-2 h-full">
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
              <span className="text-xs text-zinc-600">
                Teamleden op locatie
              </span>
              <span className="text-xs font-semibold text-zinc-800">—</span>
            </div>
            <Link
              href="/locaties"
              className="mt-auto flex items-center justify-center gap-1.5 px-4 py-2 rounded-xl border border-gray-200/40 bg-white/40 hover:bg-white/70 hover:border-gray-300/60 transition-all duration-200 text-xs font-semibold text-zinc-600"
            >
              <MdOutlineLocationOn size={14} />
              Bekijk kaart
            </Link>
          </div>
          <Link
            href="/locaties"
            className="mt-auto flex items-center justify-center gap-1.5 px-4 py-2.5 rounded-2xl border border-gray-200/40 bg-white/40 hover:bg-white/70 hover:border-gray-300/60 transition-all duration-200 text-xs font-semibold text-zinc-600"
          >
            <MdOutlineLocationOn size={14} />
            Bekijk kaart
          </Link>
        </div>
      </div>
    </Container>
  );
}
