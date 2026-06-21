import type React from 'react';

export interface IFilterOption {
  value: string;
  label: string | React.ReactNode;
  icon?: React.ReactNode;
}
