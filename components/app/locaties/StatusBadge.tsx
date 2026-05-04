export function StatusBadge({
  status,
  size = 'sm',
}: {
  status: string;
  size?: 'sm' | 'xs';
}) {
  const colors =
    status === 'Actief'
      ? 'text-emerald-500 bg-emerald-300/30'
      : status === 'In onderhoud'
        ? 'text-orange-400 bg-orange-300/30'
        : 'text-rose-400 bg-rose-300/30';

  const sizeClasses =
    size === 'sm' ? 'px-3 py-1 text-xs' : 'px-2.5 py-1 text-[10px]';

  return (
    <span
      className={`${sizeClasses} rounded-full font-bold uppercase tracking-wide w-fit  ${colors}`}
    >
      {status}
    </span>
  );
}
