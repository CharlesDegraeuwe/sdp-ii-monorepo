import { ModalProps } from '@/components/design-system/Modal/Modal.types';

const Modal = ({ children, onClose }: ModalProps) => {
  return (
    <div
      className="fixed inset-0 z-[99999] flex flex-col items-center justify-center bg-zinc-600/50"
      onClick={onClose}
    >
      <div onClick={(e) => e.stopPropagation()}>{children}</div>
    </div>
  );
};

Modal.displayName = 'Modal';
export default Modal;
