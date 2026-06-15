'use client';
import { Container } from '@/components/design-system/Container';
import { TaskList } from './TaskList';
import { TaskDetails } from './TaskDetails';

export const TakenOverview = () => {
  return (
    <div className="relative w-full flex flex-col gap-3 pt-5 min-h-96">
      <div className="w-full grid grid-cols-1 sm:grid-cols-2 gap-5 flex-1">
        <Container label="Taken" height="full" padding="0">
          <TaskList />
        </Container>

        <Container label="Details" height="full">
          <TaskDetails />
        </Container>
      </div>
    </div>
  );
};
