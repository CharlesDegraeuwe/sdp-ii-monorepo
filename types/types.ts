export interface WerknemerUser {
  id: string;
  email: string;
  naam: string;
  voornaam: string;
  telefoonnummer: string;
  geboortedatum: string;
  rol: string;
  status: string;
  accessToken: string;
}

export interface Notificatie {
  id: string;
  werknemer: WerknemerUser;
  titel: string;
  bericht: string;
  gelezen: string;
  datum: string;
  referentieId: string;
}

export interface Machine {
  id: number;
  naam: string;
  status: string;
  site?: { id: number };
}

export interface Team {
  id: number;
  naam: string;
  beschrijving: string;
  managerId: number;
  managerNaam: string;
  siteId: number;
  siteNaam: string;
}

export interface Site {
  id: number;
  naam: string;
  locatie: string;
  capaciteit: number;
  status: string;
  machines?: Machine[];
  teams?: Team[];
}

export interface CustomSession {
  user?: { token?: string; id?: number };
  accessToken?: string;
}

export interface SiteTeamResponse {
  site?: { id: number };
  team: Team;
}
