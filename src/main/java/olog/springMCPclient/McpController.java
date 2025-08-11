package olog.springMCPclient;

import io.modelcontextprotocol.client.McpSyncClient;
import io.modelcontextprotocol.spec.McpSchema;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/mcp")
public class McpController {

  private final McpSyncClient mcp;

  public McpController(McpSyncClient mcp) {
    this.mcp = mcp;
  }

  @GetMapping("/tools")
  public Object tools() {
    return mcp.listTools();
  }

  @GetMapping("/indices")
  public Object indices(@RequestParam(defaultValue = "*") String pattern) {
    // Map args directly
    Map<String, Object> args = Map.of("index_pattern", pattern);

    McpSchema.CallToolRequest req = McpSchema.CallToolRequest.builder()
        .name("list_indices")
        .arguments(args)  
        .build();

    return mcp.callTool(req);
  }

  @PostMapping("/search")
  public Object search(@RequestBody Map<String, Object> body) {
    // body already is Map<String,Object>: { "index": "...", "query_body": {...} }
    McpSchema.CallToolRequest req = McpSchema.CallToolRequest.builder()
        .name("search")
        .arguments(body) 
        .build();

    return mcp.callTool(req);
  }
}


