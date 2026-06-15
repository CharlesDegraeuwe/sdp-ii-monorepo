'use client';
import { useState } from 'react';
import { Label } from '@/components/design-system/Label';
import { Input } from '@/components/design-system/Input';
import TextArea from '@/components/design-system/TextArea/TextArea';

interface ZiekteTabProps {
  reden: string;
  ziekteStart: string;
  ziekteEind: string;
  certificaat: File | null;
  setReden: (reden: string) => void;
  setZiekteStart: (ziekteStart: string) => void;
  setZiekteEind: (ziekteEind: string) => void;
  setCertificaat: (file: File) => void;
}

const ZiekteTab = (props: ZiekteTabProps) => {
  const {
    reden,
    ziekteStart,
    ziekteEind,
    certificaat,
    setReden,
    setZiekteStart,
    setZiekteEind,
    setCertificaat,
  } = props;
  const [isDragging, setIsDragging] = useState(false);

  return (
    <>
      <div className="flex flex-col sm:flex-row gap-4">
        <div className="flex flex-col gap-1 flex-1">
          <Label text={'Startdatum'} verplicht />

          <Input
            type="date"
            value={ziekteStart}
            onChange={(e) => setZiekteStart(e.target.value)}
          />
        </div>
        <div className="flex flex-col gap-1 flex-1">
          <Label text={'Einddatum'} verplicht />
          <Input
            type="date"
            value={ziekteEind}
            onChange={(e) => setZiekteEind(e.target.value)}
          />
        </div>
      </div>
      <div className="flex flex-col gap-1">
        <Label text={'Reden'} verplicht />
        <TextArea
          type="text"
          value={reden}
          onChange={(e) => setReden(e.target.value)}
          placeholder="Reden voor afwezigheid..."
        />
      </div>
      <div className="flex flex-col gap-1">
        <Label text={'Ziektebriefje'} />
        <div
          onDragOver={(e) => {
            e.preventDefault();
            setIsDragging(true);
          }}
          onDragLeave={() => setIsDragging(false)}
          onDrop={(e) => {
            e.preventDefault();
            setIsDragging(false);
            const f = e.dataTransfer.files[0];
            if (f) setCertificaat(f);
          }}
          onClick={() => document.getElementById('fileInput')?.click()}
          className={`border-2 border-dashed rounded-2xl p-8 flex flex-col items-center min-h-36 justify-center gap-2 cursor-pointer transition-all duration-300 ${isDragging ? 'border-zinc-900 bg-zinc-100' : 'border-gray-300/50 bg-gray-300/10 hover:border-gray-400/50'}`}
        >
          <svg
            xmlns="http://www.w3.org/2000/svg"
            className="w-6 h-6 text-zinc-800"
            fill="none"
            viewBox="0 0 24 24"
            stroke="currentColor"
            strokeWidth={2}
          >
            <path
              strokeLinecap="round"
              strokeLinejoin="round"
              d="M4 16v2a2 2 0 002 2h12a2 2 0 002-2v-2M12 12V4m0 0l-3 3m3-3l3 3"
            />
          </svg>
          <span className="text-sm text-zinc-400">
            {certificaat
              ? certificaat.name
              : 'Sleep bestand hierheen of klik om te bladeren'}
          </span>
          <input
            id="fileInput"
            type="file"
            accept=".pdf,.png,.jpg,.jpeg"
            className="hidden"
            onChange={(e) => {
              if (e.target.files?.[0]) setCertificaat(e.target.files[0]);
            }}
          />
        </div>
      </div>
    </>
  );
};

ZiekteTab.displayName = 'ZiekteTab';
export default ZiekteTab;
