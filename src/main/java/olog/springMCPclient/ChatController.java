package olog.springMCPclient;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/chat")
public class ChatController {
  private final ChatClient chat;
  private final ElasticTools tools;

  public ChatController(ChatClient.Builder builder, ElasticTools tools) {
    this.chat = builder.build();
    this.tools = tools;
  }

  @GetMapping("/ping")
  public String ping() { return "chat-ok"; }

  @PostMapping
  public String chat(@RequestBody String userMessage) {
    return chat
        .prompt(userMessage)
        .tools(tools)   //expose the MCP tools to llm
        .call()
        .content();
  }
}


