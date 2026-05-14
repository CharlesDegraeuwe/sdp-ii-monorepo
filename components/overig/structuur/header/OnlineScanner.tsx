'use client';
import { useEffect, useState } from 'react';
import { RiWifiOffLine } from 'react-icons/ri';

const OnlineScanner = () => {
  const [isOnline, setIsOnline] = useState(() =>
    typeof window !== 'undefined' ? window.navigator.onLine : true,
  );

  useEffect(() => {
    const handleOnline = () => setIsOnline(true);
    const handleOffline = () => setIsOnline(false);
    window.addEventListener('online', handleOnline);
    window.addEventListener('offline', handleOffline);
    return () => {
      window.removeEventListener('online', handleOnline);
      window.removeEventListener('offline', handleOffline);
    };
  }, []);

  return (
    <>
      {!isOnline && (
        <div
          className={
            'truncate text-rose-700 font-medium text-sm flex flex-row gap-1 sm:gap-2 items-center'
          }
        >
          <RiWifiOffLine />
          <span className="hidden sm:inline">je bent offline</span>
        </div>
      )}
    </>
  );
};

OnlineScanner.displayName = 'OnlineScanner';
export default OnlineScanner;
