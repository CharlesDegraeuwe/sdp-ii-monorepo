'use client';

import { useEffect, useMemo } from 'react';
import { IoClose } from 'react-icons/io5';
import Image from 'next/image';

const mimeToExt: Record<string, string> = {
  'image/png': 'png',
  'image/jpeg': 'jpg',
  'image/gif': 'gif',
  'application/pdf': 'pdf',
  'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet': 'xlsx',
  'application/vnd.openxmlformats-officedocument.wordprocessingml.document':
    'docx',
};

interface FileItemProps {
  file: File;
  fileList: File[];
  setFileList: (fileList: File[]) => void;
}

const FileItem = ({ file, fileList, setFileList }: FileItemProps) => {
  const handleDelete = () => {
    setFileList(fileList.filter((f) => f !== file));
  };

  const previewUrl = useMemo(
    () => (file.type.startsWith('image/') ? URL.createObjectURL(file) : null),
    [file],
  );

  useEffect(() => {
    return () => {
      if (previewUrl) URL.revokeObjectURL(previewUrl);
    };
  }, [previewUrl]);

  const ext = fileExtension(file);

  return (
    <div className="relative drop-shadow-xs cursor-pointer min-h-12 group min-w-12 max-w-12 max-h-12 border bg-zinc-300 border-zinc-400/30 rounded-xl">
      {previewUrl ? (
        <Image src={previewUrl} alt={file.name} fill className="object-cover" />
      ) : (
        <div className="flex h-full w-full items-center justify-center text-[10px] font-semibold uppercase text-zinc-600">
          {ext ?? 'file'}
        </div>
      )}
      <div
        onClick={handleDelete}
        className="absolute w-4 h-4 rounded-full group-hover:opacity-100 opacity-0 transition-opacity duration-300 border-rose-400 border -right-2 text-white flex items-center justify-center text-xs cursor-pointer bg-rose-500 -top-2"
      >
        <IoClose size={10} />
      </div>
    </div>
  );
};

function fileExtension(file: File): string | null {
  if (file.type && mimeToExt[file.type]) {
    return mimeToExt[file.type];
  }
  const parts = file.name.split('.');
  return parts.length > 1 ? parts.pop()!.toLowerCase() : null;
}

FileItem.displayName = 'FileItem';
export default FileItem;
