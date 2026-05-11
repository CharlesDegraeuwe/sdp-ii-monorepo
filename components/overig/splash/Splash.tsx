const SplashOverlay = () => {
  return (
    <div
      className={
        'min-w-screen z-[9999] min-h-screen bg-zinc-50 absolute flex items-center justify-center'
      }
    >
      <div className={'w-1/4 h-50 border flex flex-col'}>
        <span></span>
      </div>
    </div>
  );
};

SplashOverlay.displayName = 'SplashOverlay';
export default SplashOverlay;
