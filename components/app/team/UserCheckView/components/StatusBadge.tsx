type Props = {
  status: string;
};

export const StatusBadge = ({ status }: Props) => {
  const isActive = status === 'Actief';
  const color = isActive ? 'emerald' : 'rose';

  return (
    <span className={`flex items-center gap-1.5 text-xs text-${color}-500`}>
      <div
        className={`w-1.5 h-1.5 animate-pulse rounded-full bg-${color}-500`}
      />
      {isActive ? 'actief' : status.toLowerCase()}
    </span>
  );
};
