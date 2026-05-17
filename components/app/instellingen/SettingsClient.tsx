'use client';

import { useState, useEffect, useCallback } from 'react';
import { useRouter } from 'next/navigation';
import SettingOptions from '@/components/app/instellingen/options';
import PageSection from '@/components/design-system/PageSection/PageSection';
import Button from '@/components/design-system/Button/Button';
import Select from '@/components/design-system/Select/Select';
import Input from '@/components/design-system/Input/Input';
import Link from '@/components/design-system/Link/Link';
import { useUser } from '@/providers/UserProvider';
import { applySettings } from '@/providers/SettingsProvider';

const STORAGE_KEY = 'sdp2_settings';

function loadSettings(): Record<string, string | boolean | number> {
  if (typeof window === 'undefined') return {};
  try {
    return JSON.parse(localStorage.getItem(STORAGE_KEY) ?? '{}');
  } catch {
    return {};
  }
}

function saveSettings(settings: Record<string, string | boolean | number>) {
  localStorage.setItem(STORAGE_KEY, JSON.stringify(settings));
}

const Toggle = ({
  checked,
  onChange,
}: {
  checked: boolean;
  onChange: (v: boolean) => void;
}) => (
  <button
    type="button"
    role="switch"
    aria-checked={checked}
    onClick={() => onChange(!checked)}
    className={`relative inline-flex h-6 cursor-pointer w-11 items-center rounded-full transition-colors duration-200 focus:outline-none ${
      checked ? 'bg-rose-500' : 'bg-zinc-300'
    }`}
  >
    <span
      className={`inline-block h-4 w-4 transform rounded-full bg-white shadow transition-transform duration-200 ${
        checked ? 'translate-x-6' : 'translate-x-1'
      }`}
    />
  </button>
);

const START_VIEW_COOKIE = 'sdp2_start_view';

