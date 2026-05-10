'use client';
import React, { ChangeEvent, useCallback, useEffect, useState } from 'react';
import { Button } from '@/components/design-system/Button';
import { FaPlus } from 'react-icons/fa6';
import Modal from '@/components/design-system/Modal/Modal';
import Image from 'next/image';

import { LuSend } from 'react-icons/lu';
import FileItem from '@/components/app/chat/FileItem';

type ChatInputProps = Omit<
  React.TextareaHTMLAttributes<HTMLTextAreaElement>,
  'onChange'
> & {
  autoFocus: boolean;
  onFileSelect?: (files: FileList | null) => void;
  onValueChange?: (value: string) => void;
  onSubmit?: () => void;
  textareaRef?: React.RefObject<HTMLTextAreaElement | null>;
  files: File[];
  setFiles: React.Dispatch<React.SetStateAction<File[]>>;
  isReceiving: boolean;
};

const ChatInput = ({
  id,
  onFileSelect,
  onValueChange,
  onSubmit,
  textareaRef,
  autoFocus,
  files,
  setFiles,
  isReceiving,
  ...props
}: ChatInputProps) => {
  const fileInputRef = React.useRef<HTMLInputElement>(null);

  const [isUploaded] = useState<boolean>(true);
  const [maxLength, setMaxLength] = React.useState(false);

  const handleFiles = useCallback(
    (fileList: FileList | null) => {
      if (!fileList) return;
      setFiles((prev) => {
        const newFiles = [...prev, ...Array.from(fileList)];

        if (newFiles.length === 3) {
          setMaxLength((prev) => !prev);
        }

        return newFiles;
      });
    },
    [setFiles],
  );
  const [isDragging, setIsDragging] = React.useState(false);
  const dragCounter = React.useRef(0);

  useEffect(() => {
    const handleDragEnter = (e: DragEvent) => {
      e.preventDefault();
      if (e.dataTransfer?.types.includes('Files')) {
        dragCounter.current++;
        setIsDragging(true);
      }
    };
    const handleDragLeave = (e: DragEvent) => {
      e.preventDefault();
      dragCounter.current--;
      if (dragCounter.current === 0) setIsDragging(false);
    };
    const handleDragOver = (e: DragEvent) => e.preventDefault();
    const handleDrop = (e: DragEvent) => {
      e.preventDefault();
      dragCounter.current = 0;
      setIsDragging(false);
      if (e.dataTransfer?.files) {
        if (!maxLength) {
          handleFiles(e.dataTransfer.files);
        }
      }
    };

    window.addEventListener('dragenter', handleDragEnter);
    window.addEventListener('dragleave', handleDragLeave);
    window.addEventListener('dragover', handleDragOver);
    window.addEventListener('drop', handleDrop);
    return () => {
      window.removeEventListener('dragenter', handleDragEnter);
      window.removeEventListener('dragleave', handleDragLeave);
      window.removeEventListener('dragover', handleDragOver);
      window.removeEventListener('drop', handleDrop);
    };
  }, [onFileSelect, handleFiles, maxLength]);

  const handleKeyDown = (e: React.KeyboardEvent<HTMLTextAreaElement>) => {
    if (e.key === 'Enter' && !e.shiftKey) {
      e.preventDefault();
      handleSending();
    }
  };

  const handleChange = (e: ChangeEvent<HTMLTextAreaElement>) => {
    onValueChange?.(e.currentTarget.value);
  };

  const handleSending = () => {
    if (isUploaded) {
      onSubmit?.();
    }
  };
  return (
    <>
      {isDragging && (
        <Modal>
          {maxLength ? (
            <>
              <span className="text-rose-700 mt-3 text-lg">
                Je kan maximaal 3 bestanden uploaden
              </span>
            </>
          ) : (
            <>
              <Image
                src="/file.svg"
                alt="file"
                width={20}
                height={20}
                className="w-20 h-20 invert brightness-0"
              />
              <span className="text-white mt-3 text-lg">
                Sleep je bestanden hierheen
              </span>
            </>
          )}
        </Modal>
      )}
      <div className="relative flex flex-col w-full items-end gap-2 overflow-hidden rounded-3xl border border-gray-300/30 bg-gray-300/30 p-5 pb-0 shadow-inner focus-within:border-gray-700/30">
        <textarea
          autoFocus={autoFocus}
          id={id}
          ref={textareaRef}
          autoComplete="off"
          rows={3}
          className="flex-1 resize-none max-h-13 scroll-hidden w-full bg-transparent outline-none"
          onKeyDown={handleKeyDown}
          onChange={handleChange}
          {...props}
        />
        <div
          className={'w-full h-15 flex flex-row gap-2 overflow-visible pb-2'}
        >
          {files.map((file, index) => (
            <FileItem
              key={index}
              file={file}
              fileList={files}
              setFileList={setFiles}
            />
          ))}
        </div>
        <input
          ref={fileInputRef}
          type="file"
          className="hidden"
          onChange={(e) => {
            if (!maxLength) {
              onFileSelect?.(e.target.files);
              handleFiles(e.target.files);
            }
          }}
        />
        <div className={'absolute z-20 right-0 top-4 flex flex-row px-3'}>
          <Button
            icon={<FaPlus />}
            variant="ghost"
            onClick={() => fileInputRef.current?.click()}
          />
          <Button
            disabled={!isUploaded || isReceiving}
            px={'px-0'}
            icon={<LuSend size={14} />}
            variant={'submit'}
            onClick={handleSending}
          />
        </div>
      </div>
    </>
  );
};

ChatInput.displayName = 'ChatInput';
export default ChatInput;
