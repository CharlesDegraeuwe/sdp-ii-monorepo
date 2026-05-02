import Image from 'next/image';

export default function MailPage() {
  return (
    <div
      className={'w-full h-full flex items-center justify-center p-10 bg-white'}
    >
      <div className={'flex flex-col gap-5'}>
        <Image
          src={'/logo.svg'}
          width={0}
          height={0}
          alt={'logo'}
          className={'h-20 w-fit '}
        />
        <hr />
        <div className={'flex flex-col gap-3'}>
          <h1 className={'text-2xl font-bold'}>
            Login code voor delware suite
          </h1>
          <p>
            Dit is je unieke logincode voor delaware suite. Deze blijft 10
            minuten geldig, deel dit met niemand.
          </p>
          <span
            className={
              'rounded-2xl w-fit px-3 py-2 font-bold text-3xl bg-zinc-100'
            }
          >
            155877
          </span>
        </div>
      </div>
    </div>
  );
}
