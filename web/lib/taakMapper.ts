import type { Task } from '@/stores/taakStore';

interface BackendWerknemer {
  id: number;
  naam: string;
  voornaam: string;
  email: string;
}

interface BackendTaakDTO {
  id: number;
  werknemer?: BackendWerknemer;
  titel?: string;
  beschrijving?: string;
  afgewerkt?: string;
  deadline?: string;
  teamId?: number;
  siteId?: number;
}

export function mapBackendTask(
  dto: BackendTaakDTO | Record<string, unknown>,
): Task {
  const d = dto as BackendTaakDTO;
  return {
    id: String(d.id),
    name: d.titel ?? '',
    description: d.beschrijving ?? '',
    specifications: d.beschrijving ?? '',
    dueDate: d.deadline ?? '',
    location: d.siteId != null ? String(d.siteId) : '',
    important: false,
    finished: d.afgewerkt === 'ja',
    assigneeId: d.werknemer?.id != null ? String(d.werknemer.id) : undefined,
  };
}

export function mapTaskToBackend(task: Omit<Task, 'id' | 'finished'>) {
  const hasTime = task.dueDate?.includes('T');
  const deadline = hasTime ? task.dueDate.split('T')[0] : task.dueDate;
  const startuur = hasTime ? task.dueDate.split('T')[1]?.substring(0, 5) : null;

  return {
    ...(task.assigneeId ? { werknemerId: Number(task.assigneeId) } : {}),
    titel: task.name,
    beschrijving: task.description ?? task.specifications ?? '',
    deadline,
    ...(startuur ? { startuur } : {}),
  };
}
