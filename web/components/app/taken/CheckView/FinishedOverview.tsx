'use client';
import { Container } from '@/components/design-system/Container';
import { FinishedTaskList } from './FinishedTaskList';
import { FinishedTaskDetails } from './FinishedTaskDetails';

export const FinishedOverview = () => {
  return (
    <div className="relative w-full flex flex-col gap-3 pt-5 min-h-96">
      <div className="w-full grid grid-cols-1 sm:grid-cols-2 gap-5 flex-1">
        <Container label="Afgewerkte taken" height="full" padding="0">
          <FinishedTaskList />
        </Container>

        <Container label="Details" height="full">
          <FinishedTaskDetails />
        </Container>
      </div>
    </div>
  );
};
