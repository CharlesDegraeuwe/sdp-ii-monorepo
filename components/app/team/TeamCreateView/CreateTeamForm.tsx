'use client';
import { FormHelper } from '@/components/design-system/Form';
import { Container } from '@/components/design-system/Container';
import { Input } from '@/components/design-system/Input';
import { Button } from '@/components/design-system/Button';
import { Label } from '@/components/design-system/Label';
import { useState, useMemo } from 'react';
import { useCreateTeam } from '@/hooks/useCreateTeam';
import { useTeamsStore } from '@/stores/teamStore';
import { IoIosAdd } from 'react-icons/io';
import EmployeeModal from '@/components/app/team/Modal/EmployeeModal';

interface Lid {
  werknemerId: number;
  isSupervisor: boolean;
}

interface CreateTeamFormProps {
  scope?: string;
}

const CreateTeamForm = ({ scope }: CreateTeamFormProps) => {
  const werknemers = useTeamsStore((s) => s.werknemers);
  const sites = useTeamsStore((s) => s.sites);
  const createTeam = useCreateTeam();

  const [naam, setNaam] = useState('');
  const [beschrijving, setBeschrijving] = useState('');
  const [managerId, setManagerId] = useState<number | ''>('');
  const [siteId, setSiteId] = useState<number | ''>('');
  const [search, setSearch] = useState('');
  const [leden, setLeden] = useState<Lid[]>([]);
  const [showModal, setShowModal] = useState(false);
  const [submitting, setSubmitting] = useState(false);

  const filtered = useMemo(() => {
    const list = Object.values(werknemers);
    if (!search) return list;
    const q = search.toLowerCase();
    return list.filter(
      (e) =>
        e.voornaam.toLowerCase().includes(q) ||
        e.naam.toLowerCase().includes(q),
    );
  }, [werknemers, search]);

  const isLid = (id: number) => leden.some((l) => l.werknemerId === id);
  const isSupervisor = (id: number) =>
    leden.find((l) => l.werknemerId === id)?.isSupervisor ?? false;

  const toggleLid = (id: number) => {
    setLeden((prev) =>
      isLid(id)
        ? prev.filter((l) => l.werknemerId !== id)
        : [...prev, { werknemerId: id, isSupervisor: false }],
    );
  };

  const toggleSupervisor = (id: number) => {
    setLeden((prev) =>
      prev.map((l) =>
        l.werknemerId === id ? { ...l, isSupervisor: !l.isSupervisor } : l,
      ),
    );
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!naam || managerId === '' || siteId === '') return;

    setSubmitting(true);
    try {
      await createTeam({
        naam,
        beschrijving,
        managerId: Number(managerId),
        siteId: Number(siteId),
        leden,
      });
      setNaam('');
      setBeschrijving('');
      setManagerId('');
      setSiteId('');
      setLeden([]);
    } finally {
      setSubmitting(false);
    }
  };

  const managers = Object.values(werknemers).filter(
    (w) => w.role === 'Manager',
  );

  return (
    <>
      <div className={'w-full flex flex-col gap-3 pt-5'}>
        <Container label={'Nieuw team'}>
          <FormHelper onSubmit={handleSubmit} noHeight>
            <div className={'w-full grid grid-cols-2 gap-6'}>
              <div className={'flex flex-col gap-4'}>
                <Label text={'Basis informatie'} size={'sm'} weight={600} />
                <Input
                  type={'text'}
                  placeholder={'Teamnaam'}
                  value={naam}
                  onChange={(e) => setNaam(e.target.value)}
                  errorOption={false}
                />
                <textarea
                  placeholder={'Beschrijving...'}
                  value={beschrijving}
                  onChange={(e) => setBeschrijving(e.target.value)}
                  className={
                    'w-full px-4 py-3 rounded-2xl bg-gray-300/30 border border-gray-300/30 focus:border-gray-700/30 text-sm min-h-24 outline-none shadow-inner'
                  }
                />

                <div className={'flex flex-col gap-1'}>
                  <select
                    value={managerId}
                    onChange={(e) =>
                      setManagerId(e.target.value ? Number(e.target.value) : '')
                    }
                    className={
                      'w-full rounded-full outline-none ring-0 border border-gray-300/30 focus:border-gray-700/30 px-5 py-3 bg-gray-300/30 shadow-inner'
                    }
                  >
                    <option value={''}>Selecteer manager...</option>
                    {managers.map((m) => (
                      <option key={m.id} value={m.id}>
                        {m.voornaam} {m.naam}
                      </option>
                    ))}
                  </select>
                </div>

                <div className={'flex flex-col gap-1'}>
                  <select
                    value={siteId}
                    onChange={(e) =>
                      setSiteId(e.target.value ? Number(e.target.value) : '')
                    }
                    className={
                      'w-full rounded-full outline-none ring-0 border border-gray-300/30 focus:border-gray-700/30 px-5 py-3 bg-gray-300/30 shadow-inner'
                    }
                  >
                    <option value={''}>Selecteer site...</option>
                    {Object.values(sites).map((s) => (
                      <option key={s.id} value={s.id}>
                        {s.naam}
                      </option>
                    ))}
                  </select>
                </div>

                <Button
                  variant={'primary'}
                  type={'submit'}
                  label={submitting ? 'Bezig...' : 'Team aanmaken'}
                  loading={submitting}
                  disabled={submitting}
                />
              </div>

              <div
                className={
                  'flex flex-col gap-3 border-l border-zinc-200/50 pl-6'
                }
              >
                <div className={'flex flex-row justify-between items-center'}>
                  <Label text={'Leden'} size={'sm'} weight={600} />
                  <button
                    type={'button'}
                    onClick={() => setShowModal(true)}
                    className={
                      'flex flex-row items-center gap-1 text-xs text-zinc-500 hover:text-zinc-800'
                    }
                  >
                    <IoIosAdd className={'w-4 h-4'} />
                    <span>Nieuwe werknemer</span>
                  </button>
                </div>

                <Input
                  type={'text'}
                  placeholder={'Zoek werknemers...'}
                  value={search}
                  onChange={(e) => setSearch(e.target.value)}
                  errorOption={false}
                />

                <div className={'flex flex-col gap-2 max-h-96 overflow-y-auto'}>
                  {filtered.map((w) => {
                    const checked = isLid(w.id);
                    return (
                      <div
                        key={w.id}
                        className={`flex flex-row items-center gap-2 px-3 py-2 rounded-full text-sm transition ${
                          checked ? 'bg-rose-50' : 'bg-zinc-100'
                        }`}
                      >
                        <input
                          type={'checkbox'}
                          checked={checked}
                          onChange={() => toggleLid(w.id)}
                        />
                        <span className={'flex-1'}>
                          {w.voornaam} {w.naam}
                        </span>
                        {checked && (
                          <label
                            className={
                              'text-xs flex items-center gap-1 text-zinc-500'
                            }
                          >
                            <input
                              type={'checkbox'}
                              checked={isSupervisor(w.id)}
                              onChange={() => toggleSupervisor(w.id)}
                            />
                            supervisor
                          </label>
                        )}
                      </div>
                    );
                  })}
                </div>
              </div>
            </div>
          </FormHelper>
        </Container>
      </div>

      {showModal && <EmployeeModal onClose={() => setShowModal(false)} />}
    </>
  );
};

CreateTeamForm.displayName = 'CreateTeamForm';
export default CreateTeamForm;
