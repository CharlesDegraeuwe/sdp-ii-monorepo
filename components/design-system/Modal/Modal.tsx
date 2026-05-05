import { ModalProps } from '@/components/design-system/Modal/Modal.types';

const Modal = (props: ModalProps) => {
  const { children } = props;
  return (
    <div
      className={
        'left-0 top-0 w-full z-90 flex flex-col backdrop-blur-2xl items-center justify-center h-full absolute bg-zinc-600/30'
      }
    >
      {children}
    </div>
  );
};

Modal.displayName = 'Modal';
export default Modal;
