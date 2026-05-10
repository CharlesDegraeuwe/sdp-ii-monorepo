'use client';
import { useState } from 'react';
import { Container } from '@/components/design-system/Container';
import { Button } from '@/components/design-system/Button';
import { useTeamsStore } from '@/stores/teamStore';
import EmployeeModal from '@/components/app/team/Modal/EmployeeModal';
import { IoIosAdd } from 'react-icons/io';
import { EmployeeList } from '@/components/app/team/UserCheckView/components/EmployeeList';
import { EmployeeDetails } from '@/components/app/team/UserCheckView/components/EmployeeDetails';

const UsersOverview = () => {
  const selectedWerknemerId = useTeamsStore((s) => s.selectedWerknemerId);
  const [showModal, setShowModal] = useState(false);

  return (
    <div className="relative w-full h-3/4 flex flex-col gap-3 pt-5">
      {showModal && <EmployeeModal onClose={() => setShowModal(false)} />}

      <div className="flex flex-row justify-end">
        <Button
          onClick={() => setShowModal(true)}
          className="flex flex-row items-center gap-1 text-xs text-zinc-500 hover:text-zinc-800 hover:bg-zinc-200/40 transition px-3 py-2 rounded-full"
          textSize="sm"
          iconLeft={<IoIosAdd className="w-5 h-5" />}
          label="Gebruiker toevoegen"
          variant="ghost"
        />
      </div>

      <div className="w-full grid grid-cols-2 gap-5 min-h-full">
        <Container label="Werknemers" height="full" padding="0">
          <EmployeeList />
        </Container>

        <Container label="Details" height="full">
          <EmployeeDetails werknemerId={selectedWerknemerId} />
        </Container>
      </div>
    </div>
  );
};

UsersOverview.displayName = 'UsersOverview';
export default UsersOverview;
