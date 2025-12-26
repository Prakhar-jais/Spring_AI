package basic.ai.spring.chat.service;

import org.junit.jupiter.api.Test;
import org.springframework.ai.document.Document;
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

    @Test
    public void testEmbedText(){
        var embed = aiservice.getEmbeddings(" Text that will go for creation of embeddings");
        System.out.println(embed.length);
        for(float e: embed ){
            System.out.println(e+" ");
        }
    }

//    @Test
//    public void testStoreData(){
//        aiservice.ingestDataToVectorStore("This is a big text for storing data into vector store");
//    }
    @Test
    public void testStoreData(){
        aiservice.ingestDataToVectorStore();
    }

    @Test
    public void testSimilaritySearch(){
        var res = aiservice.similaritySearch("space movie");
        for(Document val:res){
            System.out.println(val);

        }
    }

//    @Test
//    public void testAskAI(){
////        var response = aiservice.askAI("what is the role of stand up comedian ");
//        var response = aiservice.askAI("what is the role of spring ai developer");
//
//        System.out.println(response);
//
//        }
//    }
}
