//import com.cosin.model.vo.GraphVO;
//import com.cosin.model.vo.NodeDTO;
//import com.cosin.model.vo.RelationDTO;
//import com.fasterxml.jackson.core.JsonParseException;
//import com.fasterxml.jackson.core.JsonProcessingException;
//import com.fasterxml.jackson.databind.JsonMappingException;
//import com.fasterxml.jackson.dataformat.xml.XmlMapper;
//
//import java.io.IOException;
//import java.util.ArrayList;
//
//public class XmlTest {
//    public static void main(String[] args) {
////      new XmlTest().write();
//      new XmlTest().read();
//    }
//
//    void write(){
//        GraphVO graphVO = new GraphVO();
//        ArrayList nodes = new ArrayList();
//        nodes.add(new NodeDTO(1L,"皮卡丘","宠物",1,1));
//        nodes.add(new NodeDTO(2L,"小智","人物",2,1));
//        nodes.add(new NodeDTO(3L,"杰尼龟","宠物",3,1));
//        ArrayList rels = new ArrayList();
//        rels.add(new RelationDTO(1L,"主人", 1L,2L,1,1));
//        rels.add(new RelationDTO(2L,"朋友", 1L,3L,2,1));
//        graphVO.setNodes(nodes).setRelations(rels);
//        XmlMapper xmlMapper = new XmlMapper();
//        try {
//            String xml = xmlMapper.writeValueAsString(graphVO);
//            System.out.println(xml);
//        } catch (JsonProcessingException e) {
//            e.printStackTrace();
//        }
//    }
//
//    void read(){
//        XmlMapper xmlMapper = new XmlMapper();
//        String xml = "<graph><nodes><node><nodeId>1</nodeId><name>皮卡丘</name><group>宠物</group><val>1</val></node><node><nodeId>2</nodeId><name>小智</name><group>人物</group><val>2</val></node><node><nodeId>3</nodeId><name>杰尼龟</name><group>宠物</group><val>3</val></node></nodes><rels><rel><relationId>1</relationId><name>主人</name><source>1</source><target>2</target><val>1</val></rel><rel><relationId>2</relationId><name>朋友</name><source>1</source><target>3</target><val>2</val></rel></rels></graph>";
//        try {
//            GraphVO graphVO = xmlMapper.readValue(xml,GraphVO.class);
//            System.out.println(graphVO);
//        } catch (JsonParseException e) {
//            System.out.println("xml格式错误");
//        }catch (JsonMappingException e){
//            System.out.println("xml属性错误");
//        }catch (IOException e){
//
//        }
//    }
//}
