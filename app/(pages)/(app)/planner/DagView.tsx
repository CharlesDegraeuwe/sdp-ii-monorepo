import { Fragment } from 'react';
import { Container } from '@/components/design system/Container';
import { HOURS } from './constants';

export function DagView() {
  return (
    <Container className="h-full">
      <div className="h-full overflow-auto">
        <div className="grid grid-cols-[48px_1fr]">
          {HOURS.map((hour) => (
            <Fragment key={hour}>
              <div className="text-xs text-gray-400 pr-2 text-right py-2 border-t border-gray-200/30">
                {String(hour).padStart(2, '0')}:00
              </div>
              <div className="border-t border-gray-200/30 h-10" />
            </Fragment>
          ))}
        </div>
      </div>
    </Container>
  );
}
