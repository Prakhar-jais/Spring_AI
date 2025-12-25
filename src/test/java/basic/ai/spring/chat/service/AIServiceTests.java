package basic.ai.spring.chat.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class AIServiceTests {

    @Autowired
    private AIService aiservice;

    @Test
    public void testGetJoke(){
        var joke =  aiservice.getJoke("Coders");
        System.out.println(joke);
    }
}
