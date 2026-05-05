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
import { HiOutlineLocationMarker } from 'react-icons/hi';
import { HiOutlineUserGroup } from 'react-icons/hi2';

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

  const isActive = employee?.status === 'Actief';

  return (
    <div className={'relative w-full h-3/4 flex flex-col gap-3 pt-5'}>
      {showModal && <EmployeeModal onClose={() => setShowModal(false)} />}

      <div className={'flex flex-row justify-end'}>
        <button
          onClick={() => setShowModal(true)}
          className={
            'flex flex-row items-center gap-1 text-xs text-zinc-500 hover:text-zinc-800 hover:bg-zinc-200/40 transition px-3 py-2 rounded-full'
          }
        >
          <IoIosAdd className={'w-5 h-5'} />
          <span>Gebruiker toevoegen</span>
        </button>
      </div>

      <div className={'w-full grid grid-cols-2 gap-5 min-h-full'}>
        <Container label={'Werknemers'} height={'full'}>
          <div className={'flex flex-col gap-3'}>
            <Input
              type={'text'}
              placeholder={'Zoek werknemers...'}
              value={search}
              onChange={(e) => setSearch(e.target.value)}
              errorOption={false}
            />
            <div
              className={'flex flex-col gap-2 max-h-96 overflow-y-auto pr-1'}
            >
              {filtered.length === 0 ? (
                <div className={'w-full py-8 flex items-center justify-center'}>
                  <Label
                    text={'Geen werknemers gevonden'}
                    variant={'emptystate'}
                  />
                </div>
              ) : (
                filtered.map((e) => {
                  const active = selectedWerknemerId === e.id;
                  return (
                    <button
                      key={e.id}
                      onClick={() => selectWerknemer(e.id)}
                      className={`px-4 py-2 rounded-full text-sm text-left transition ${
                        active
                          ? 'bg-white ring-2 ring-rose-500 shadow-sm'
                          : 'bg-zinc-100 hover:bg-zinc-200'
                      }`}
                    >
                      {e.voornaam} {e.naam}
                    </button>
                  );
                })
              )}
            </div>
          </div>
        </Container>

        <Container label={'Details'} height={'full'}>
          {!employee ? (
            <div
              className={'w-full h-full flex items-center justify-center pb-12'}
            >
              <Label text={'Selecteer een gebruiker'} variant={'emptystate'} />
            </div>
          ) : (
            <div className={'flex flex-col gap-5 h-full'}>
              <div
                className={'flex flex-row justify-between items-start gap-4'}
              >
                <div className={'flex flex-col min-w-0'}>
                  <p className={'font-bold text-lg leading-tight truncate'}>
                    {employee.voornaam} {employee.naam}
                  </p>
                  <p className={'text-xs text-zinc-400 mb-3'}>
                    user{employee.id}
                  </p>
                  <p className={'text-sm text-zinc-700 truncate'}>
                    {employee.email}
                  </p>
                  <p className={'text-sm text-zinc-700'}>{employee.telefoon}</p>
                </div>
                <FaRegUserCircle
                  className={'w-14 h-14 text-zinc-300 shrink-0'}
                />
              </div>

              <div
                className={
                  'border-t border-zinc-200/70 pt-4 flex flex-col gap-3 text-sm'
                }
              >
                <div className={'flex justify-between items-center'}>
                  <span className={'flex items-center gap-2 text-zinc-600'}>
                    <HiOutlineLocationMarker className={'w-4 h-4'} />
                    {employee.siteNaam || '—'}
                  </span>
                  <span
                    className={`flex items-center gap-1.5 text-xs ${
                      isActive ? 'text-emerald-500' : 'text-rose-500'
                    }`}
                  >
                    <span
                      className={`w-1.5 h-1.5 rounded-full ${
                        isActive ? 'bg-emerald-500' : 'bg-rose-500'
                      }`}
                    />
                    {isActive ? 'actief' : employee.status.toLowerCase()}
                  </span>
                </div>

                {team && (
                  <div className={'flex justify-between items-center'}>
                    <span className={'flex items-center gap-2 text-zinc-600'}>
                      <HiOutlineUserGroup className={'w-4 h-4'} />
                      {team.naam}
                      {employee.role !== 'employee' && (
                        <span className={'text-zinc-400'}>
                          — {employee.role}
                        </span>
                      )}
                    </span>
                    <button
                      className={
                        'text-xs underline text-zinc-500 hover:text-zinc-800'
                      }
                    >
                      zie planning
                    </button>
                  </div>
                )}
              </div>

              <div className={'flex flex-col gap-2 mt-auto'}>
                <Button
                  variant={'primary'}
                  label={'Demoot'}
                  onClick={() =>
                    updateWerknemer(employee.id, { role: 'employee' })
                  }
                />
                <Button
                  variant={'outline'}
                  label={'Blokkeer werknemer'}
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
  );
};

UsersOverview.displayName = 'UsersOverview';
export default UsersOverview;
