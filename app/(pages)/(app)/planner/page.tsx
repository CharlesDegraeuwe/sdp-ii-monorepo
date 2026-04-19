import type { Metadata } from 'next';
import PlannerClient from './PlannerClient';

export const metadata: Metadata = {
  title: 'Planner | Delaware Suite',
};

export default function Page() {
  return <PlannerClient />;
}
