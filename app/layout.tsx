import { Geist, Geist_Mono } from 'next/font/google';
import './globals.css';
import SessionProvider from '@/providers/SessionProvider';
import { UserProvider } from '@/providers/UserProvider';
import Toast from '@/components/design-system/Toast/Toast';

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
  const showToast = false;
  return (
    <html
      lang="en"
      className={`${geistSans.variable} ${geistMono.variable} h-full antialiased`}
    >
      <body className="relative flex flex-col h-screen w-screen">
        <SessionProvider>
          <UserProvider>
            {showToast && <Toast />}
            {children}
          </UserProvider>
        </SessionProvider>
      </body>
    </html>
  );
}
