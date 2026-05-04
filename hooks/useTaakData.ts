import { useEffect } from 'react';
import { useTaakStore } from '@/stores/taakStore';

const STALE_MS = 5 * 60 * 1000;

export const useTaakData = () => {
  const {
    setTeams,
    setMembers,
    setTasks,
    setLoaded,
    setLastSynced,
    lastSynced,
    loaded,
  } = useTaakStore();

  useEffect(() => {
    const fetchData = async () => {
      if (lastSynced && Date.now() - lastSynced < STALE_MS) {
        setLoaded(true);
        return;
      }

      try {
        const [teamsRes, membersRes, tasksRes] = await Promise.all([
          fetch('/api/teams'),
          fetch('/api/members'),
          fetch('/api/tasks'),
        ]);

        const [teams, members, tasks] = await Promise.all([
          teamsRes.json(),
          membersRes.json(),
          tasksRes.json(),
        ]);

        setTeams(teams);
        setMembers(members);
        setTasks(tasks);
        setLastSynced(Date.now());
        setLoaded(true);
      } catch (e) {
        console.error('Failed to fetch team data', e);
        setLoaded(true);
      }
    };

    void fetchData();
  }, []);

  return { loaded };
};
