'use client';

import SettingOptions from '@/app/(pages)/(app)/instellingen/components/options';
import PageSection from '@/components/design system/PageSection/PageSection';
import { useUser } from '@/providers/UserProvider';

const SettingsClient = () => {
  const { user } = useUser();
  const userRole = user?.rol?.toLowerCase();

  const hasRole = (requiredRole?: string): boolean => {
    if (!requiredRole) return true; // geen restrictie → iedereen mag
    if (!userRole) return false; // user niet ingelogd of geen rol
    return userRole === requiredRole.toLowerCase();
  };

  return (
    <div className="w-1/2 flex flex-col gap-5 items-center">
      {SettingOptions.filter((section) => hasRole(section.requiresRole)).map(
        (section, i) => {
          const visibleOptions = section.options.filter((opt) =>
            hasRole(opt.requiresRole),
          );

          if (visibleOptions.length === 0) return null;

          return (
            <PageSection
              key={i}
              title={section.section}
              danger={section.section === 'gevarenzone'}
            >
              <div className="flex flex-col gap-3 items-center w-full px-3">
                {visibleOptions.map((opt, j) => (
                  <div
                    key={opt.id ?? j}
                    className={'w-full flex flex-col gap-3'}
                  >
                    <div className="w-full h-15 flex justify-between items-center">
                      <span className={'font-bold text-zinc-600 text-sm'}>
                        {opt.label}
                      </span>
                      {opt.description && (
                        <p className={'text-xs text-zinc-400'}>
                          {opt.description}*
                        </p>
                      )}
                    </div>
                    <div></div>
                  </div>
                ))}
              </div>
            </PageSection>
          );
        },
      )}
    </div>
  );
};

export default SettingsClient;
