type SettingOptionType = 'toggle' | 'select' | 'input' | 'button' | 'link';

interface SettingOption {
  id: string;
  type: SettingOptionType;
  label: string;
  description?: string;
  value?: string | boolean | number;
  options?: { label: string; value: string }[];
  requiresRole?: string[];
  action?: string;
}

interface SettingSection {
  section: string;
  description?: string;
  requiresRole?: string[];
  options: SettingOption[];
}

const SettingOptions: SettingSection[] = [
  {
    section: 'algemeen',
    description: 'Algemene voorkeuren',
    options: [
      {
        id: 'theme',
        type: 'select',
        label: 'Thema',
        value: 'light',
        options: [
          { label: 'Licht', value: 'light' },
          { label: 'Donker', value: 'dark' },
          { label: 'Systeem', value: 'system' },
        ],
      },
      {
        id: 'start-view',
        type: 'select',
        label: 'Startscherm',
        description: 'Welk scherm opent bij inloggen',
        value: 'dashboard',
        options: [
          { label: 'Dashboard', value: 'dashboard' },
          { label: 'Werknemers', value: 'employees' },
          { label: 'Planning', value: 'planning' },
          { label: 'Teams', value: 'teams' },
        ],
      },
    ],
  },
  {
    section: 'account',
    description: 'Persoonlijke accountinstellingen',
    options: [
      {
        id: 'display-name',
        type: 'input',
        label: 'Weergavenaam',
        description: 'Hoe je naam verschijnt in de app',
        value: '',
      },
      {
        id: 'email',
        type: 'input',
        label: 'E-mailadres',
        value: '',
      },
      {
        id: 'change-password',
        type: 'button',
        label: 'Wachtwoord wijzigen',
        action: 'change-password',
      },
    ],
  },
  {
    section: 'meldingen',
    description: 'Wanneer de app je informeert',
    options: [
      {
        id: 'notify-sound',
        type: 'toggle',
        label: 'Geluid bij meldingen',
        value: false,
      },
    ],
  },
  {
    section: 'weergave',
    description: 'Hoe de app wordt getoond',
    options: [
      {
        id: 'compact-mode',
        type: 'toggle',
        label: 'Compacte weergave',
        description: 'Kleinere rijen en minder witruimte',
        value: false,
      },
    ],
  },
  {
    section: 'beheer',
    description: 'Admin-instellingen',
    requiresRole: ['admin', 'manager'],
    options: [
      {
        id: 'manage-roles',
        type: 'button',
        label: 'Rollen beheren',
        description: 'Beheer rollen en rechten',
        action: 'manage-roles',
      },
      {
        id: 'manage-teams',
        type: 'button',
        label: 'Teams beheren',
        description: 'Teams toevoegen, wijzigen of verwijderen',
        action: 'manage-teams',
      },
      {
        id: 'audit-log',
        type: 'link',
        label: 'Activiteitenlogboek',
        description: 'Bekijk recente wijzigingen in het systeem',
        action: '/admin/audit',
      },
    ],
  },
];

export default SettingOptions;
