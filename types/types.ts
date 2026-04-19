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
