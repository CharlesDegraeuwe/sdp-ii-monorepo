import {
  badgeKleur,
  rijKleur,
  taakBadgeKleur,
} from '@/components/app/planner/utils';

const TeamDayView = () => {
  return (
    <div className="flex flex-col gap-3">
      {opDag.length === 0 && dagTaken.length === 0 && (
        <p className="text-sm text-zinc-400">Niets gepland op deze dag.</p>
      )}

      {dagTaken.map((t) => (
        <div
          key={`t-${t.id}`}
          className={`flex items-center justify-between px-4 py-3 rounded-2xl border ${
            t.belangrijk
              ? 'bg-rose-50/60 border-rose-100'
              : 'bg-blue-50/60 border-blue-100'
          }`}
        >
          <div className="flex flex-col gap-0.5">
            <span className="text-sm font-bold text-zinc-900">{t.naam}</span>
            {t.specificaties && (
              <span className="text-xs text-zinc-500">{t.specificaties}</span>
            )}
          </div>
          <div className="flex items-center gap-2">
            <span
              className={`text-xs font-bold px-2 py-1 rounded-full ${taakBadgeKleur(t)}`}
            >
              {t.belangrijk ? 'Belangrijk' : 'Taak'}
            </span>
            <span className="text-xs text-zinc-500">{t.locatie}</span>
          </div>
        </div>
      ))}

      {opDag.map((a, i) => (
        <div
          key={`a-${i}`}
          className={`flex items-center justify-between px-4 py-3 rounded-2xl border ${rijKleur(a)}`}
        >
          <div className="flex flex-col gap-0.5">
            <span className="text-sm font-bold text-zinc-900">
              {a.voornaam} {a.naam}
            </span>
            <span className="text-xs text-zinc-500">
              {new Date(a.startDatum).toLocaleDateString('nl-BE', {
                day: 'numeric',
                month: 'short',
              })}
              {a.startDatum !== a.eindDatum &&
                ` – ${new Date(a.eindDatum).toLocaleDateString('nl-BE', {
                  day: 'numeric',
                  month: 'short',
                })}`}
            </span>
          </div>

          <div className="flex items-center gap-2">
            <span
              className={`text-xs font-bold px-2 py-1 rounded-full ${badgeKleur(a)}`}
            >
              {a.type}
            </span>
            {a.status && (
              <span
                className={`text-xs font-bold px-2 py-1 rounded-full ${badgeKleur(a)}`}
              >
                {a.status}
              </span>
            )}
          </div>
        </div>
      ))}
    </div>
  );
};