const SettingsClient = () => {
  const { user } = useUser();
  const router = useRouter();
  const currentUser = user?.rol?.toLowerCase();

  const [settings, setSettings] = useState<
    Record<string, string | boolean | number>
  >({});
  const [accountName, setAccountName] = useState('');
  const [accountEmail, setAccountEmail] = useState('');
  const [savingAccount, setSavingAccount] = useState(false);
  const [accountSaved, setAccountSaved] = useState(false);

  const [showPasswordForm, setShowPasswordForm] = useState(false);
  const [oudWachtwoord, setOudWachtwoord] = useState('');
  const [nieuwWachtwoord, setNieuwWachtwoord] = useState('');
  const [passwordMsg, setPasswordMsg] = useState('');
  const [passwordLoading, setPasswordLoading] = useState(false);

  useEffect(() => {
    const stored = loadSettings();
    const defaults: Record<string, string | boolean | number> = {};
    SettingOptions.forEach((section) => {
      section.options.forEach((opt) => {
        if (opt.value !== undefined) {
          defaults[opt.id] = opt.value;
        }
      });
    });
    setSettings({ ...defaults, ...stored });
  }, []);

  useEffect(() => {
    if (user) {
      setAccountName(`${user.voornaam} ${user.naam}`);
      setAccountEmail(user.email);
    }
  }, [user]);

  const hasRole = (required?: string[]): boolean => {
    if (!required || required.length === 0) return true;
    return required.some((r) => r.toLowerCase() === currentUser);
  };

  const updateSetting = useCallback(
    (id: string, value: string | boolean | number) => {
      setSettings((prev) => {
        const next = { ...prev, [id]: value };
        saveSettings(next);
        applySettings(next);

        if (id === 'start-view') {
          document.cookie = `${START_VIEW_COOKIE}=${value}; path=/; max-age=${60 * 60 * 24 * 365}`;
        }

        return next;
      });
    },
    [],
  );

  const handleSaveAccount = async () => {
    if (!user) return;
    setSavingAccount(true);
    const parts = accountName.trim().split(' ');
    const voornaam = parts[0];
    const naam = parts.slice(1).join(' ') || voornaam;
    try {
      await fetch(`/api/werknemers/${user.id}`, {
        method: 'PUT',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          naam,
          voornaam,
          email: accountEmail,
          telefoonnummer: user.telefoonnummer,
          geboortedatum: user.geboortedatum,
          status: user.status,
        }),
      });
      setAccountSaved(true);
      setTimeout(() => setAccountSaved(false), 2500);
    } finally {
      setSavingAccount(false);
    }
  };

  const handleChangePassword = async () => {
    if (!user || !oudWachtwoord || !nieuwWachtwoord) return;
    setPasswordLoading(true);
    setPasswordMsg('');
    try {
      const res = await fetch('/api/werknemers/wachtwoord', {
        method: 'PUT',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          email: user.email,
          oudWachtwoord,
          nieuwWachtwoord,
        }),
      });
      if (res.ok) {
        setPasswordMsg('Wachtwoord succesvol gewijzigd.');
        setOudWachtwoord('');
        setNieuwWachtwoord('');
        setTimeout(() => {
          setShowPasswordForm(false);
          setPasswordMsg('');
        }, 2000);
      } else {
        const text = await res.text();
        setPasswordMsg(text || 'Fout bij wijzigen wachtwoord.');
      }
    } finally {
      setPasswordLoading(false);
    }
  };

  const handleAction = (action?: string) => {
    if (!action) return;
    if (action === 'change-password') {
      setShowPasswordForm((v) => !v);
      return;
    }
    if (action === 'manage-roles' || action === 'manage-teams') {
      router.push('/teams');
    }
  };

  const renderControl = (opt: (typeof SettingOptions)[0]['options'][0]) => {
    const value = settings[opt.id];

    if (opt.type === 'toggle') {
      return (
        <Toggle
          checked={Boolean(value ?? opt.value)}
          onChange={(v) => updateSetting(opt.id, v)}
        />
      );
    }

    if (opt.type === 'select' && opt.options) {
      return (
        <Select
          size="sm"
          options={opt.options}
          value={String(value ?? opt.value ?? '')}
          onChange={(v) => updateSetting(opt.id, v)}
        />
      );
    }

    if (opt.type === 'input') {
      const inputValue =
        opt.id === 'display-name'
          ? accountName
          : opt.id === 'email'
            ? accountEmail
            : String(value ?? '');

      const setter =
        opt.id === 'display-name'
          ? setAccountName
          : opt.id === 'email'
            ? setAccountEmail
            : (v: string) => updateSetting(opt.id, v);

      return (
        <Input
          type={'text'}
          value={inputValue}
          onChange={(e) => setter(e.target.value)}
        />
      );
    }

    if (opt.type === 'button') {
      return (
        <Button
          label={opt.label}
          variant="outline"
          size="sm"
          onClick={() => handleAction(opt.action)}
        />
      );
    }

    if (opt.type === 'link') {
      return <Link href={opt.action ?? '#'} label={opt.label} rounded="full" />;
    }

    return null;
  };

  return (
    <div className={'flex flex-col gap-10'}>
      {SettingOptions.filter((section) => hasRole(section.requiresRole)).map(
        (section, i) => {
          const visibleOptions = section.options.filter((opt) =>
            hasRole(opt.requiresRole),
          );

          if (visibleOptions.length === 0) return null;

          return (
            <PageSection key={i} title={section.section}>
              <div className="flex flex-col gap-4 items-center w-full px-3">
                {visibleOptions.map((opt, j) => (
                  <div key={opt.id ?? j} className="w-full flex flex-col gap-2">
                    <div className="w-full flex justify-between items-center gap-4">
                      <div className="flex flex-col gap-0.5 flex-1 min-w-0">
                        <span className="font-bold text-zinc-600 text-sm">
                          {opt.label}
                        </span>
                        {opt.description && (
                          <p className="text-xs text-zinc-400">
                            {opt.description}
                          </p>
                        )}
                      </div>
                      <div className="shrink-0">{renderControl(opt)}</div>
                    </div>

                    {opt.id === 'change-password' && showPasswordForm && (
                      <div className="flex flex-col gap-3 bg-zinc-50 rounded-2xl p-4 border border-zinc-200">
                        <Input
                          type="password"
                          placeholder="Huidig wachtwoord"
                          value={oudWachtwoord}
                          onChange={(e) => setOudWachtwoord(e.target.value)}
                        />
                        <Input
                          type="password"
                          placeholder="Nieuw wachtwoord"
                          value={nieuwWachtwoord}
                          onChange={(e) => setNieuwWachtwoord(e.target.value)}
                        />
                        {passwordMsg && (
                          <p
                            className={`text-sm ${passwordMsg.includes('succesvol') ? 'text-emerald-600' : 'text-rose-600'}`}
                          >
                            {passwordMsg}
                          </p>
                        )}
                        <Button
                          label="Wachtwoord opslaan"
                          variant="primary"
                          size="sm"
                          loading={passwordLoading}
                          onClick={handleChangePassword}
                        />
                      </div>
                    )}
                  </div>
                ))}

                {section.section === 'account' && (
                  <div className="w-full pt-1">
                    <Button
                      label={
                        accountSaved ? 'Opgeslagen!' : 'Accountgegevens opslaan'
                      }
                      variant={accountSaved ? 'approve' : 'primary'}
                      size="sm"
                      loading={savingAccount}
                      onClick={handleSaveAccount}
                    />
                  </div>
                )}
              </div>
            </PageSection>
          );
        },
      )}
    </div>
  );
};

export default SettingsClient;
