const snapShotCache = new Map();

const get = projectId => {
  if (snapShotCache.has(projectId)) {
    return snapShotCache.get(projectId);
  }
  console.warn(`[SnapshotCache] cache not found(projectId = ${projectId})`);
  return null;
};

const set = (projectId, snapshot) => {
  const lastSnapshot = snapShotCache.get(projectId);
  if (lastSnapshot) {
    console.log(`[SnapshotCache] recent snapshot replaced`, lastSnapshot);
  }
  snapShotCache.set(projectId, snapshot);
  return lastSnapshot;
};

export const SnapshotCache = { get, set };
