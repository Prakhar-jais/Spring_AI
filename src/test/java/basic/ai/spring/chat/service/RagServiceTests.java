package basic.ai.spring.chat.service;


import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class RagServiceTests {

    @Autowired
    private RagService ragService;


    @Test
    public void testIngest(){

        ragService.ingestPdfToVectorStore();
    }

    @Test
    public void testAskAI(){
        var response = ragService.askAI("what is the role of transformer model in machine translation");

        System.out.println(response);

        }
}
