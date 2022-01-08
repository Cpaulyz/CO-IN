export const ROUTE_PATH = {
  // prefix = /user
  User: '/user',
  Login: '/user',
  Register: '/user/register',
  // prefix = /search
  Search: '/search',
  // prefix = /
  Home: '/',
  HomeProjects: '/',
  HomeSquare: '/square',
  Chat: '/chat',
  Tutorial: '/tutorial',
  SystemDesign: '/systemdesign',
  // prefix = /graph
  Graph: projectId => `/graph/${projectId}`,
  Smarthelper: projectId => `/smarthelper/${projectId}`,
  // prefix = /notfound
  NotFound: '/notfound',
};
