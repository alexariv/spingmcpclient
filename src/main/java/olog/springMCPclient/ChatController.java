package olog.springMCPclient;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/chat")
public class ChatController {
  private static final Logger log = LoggerFactory.getLogger(ChatController.class);

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
    log.info("CHAT: received: {}", userMessage);
    String out = chat
        .prompt(userMessage)
        .tools(tools)                 // expose MCP-backed tools
        .call()
        .content();
    log.info("CHAT: done ({} chars)", out.length());
    return out;
  }
}
