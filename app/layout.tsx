import { Geist, Geist_Mono } from 'next/font/google';
import './globals.css';
import SessionProvider from '@/providers/SessionProvider';
import { UserProvider } from '@/providers/UserProvider';
import { ToastProvider } from '@/providers/ToastProvider';
import { SplashProvider } from '@/providers/SplashProvider';
import { SettingsProvider } from '@/providers/SettingsProvider';
import { ReactQueryProvider } from '@/providers/ReactQueryProvider';

const geistSans = Geist({
  variable: '--font-geist-sans',
  subsets: ['latin'],
});

const geistMono = Geist_Mono({
  variable: '--font-geist-mono',
  subsets: ['latin'],
});

export default function RootLayout({
  children,
}: Readonly<{
  children: React.ReactNode;
}>) {
  return (
    <html
      lang="en"
      className={`${geistSans.variable} ${geistMono.variable} h-full antialiased`}
    >
      <body className="relative flex flex-col h-screen w-screen overflow-x-hidden">
        <SettingsProvider>
          <SessionProvider>
            <ReactQueryProvider>
              <UserProvider>
                <SplashProvider>
                  <ToastProvider>{children}</ToastProvider>
                </SplashProvider>
              </UserProvider>
            </ReactQueryProvider>
          </SessionProvider>
        </SettingsProvider>
      </body>
    </html>
  );
}
