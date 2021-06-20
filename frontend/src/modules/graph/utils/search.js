export const buildSearchHistoryKey = (userId, projectId) => {
  return `searchHistory:${userId}:${projectId}`
}

export const getSearchHistory = key => {
  return JSON.parse(localStorage.getItem(key)) || []
}

export const setSearchHistory = (key, history) => {
  localStorage.setItem(key, JSON.stringify(history))
}
