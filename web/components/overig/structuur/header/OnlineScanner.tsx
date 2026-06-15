'use client';
import { RiWifiOffLine } from 'react-icons/ri';

const OnlineScanner = () => {
  return (
    <>
      {!window.navigator.onLine && (
        <div
          className={
            'truncate text-rose-700 font-medium text-sm flex flex-row gap-2 items-center'
          }
        >
          <RiWifiOffLine />
          <span>je bent offline</span>
        </div>
      )}
    </>
  );
};

OnlineScanner.displayName = 'OnlineScanner';
export default OnlineScanner;
