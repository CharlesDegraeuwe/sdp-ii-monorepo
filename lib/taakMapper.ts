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
  return {
    werknemerId: task.assigneeId ? Number(task.assigneeId) : 0,
    titel: task.name,
    beschrijving: task.description ?? task.specifications ?? '',
    deadline: task.dueDate,
  };
}
