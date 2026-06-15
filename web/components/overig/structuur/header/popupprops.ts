export interface PopupProps extends React.HTMLAttributes<HTMLDivElement> {
  popupRef: React.RefObject<HTMLDivElement | null>;
  isOpen: boolean;
}
