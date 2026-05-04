'use client';
import { Container } from '@/components/design system/Container';
import { Input } from '@/components/design system/Input';
import { Button } from '@/components/design system/Button';
import { Label } from '@/components/design system/Label';
import { useState, useMemo } from 'react';
import { useTeamsStore } from '@/stores/teamStore';
import EmployeeModal from '@/components/app/team/Modal/EmployeeModal';
import { FaRegUserCircle } from 'react-icons/fa';
import { IoIosAdd } from 'react-icons/io';

const UsersOverview = () => {
  const werknemers = useTeamsStore((s) => s.werknemers);
  const teams = useTeamsStore((s) => s.teams);
  const selectedWerknemerId = useTeamsStore((s) => s.selectedWerknemerId);
  const selectWerknemer = useTeamsStore((s) => s.selectWerknemer);
  const updateWerknemer = useTeamsStore((s) => s.updateWerknemer);

  const [search, setSearch] = useState('');
  const [showModal, setShowModal] = useState(false);

  const filtered = useMemo(() => {
    const list = Object.values(werknemers);
    if (!search) return list;
    const q = search.toLowerCase();
    return list.filter(
      (e) =>
        e.voornaam.toLowerCase().includes(q) ||
        e.naam.toLowerCase().includes(q) ||
        e.email.toLowerCase().includes(q),
    );
  }, [werknemers, search]);

  const employee = selectedWerknemerId ? werknemers[selectedWerknemerId] : null;
  const team = employee?.siteId
    ? Object.values(teams).find((t) => t.siteId === employee.siteId)
    : null;

  return (
    <div className={'w-full h-full'}>
      <div className={'w-full h-3/4 flex flex-col gap-3 pt-5'}>
        <div className={'flex flex-row justify-end max-h-5'}>
          <button
            onClick={() => setShowModal(true)}
            className={
              'flex flex-row cursor-pointer hover:bg-zinc-300/30 transition-all duration-300 px-2 py-3 rounded-full items-center gap-1 text-xs text-zinc-500 hover:text-zinc-800'
            }
          >
            <IoIosAdd className={'w-5 h-5'} />
            <span>Gebruiker toevoegen</span>
          </button>
        </div>

        <div className={'w-full grid grid-cols-2 gap-5 min-h-full'}>
          <Container label={'Werknemers'} height={'full'}>
            <Input
              type={'text'}
              placeholder={'Zoek werknemers...'}
              value={search}
              onChange={(e) => setSearch(e.target.value)}
              errorOption={false}
            />
            <div className={'flex flex-col gap-2 max-h-96 overflow-y-auto'}>
              {filtered.length === 0 && (
                <div className={'w-full py-8 flex items-center justify-center'}>
                  <Label
                    text={'Geen werknemers gevonden'}
                    variant={'emptystate'}
                  />
                </div>
              )}
              {filtered.map((e) => {
                const active = selectedWerknemerId === e.id;
                return (
                  <button
                    key={e.id}
                    onClick={() => selectWerknemer(e.id)}
                    className={`px-4 py-2 rounded-full text-sm text-left transition ${
                      active
                        ? 'bg-white ring-2 ring-rose-500'
                        : 'bg-zinc-100 hover:bg-zinc-200'
                    }`}
                  >
                    {e.voornaam} {e.naam}
                  </button>
                );
              })}
            </div>
          </Container>

          <Container label={'Details'} height={'full'}>
            {!employee ? (
              <div
                className={
                  'w-full h-full flex items-center justify-center pb-12'
                }
              >
                <Label
                  text={'Selecteer een gebruiker'}
                  variant={'emptystate'}
                />
              </div>
            ) : (
              <div className={'flex flex-col gap-4'}>
                <div className={'flex flex-row justify-between items-start'}>
                  <div>
                    <p className={'font-bold text-lg'}>
                      {employee.voornaam} {employee.naam}
                    </p>
                    <p className={'text-xs text-zinc-500'}>#{employee.id}</p>
                    <p className={'text-sm mt-2'}>{employee.email}</p>
                    <p className={'text-sm'}>{employee.telefoon}</p>
                  </div>
                  <FaRegUserCircle className={'w-12 h-12 text-zinc-300'} />
                </div>

                <div
                  className={
                    'border-t border-zinc-200 pt-4 flex flex-col gap-2 text-sm'
                  }
                >
                  <div className={'flex justify-between'}>
                    <span className={'text-zinc-600'}>{employee.siteNaam}</span>
                    <span
                      className={
                        employee.status === 'Actief'
                          ? 'text-emerald-500'
                          : 'text-red-500'
                      }
                    >
                      {employee.status === 'Actief'
                        ? '● Actief'
                        : '● ' + employee.status}
                    </span>
                  </div>
                  {team && (
                    <div className={'flex justify-between'}>
                      <span className={'text-zinc-600'}>
                        {team.naam}
                        {employee.role !== 'employee' && ` — ${employee.role}`}
                      </span>
                    </div>
                  )}
                </div>

                <div className={'flex flex-row gap-2 mt-2'}>
                  <Button
                    variant={'primary'}
                    label={'Demoot'}
                    textSize={'sm'}
                    onClick={() =>
                      updateWerknemer(employee.id, { role: 'employee' })
                    }
                  />
                  <Button
                    variant={'outline'}
                    label={'Blokkeer'}
                    textSize={'sm'}
                    onClick={() =>
                      updateWerknemer(employee.id, { status: 'Geblokkeerd' })
                    }
                  />
                </div>
              </div>
            )}
          </Container>
        </div>
      </div>

      {showModal && <EmployeeModal onClose={() => setShowModal(false)} />}
    </div>
  );
};

UsersOverview.displayName = 'UsersOverview';
export default UsersOverview;
