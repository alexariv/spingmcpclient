package olog.springMCPclient;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/chat")
public class ChatController {
  private final ChatClient chat;

  public ChatController(ChatClient.Builder builder) {
    this.chat = builder.build();
  }

  @GetMapping("/ping")
  public String ping() { return "chat-ok"; }

  @PostMapping
  public String chat(@RequestBody String userMessage) {
    return chat.prompt(userMessage).call().content();
  }
}

