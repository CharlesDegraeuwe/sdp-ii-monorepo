export function Card({
  children,
  className = '',
}: {
  children: React.ReactNode;
  className?: string;
}) {
  return (
    <div
      className={`border border-gray-300/30 backdrop-blur-2xl rounded-3xl bg-gray-300/30 shadow-sm hover:border-gray-400/30 transition-all duration-300 ${className}`}
    >
      {children}
    </div>
  );
}

export function SectionTitle({ children }: { children: React.ReactNode }) {
  return (
    <span className="text-xs font-bold text-zinc-500 px-1 uppercase tracking-wide">
      {children}
    </span>
  );
}
