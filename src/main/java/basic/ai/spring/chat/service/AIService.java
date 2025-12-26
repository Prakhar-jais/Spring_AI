package basic.ai.spring.chat.service;

import basic.ai.spring.chat.dto.Joke;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AIService {


    private final ChatClient chatClient;

    private final EmbeddingModel embeddingModel;

    private final VectorStore vectorStore;

    public float[] getEmbeddings(String text){
        return embeddingModel.embed(text);
    }

//    public void ingestDataToVectorStore(String text){
//        Document document = new Document(text);
//        vectorStore.add(List.of(document));
//    }


    public String askAI(String prompt){

        String template = """
                You are an AI assistant halping a developer.
                Rules:
                - Use ONLY provided information in the context 
                - You may rephrase, summarize, and explain in natural language
                - Do not introduce new concepts or facts
                - If multiple context sections are relevant, combine them into single explanation.
                - If the answer is not present, say "I don't know"
                
                Context:
                {context}
                
                Answer in a friendly, conversational tone.
                """;

        List<Document> documents = vectorStore.similaritySearch(SearchRequest.builder()
                .query(prompt)
                .topK(2)
                .filterExpression("topic == 'ai' or topic == 'vectorstore'")
                .build());
        String context =  documents.stream().map(Document::getText).collect(Collectors.joining("\n\n"));
        PromptTemplate promptTemplate = new PromptTemplate(template);
        String system = promptTemplate.render(Map.of("context",context));


        return chatClient.prompt()
                .system(system)
                .user(prompt)
                .advisors(new SimpleLoggerAdvisor())
                .call()
                .content();
    }

    public static List<Document> ingestSpringAiDocs(){
        List<Document> springAiDocs = List.of(
                new Document("Introduction to Spring AI and its integration with the Spring ecosystem.",
                        Map.of("title","Spring AI Overview","category","documentation","year",2023)),

                new Document("Guide to configuring OpenAI and Azure OpenAI models with Spring AI.",
                        Map.of("title","Model Configuration","category","setup","year",2023)),

                new Document("Learn how to use Prompt Templates for dynamic AI-driven responses.",
                        Map.of("title","Prompt Templates","category","usage","year",2023)),

                new Document("Step-by-step tutorial on building chat applications with Spring AI.",
                        Map.of("title","Chat Applications","category","tutorial","year",2023)),

                new Document("Explanation of embedding models and vector stores for semantic search.",
                        Map.of("title","Embeddings & Vector Stores","category","advanced","year",2023)),

                new Document("Walkthrough on integrating Spring AI with external APIs and services.",
                        Map.of("title","API Integration","category","integration","year",2023))
        );
        return springAiDocs;

    }

    public void ingestDataToVectorStore(){
        List<Document> movies = List.of(
                new Document("A thief who steals corporate secrets through dream sharing tech.",Map.of("title","Inception","genre","sci-fi","year",2010)),
                new Document("A team of explorers travel through a blackhole in space to ensure humaniy survival.",Map.of("title","Intersteller","genre","sci-fi","year",2014)),
                new Document("A poor passionate young men falls in love iwth rich young woman, giving her sense of freedom.",Map.of("title","The Notebook","genre","romance","year",2004))
        ) ;

        vectorStore.add(movies);
        vectorStore.add(ingestSpringAiDocs());

    }

    public List<Document> similaritySearch(String text){

        return vectorStore.similaritySearch(SearchRequest.builder()
                .query(text)
                .topK(3)
                .similarityThreshold(0.3)
                .build());

    }

    public String getJoke(String topic){

        String systemPrompt = """
                You are a sarcastic joker , you make poetic jokes in 5 lines
                You don't make joke about politics.
                Give a joke on the topic : {topic}
                """ ;
        PromptTemplate promptTemplate = new PromptTemplate(systemPrompt);
        String renderdText = promptTemplate.render(Map.of("topic",topic));

//
//        return chatClient.prompt()
//                .system("you are sarcastic joker who responds in jokes only")
//                .user(renderdText)
//                .call()
//                .content();


        // Or u can use

//        var response = chatClient.prompt()
//                .system("you are sarcastic joker who responds in jokes only")
//                .user(renderdText)
//                .call()
//                .chatClientResponse();
//
//        return response.chatResponse().getResult().getOutput().getText();


        var response = chatClient.prompt()
                .system("you are sarcastic joker who responds in jokes only")
                .user(renderdText)
                .advisors(new SimpleLoggerAdvisor())
                .call()
                .entity(Joke.class);

        return response.text();
    }

}
