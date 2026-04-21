import GoogleMaps from '@/app/(pages)/(app)/locaties/components/googlemaps';
import { Container } from '@/components/design system/Container';
import { Input } from '@/components/design system/Input';
import type { Metadata } from 'next';

export const metadata: Metadata = {
  title: 'Locaties | Delaware Suite',
};

export default function Page() {
  return (
    <div className={'relative z-0 w-full h-full flex'}>
      <div className="flex flex-col gap-5 absolute h-full max-w-1/5 w-1/5 z-40 left-20 top-30">
        <Container bg={'zinc-50'} className={'max-h-3/4 h-3/4 pt-5'}>
          <Input placeholder={'zoeken...'} color={'white'} />
        </Container>
      </div>
      <GoogleMaps />
    </div>
  );
}
