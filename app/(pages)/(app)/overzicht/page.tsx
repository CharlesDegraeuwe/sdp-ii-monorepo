import { Container } from '@/components/design system/Container';

export default function Page() {
  return (
    <div className="w-full min-h-screen grid grid-cols-4 grid-rows-4 gap-5 px-7 pt-40 py-20">
      <Container
        label={'Snelle acties:'}
        className="col-start-1 col-end-3 row-start-1 row-end-2"
      />

      <Container
        label={'Geplande uren:'}
        className="col-start-1 col-end-3 row-start-2 row-end-5"
      />

      <Container
        label={'Kalender:'}
        className="col-start-3 col-end-4 row-start-1 row-end-3"
      />

      <Container
        label={'Notificaties:'}
        className="col-start-4 col-end-5 row-start-1 row-end-3"
      />

      <Container
        label={'Snelle statistieken:'}
        className="col-start-3 col-end-4 row-start-3 row-end-4"
      />

      <Container
        indent={true}
        className="col-start-4 col-end-5 row-start-3 row-end-4"
      />

      <Container className="col-start-3 col-end-5 row-start-4 row-end-5" />
    </div>
  );
}
