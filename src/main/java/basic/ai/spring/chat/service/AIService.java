package basic.ai.spring.chat.service;

import basic.ai.spring.chat.dto.Joke;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class AIService {


    private final ChatClient chatClient;

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
