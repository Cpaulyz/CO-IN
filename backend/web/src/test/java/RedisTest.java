//import com.cosin.COINWebApplication;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.data.redis.core.RedisTemplate;
//import org.springframework.test.context.junit4.SpringRunner;
//
//import javax.annotation.Resource;
//
//@RunWith(SpringRunner.class)
//@SpringBootTest(classes = COINWebApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
//public class RedisTest {
//
//    @Autowired
//    private RedisTemplate<String,String> redisTemplate;
//
//    @Test
//    public void set(){
////        redisTemplate.opsForValue().set("myKey","myValue");
////        System.out.println(redisTemplate.opsForValue().get("myKey"));
////        System.out.println(redisTemplate.opsForHash().increment("graphid","1",1));
//        int value = 5;
//        redisTemplate.opsForHash().put("graphid","3",""+value);
//        System.out.println(redisTemplate.opsForHash().get("graphid","3"));
//    }
//
//
//}
