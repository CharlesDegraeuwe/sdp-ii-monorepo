import type { Task } from '@/stores/taakStore';

export function mapBackendTask(dto: Record<string, unknown>): Task {
  return {
    id: String(dto.id),
    name: (dto.naam as string) ?? '',
    specifications: (dto.specificaties as string) ?? '',
    dueDate: (dto.deadline as string) ?? '',
    duration: (dto.duur as string) ?? undefined,
    location: (dto.locatie as string) ?? '',
    important: (dto.belangrijk as boolean) ?? false,
    finished: (dto.afgewerkt as boolean) ?? false,
    finishedAt: (dto.afgewerktOp as string) ?? undefined,
    assigneeId: dto.werknemerId ? String(dto.werknemerId) : undefined,
  };
}

export function mapTaskToBackend(task: Omit<Task, 'id' | 'finished'>) {
  return {
    naam: task.name,
    specificaties: task.specifications,
    deadline: task.dueDate,
    locatie: task.location,
    duur: task.duration,
    belangrijk: task.important,
  };
}
