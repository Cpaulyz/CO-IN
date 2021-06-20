// 生成所有路由
export const makeAllRoute = routes => {
  let allRoute = []
  for (let i = 0; i < routes.length; i++) {
    if (routes[i].children) {
      for (let j = 0; j < routes[i].children.length; j++) {
        if (routes[i]['children'][j].path === '') {
          allRoute.push(routes[i].path)
        } else {
          allRoute.push(routes[i].path + `/${routes[i]['children'][j].path}`)
        }
      }
    } else {
      allRoute.push(routes[i].path)
    }
  }
  return allRoute
}

export const makeAllRoute2 = routes => {
  let allRoute = []
  routes.forEach(({ path, children }) => {
    if (children) {
      children.forEach(({ path: cPath }) => {
        allRoute.push(`${path}${cPath ? '/' + cPath : ''}`)
      })
    } else {
      allRoute.push(path)
    }
  })
  return allRoute
}
