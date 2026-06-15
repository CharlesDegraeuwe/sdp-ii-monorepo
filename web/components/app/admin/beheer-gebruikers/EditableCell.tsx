import { useState, useRef, useEffect } from 'react';

interface EditableCellProps {
  value: string;
  onSave: (next: string) => void;
  type?: 'text' | 'email' | 'date';
}

const EditableCell = ({ value, onSave, type = 'text' }: EditableCellProps) => {
  const [editing, setEditing] = useState(false);
  const [draft, setDraft] = useState(value);
  const ref = useRef<HTMLInputElement>(null);
  const saving = useRef(false);

  useEffect(() => {
    setDraft(value);
  }, [value]);

  useEffect(() => {
    if (editing) ref.current?.focus();
  }, [editing]);

  const commit = () => {
    if (saving.current) return;
    saving.current = true;
    setEditing(false);
    if (draft !== value) onSave(draft);
    saving.current = false;
  };

  if (!editing) {
    return (
      <span
        onClick={() => setEditing(true)}
        className="cursor-text px-2 py-1 rounded-md hover:bg-white/40 transition-colors block"
      >
        {value}
      </span>
    );
  }

  return (
    <input
      ref={ref}
      type={type}
      value={draft}
      onChange={(e) => setDraft(e.target.value)}
      onBlur={commit}
      onKeyDown={(e) => {
        if (e.key === 'Enter') commit();
        if (e.key === 'Escape') {
          setDraft(value);
          setEditing(false);
        }
      }}
      className="bg-white border w-full rounded-md px-2 py-1 outline-none"
    />
  );
};

export default EditableCell;
