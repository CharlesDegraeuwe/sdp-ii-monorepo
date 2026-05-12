export interface SelectOption {
  value: string;
  label: string;
}

export type SelectProps = {
  options: SelectOption[];
  value?: string | number;
  onChange?: (value: string | number) => void;
  placeholder?: string;
  label?: string;
  error?: string;
  errorOption?: boolean;
  id?: string;
  disabled?: boolean;
  size?: 'sm' | 'md' | 'lg' | 'xl';
};
