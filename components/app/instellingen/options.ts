type SettingOptionType =
  | 'toggle'
  | 'select'
  | 'input'
  | 'button'
  | 'link'
  | 'danger';

interface SettingOption {
  id: string;
  type: SettingOptionType;
  label: string;
  description?: string;
  icon?: string;
  value?: string | boolean | number;
  options?: { label: string; value: string }[];
  disabled?: boolean;
  requiresRole?: string[];
  action?: string;
}

interface SettingSection {
  section: string;
  icon?: string;
  description?: string;
  requiresRole?: string[];
  options: SettingOption[];
}

const SettingOptions: SettingSection[] = [
  {
    section: 'algemeen',
    icon: 'settings',
    description: 'Algemene voorkeuren',
    options: [
      {
        id: 'language',
        type: 'select',
        label: 'Taal',
        description: 'Interface taal',
        icon: 'globe',
        value: 'nl',
        options: [
          { label: 'Nederlands', value: 'nl' },
          { label: 'English', value: 'en' },
          { label: 'Français', value: 'fr' },
        ],
      },
      {
        id: 'theme',
        type: 'select',
        label: 'Thema',
        icon: 'palette',
        value: 'light',
        options: [
          { label: 'Licht', value: 'light' },
          { label: 'Donker', value: 'dark' },
          { label: 'Systeem', value: 'system' },
        ],
      },
      {
        id: 'date-format',
        type: 'select',
        label: 'Datumnotatie',
        description: 'Hoe datums worden weergegeven',
        icon: 'calendar',
        value: 'dd-mm-yyyy',
        options: [
          { label: 'DD-MM-YYYY', value: 'dd-mm-yyyy' },
          { label: 'MM/DD/YYYY', value: 'mm/dd/yyyy' },
          { label: 'YYYY-MM-DD', value: 'yyyy-mm-dd' },
        ],
      },
      {
        id: 'start-view',
        type: 'select',
        label: 'Startscherm',
        description: 'Welk scherm opent bij inloggen',
        icon: 'home',
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
    icon: 'user',
    description: 'Persoonlijke accountinstellingen',
    options: [
      {
        id: 'display-name',
        type: 'input',
        label: 'Weergavenaam',
        description: 'Hoe je naam verschijnt in de app',
        icon: 'at-sign',
        value: '',
      },
      {
        id: 'email',
        type: 'input',
        label: 'E-mailadres',
        description: 'Voor login en meldingen',
        icon: '(mail)',
        value: '',
      },
      {
        id: 'change-password',
        type: 'button',
        label: 'Wachtwoord wijzigen',
        icon: 'lock',
        action: 'change-password',
      },
      {
        id: 'session-timeout',
        type: 'select',
        label: 'Sessietimeout',
        description: 'Automatisch uitloggen na inactiviteit',
        icon: 'clock',
        value: '30',
        options: [
          { label: '15 minuten', value: '15' },
          { label: '30 minuten', value: '30' },
          { label: '1 uur', value: '60' },
          { label: 'Nooit', value: 'never' },
        ],
      },
    ],
  },
  {
    section: 'meldingen',
    icon: 'bell',
    description: 'Wanneer de app je informeert',
    options: [
      {
        id: 'notify-planning-changes',
        type: 'toggle',
        label: 'Wijzigingen in planning',
        description: 'Waarschuwen bij aanpassingen in mijn planning',
        icon: 'calendar-clock',
        value: true,
      },
      {
        id: 'notify-team-updates',
        type: 'toggle',
        label: 'Team updates',
        description: 'Meldingen over wijzigingen in mijn team',
        icon: 'users',
        value: true,
      },
      {
        id: 'notify-new-employee',
        type: 'toggle',
        label: 'Nieuwe werknemers',
        description: 'Bij toevoegen van werknemer aan mijn team',
        icon: 'user-plus',
        value: false,
        requiresRole: ['admin', 'manager'],
      },
      {
        id: 'notify-sound',
        type: 'toggle',
        label: 'Geluid bij meldingen',
        icon: 'volume-2',
        value: false,
      },
    ],
  },
  {
    section: 'weergave',
    icon: 'layout',
    description: 'Hoe data wordt getoond',
    options: [
      {
        id: 'employees-per-page',
        type: 'select',
        label: 'Werknemers per pagina',
        description: 'Paginering in overzichten',
        icon: 'list',
        value: '25',
        options: [
          { label: '10', value: '10' },
          { label: '25', value: '25' },
          { label: '50', value: '50' },
          { label: '100', value: '100' },
        ],
      },
      {
        id: 'default-sort',
        type: 'select',
        label: 'Standaard sortering',
        description: 'Hoe lijsten standaard gesorteerd zijn',
        icon: 'arrow-up-down',
        value: 'name-asc',
        options: [
          { label: 'Naam (A-Z)', value: 'name-asc' },
          { label: 'Naam (Z-A)', value: 'name-desc' },
          { label: 'Datum in dienst', value: 'hired-date' },
          { label: 'Rol', value: 'role' },
        ],
      },
      {
        id: 'show-avatars',
        type: 'toggle',
        label: "Profielfoto's tonen",
        description: "Toon foto's in werknemerslijsten",
        icon: 'image',
        value: true,
      },
      {
        id: 'compact-mode',
        type: 'toggle',
        label: 'Compacte weergave',
        description: 'Kleinere rijen voor meer data op het scherm',
        icon: 'minimize-2',
        value: false,
      },
    ],
  },
  {
    section: 'beheer',
    icon: 'shield-check',
    description: 'Admin-instellingen',
    requiresRole: ['admin', 'manager'],
    options: [
      {
        id: 'manage-roles',
        type: 'button',
        label: 'Rollen beheren',
        description: 'Beheer rollen en rechten',
        icon: 'key',
        action: 'manage-roles',
      },
      {
        id: 'manage-teams',
        type: 'button',
        label: 'Teams beheren',
        description: 'Teams toevoegen, wijzigen of verwijderen',
        icon: 'users',
        action: 'manage-teams',
      },
      {
        id: 'audit-log',
        type: 'link',
        label: 'Activiteitenlogboek',
        description: 'Bekijk recente wijzigingen in het systeem',
        icon: 'file-text',
        action: '/admin/audit',
      },
      {
        id: 'export-data',
        type: 'button',
        label: 'Data exporteren',
        description: 'Exporteer werknemers- en teamgegevens (CSV)',
        icon: 'download',
        action: 'export-data',
      },
    ],
  },
  {
    section: 'gevarenzone',
    icon: 'alert-triangle',
    description: 'Onomkeerbare acties — wees voorzichtig',
    requiresRole: ['admin', 'manager'],
    options: [
      {
        id: 'archive-inactive-employees',
        type: 'danger',
        label: 'Inactieve werknemers archiveren',
        description:
          'Archiveer alle werknemers die langer dan 6 maanden uit dienst zijn',
        icon: 'archive',
        action: 'archive-inactive',
      },
      {
        id: 'reset-planning',
        type: 'danger',
        label: 'Planning resetten',
        description: 'Verwijder alle planningen ouder dan dit kwartaal',
        icon: 'calendar-x',
        action: 'reset-planning',
      },
      {
        id: 'clear-audit-log',
        type: 'danger',
        label: 'Logboek wissen',
        description: 'Verwijder auditlogs ouder dan 1 jaar (niet aanbevolen)',
        icon: 'file-x',
        action: 'clear-audit-log',
      },
      {
        id: 'factory-reset',
        type: 'danger',
        label: 'Systeem resetten',
        description:
          'Verwijder alle werknemers, teams en planningen. Houdt accounts.',
        icon: 'rotate-ccw',
        action: 'factory-reset',
      },
    ],
  },
];

export default SettingOptions;
