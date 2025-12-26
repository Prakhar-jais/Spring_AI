package basic.ai.spring.chat.service;

import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.reader.pdf.PagePdfDocumentReader;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RagService {


    private final ChatClient chatClient;

    private final EmbeddingModel embeddingModel;

    private final VectorStore vectorStore;

    @Value("classpath:MachineTranslation.pdf")
    Resource pdfFile;
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
                .similarityThreshold(0.5)
                .filterExpression("file_name == 'MachineTranslation.pdf'")
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

    // i have to run this code only once , not everytime
    public void ingestPdfToVectorStore(){
        PagePdfDocumentReader reader = new PagePdfDocumentReader(pdfFile);
        List<Document> pages = reader.get();

        TokenTextSplitter tokenTextSplitter = TokenTextSplitter.builder()
                .withChunkSize(200)
                .build();

        List<Document>chunks = tokenTextSplitter.apply(pages);
        vectorStore.add(chunks);

    }


}
