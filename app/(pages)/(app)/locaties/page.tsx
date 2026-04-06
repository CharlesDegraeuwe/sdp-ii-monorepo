import GoogleMaps from '@/app/(pages)/(app)/locaties/components/googlemaps';
import { Container } from '@/components/design system/Container';
import { Input } from '@/components/design system/Input';
import { color } from 'listr2';
import label from '@/components/design system/Label/Label';

export default function Page() {
  return (
    <div className={'relative z-0 w-full h-full flex'}>
      <div className="flex flex-col gap-5 absolute h-full max-w-1/5 w-1/5 z-40 left-5 top-30">
        <Container className={'bg-white max-h-3/4 h-3/4 pt-5'}>
          <Input placeholder={'zoeken...'} color={'white'} />
        </Container>
      </div>
      <GoogleMaps />
    </div>
  );
}
