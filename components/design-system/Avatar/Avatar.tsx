interface AvatarProps {
  img_url?: string;
  displayName: string;
  id: number;
  size?: number;
}

const gradients = [
  'bg-rose-500',
  'bg-violet-500',
  'bg-blue-500',
  'bg-cyan-500',
  'bg-emerald-500',
  'bg-lime-500',
  'bg-amber-500',
  'bg-orange-500',
  'bg-indigo-500',
  'bg-purple-500',
];

function hashId(id: string): number {
  let hash = 0;
  for (let i = 0; i < id.length; i++) {
    hash = (hash << 5) - hash + id.charCodeAt(i);
    hash |= 0;
  }
  return Math.abs(hash);
}

const Avatar = ({ img_url, displayName, id, size = 36 }: AvatarProps) => {
  const fontSize = size * 0.38;

  if (img_url) {
    return (
      <div className="flex flex-row items-center gap-2">
        <img
          src={img_url}
          width={size}
          height={size}
          title={displayName}
          className="rounded-full object-cover"
          style={{ width: size, height: size, minWidth: size, minHeight: size }}
          alt={displayName}
        />
      </div>
    );
  }

  const initials = displayName
    .split(' ')
    .map((word) => word[0])
    .join('');
  const gradient = gradients[hashId(id.toString()) % gradients.length];

  return (
    <div
      className={`rounded-full items-center text-white justify-center flex ${gradient}`}
      style={{ width: size, height: size, minWidth: size, minHeight: size }}
    >
      <span className="font-bold uppercase" style={{ fontSize }}>
        {initials}
      </span>
    </div>
  );
};

Avatar.displayName = 'Avatar';
export default Avatar;
