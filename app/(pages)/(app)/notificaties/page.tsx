export default function Page() {
  return <div></div>;
}

// ── Notificatie rij ───────────────────────────────────────────────────────────
interface RijProps {
  n: Notificatie;
  verlofStatus: string | undefined;
  bezig: boolean;
  onGelezen: () => void;
  onVerwijder: () => void;
  onGoedkeuren: () => void;
  onAfwijzen: () => void;
  onAnnuleer: () => void;
  formatDatum: (d: string) => string;
}

function NotificatieRij({
  n,
  verlofStatus,
  bezig,
  onGelezen,
  onVerwijder,
  onGoedkeuren,
  onAfwijzen,
  onAnnuleer,
  formatDatum,
}: RijProps) {
  const isOngelezen = n.gelezen === 'Nee';

  return (
    <div
      className={`flex items-start gap-4 px-5 py-4 rounded-4xl border transition-all duration-300 bg-gray-300/20 hover:border-gray-400/30 ${isOngelezen ? 'border-gray-300/40 shadow-sm' : 'border-gray-300/20'}`}
    >
      {/* Dot */}
      <div
        className={`mt-1.5 w-2 h-2 rounded-full flex-shrink-0 ${isOngelezen ? 'bg-red-400' : 'bg-emerald-400'}`}
      />

      {/* Inhoud */}
      <div className="flex-1 min-w-0">
        <div className="flex items-start justify-between gap-2">
          <span className="text-sm font-bold text-zinc-900">{n.titel}</span>
          <span className="text-xs text-zinc-400 flex-shrink-0">
            {formatDatum(n.datum)}
          </span>
        </div>
        <p className="text-sm text-zinc-500 mt-0.5">{n.bericht}</p>

        {/* Acties */}
        <div className="flex items-center gap-2 mt-3 flex-wrap">
          {n.titel === 'Nieuwe verlofaanvraag' &&
            n.referentieId &&
            (verlofStatus === 'In afwachting' ? (
              <>
                <button
                  onClick={onGoedkeuren}
                  disabled={bezig}
                  className="px-3 py-1.5 rounded-full bg-emerald-500 text-white text-xs font-bold hover:bg-emerald-600 active:scale-95 transition-all duration-200 disabled:opacity-50"
                >
                  Goedkeuren
                </button>
                <button
                  onClick={onAfwijzen}
                  disabled={bezig}
                  className="px-3 py-1.5 rounded-full bg-red-500 text-white text-xs font-bold hover:bg-red-600 active:scale-95 transition-all duration-200 disabled:opacity-50"
                >
                  Afwijzen
                </button>
              </>
            ) : verlofStatus ? (
              <span
                className={`text-xs font-bold px-3 py-1 rounded-full ${verlofStatus === 'Goedgekeurd' ? 'bg-emerald-50 text-emerald-600' : verlofStatus === 'Afgewezen' ? 'bg-red-50 text-red-500' : 'bg-zinc-100 text-zinc-500'}`}
              >
                {verlofStatus === 'Goedgekeurd'
                  ? '✓ Goedgekeurd'
                  : verlofStatus === 'Afgewezen'
                    ? '✗ Afgewezen'
                    : verlofStatus}
              </span>
            ) : null)}

          {n.titel === 'Verlof goedgekeurd' &&
            n.referentieId &&
            (verlofStatus === 'Goedgekeurd' ? (
              <button
                onClick={onAnnuleer}
                disabled={bezig}
                className="px-3 py-1.5 rounded-full bg-red-500 text-white text-xs font-bold hover:bg-red-600 active:scale-95 transition-all duration-200 disabled:opacity-50"
              >
                Annuleren
              </button>
            ) : verlofStatus === 'Geannuleerd' ? (
              <span className="text-xs font-bold px-3 py-1 rounded-full bg-zinc-100 text-zinc-500">
                ✗ Geannuleerd
              </span>
            ) : null)}

          {isOngelezen && (
            <button
              onClick={onGelezen}
              className="px-3 py-1.5 rounded-full bg-gray-300/40 text-zinc-600 text-xs font-bold hover:bg-gray-300/60 active:scale-95 transition-all duration-200"
            >
              ✓
            </button>
          )}

          <button
            onClick={onVerwijder}
            className="px-3 py-1.5 rounded-full bg-gray-300/40 text-zinc-600 text-xs font-bold hover:bg-gray-300/60 active:scale-95 transition-all duration-200"
          >
            ✕
          </button>
        </div>
      </div>
    </div>
  );
}
