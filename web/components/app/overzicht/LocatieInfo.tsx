import NextLink from 'next/link';
import { MapPinIcon, ArrowRightIcon } from '@phosphor-icons/react';
import { Container } from '@/components/design-system/Container';

export function LocatieInfo() {
  return (
    <Container label={'Locatie Info'}>
      <div className="flex flex-col gap-3 h-full">
        <div className="flex items-center gap-2">
          <MapPinIcon size={14} className="text-zinc-500 shrink-0" />
          <span className="text-xs font-semibold text-zinc-600">
            Delaware Consulting
          </span>
        </div>

        <div className="flex flex-col gap-1.5 flex-1">
          <div className="flex items-center justify-between px-3 py-2 rounded-2xl border border-gray-300/30 bg-gray-300/20">
            <span className="text-xs text-zinc-500">Hoofdkantoor</span>
            <span className="text-xs font-semibold text-zinc-800">
              Gent, België
            </span>
          </div>
          <div className="flex items-center justify-between px-3 py-2 rounded-2xl border border-gray-300/30 bg-gray-300/20">
            <span className="text-xs text-zinc-500">Teamleden op locatie</span>
            <span className="text-xs font-semibold text-zinc-800">—</span>
          </div>
        </div>

        <NextLink
          href="/locaties"
          className="shrink-0 flex items-center justify-center gap-1.5 border-t border-gray-300/30 pt-3 text-xs font-semibold text-zinc-400 hover:text-zinc-700 transition-colors duration-200"
        >
          <MapPinIcon size={13} />
          Bekijk kaart
          <ArrowRightIcon size={11} className="ml-auto" />
        </NextLink>
      </div>
    </Container>
  );
}
