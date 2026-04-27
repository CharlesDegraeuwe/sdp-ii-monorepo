export function StatusBadge({
  status,
  size = 'sm',
}: {
  status: string;
  size?: 'sm' | 'xs';
}) {
  const colors =
    status === 'Actief'
      ? 'bg-green-100 text-green-700 border-green-200'
      : status === 'In onderhoud'
        ? 'bg-orange-100 text-orange-700 border-orange-200'
        : 'bg-red-100 text-red-700 border-red-200';

  const sizeClasses =
    size === 'sm' ? 'px-3 py-1 text-xs' : 'px-2.5 py-1 text-[10px]';

  return (
    <span
      className={`${sizeClasses} rounded-full font-bold uppercase tracking-wide border ${colors}`}
    >
      {status}
    </span>
  );
}
