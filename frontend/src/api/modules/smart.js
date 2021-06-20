// import { responseItemTranformer } from '@/modules/graph/utils/item'
import { graphDataTransformer } from '@/modules/graph/utils/data'
import { linebreakFormat } from '@/modules/smart/utils/function'

export default {
  prefix: '/app',
  configs: {
    initGraph(projectId) {
      return {
        path: `/qaInitial/${projectId}`,
        method: 'GET'
      }
    },
    askQuestion(question) {
      return {
        path: '/question',
        method: 'POST',
        data: question,
        mapper(data) {
          return {
            graph: graphDataTransformer(data.graph),
            text: linebreakFormat(data.text)
          }
        }
      }
    },
    verifyInitiate(projectId) {
      return {
        path: `/verifyInitial/${projectId}`,
        method: 'GET'
      }
    },
    entityQuery(question) {
      return {
        path: '/entityQuery',
        method: 'POST',
        data: question,
        mapper(data) {
          return graphDataTransformer(data)
        }
      }
    },
    relationQuery(question) {
      return {
        path: '/relQuery',
        method: 'POST',
        data: question,
        mapper(data) {
          return graphDataTransformer(data)
        }
      }
    },
    getRelations(projectId) {
      return {
        path: `/relationNames/${projectId}`,
        method: 'GET'
      }
    },
    getHotQuestions(projectId) {
      return {
        path: `/hotQuestion/${projectId}`,
        method: 'GET'
      }
    },
    getCentrality(projectId) {
      return {
        path: `/centrality/${projectId}`,
        method: 'GET'
      }
    }
  }
}
