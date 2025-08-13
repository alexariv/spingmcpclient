package olog.springMCPclient;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

@RestController
@RequestMapping("/mcp")
public class McpController {

  private static final String JSON = "application/json";
  private static final String ACCEPT = "application/json, text/event-stream";

  private final HttpClient http = HttpClient.newHttpClient();
  private final ObjectMapper mapper = new ObjectMapper();
  private final AtomicLong ids = new AtomicLong(1);

  private final String baseUrl;
  private final String authHeaderValue;

  public McpController(
      @Value("${mcp.server.base-url:http://localhost:8080/mcp}") String baseUrl,
      @Value("${mcp.server.auth:}") String authHeaderValue
  ) {
    this.baseUrl = baseUrl;
    this.authHeaderValue = authHeaderValue;
  }

  private HttpRequest.Builder req() {
    var b = HttpRequest.newBuilder(URI.create(baseUrl))
        .header("Content-Type", JSON)
        .header("Accept", ACCEPT);
    if (!authHeaderValue.isBlank()) b.header("Authorization", authHeaderValue);
    return b;
  }

  private String unwrapSse(java.net.http.HttpResponse<String> res) {
    String body = res.body();
    String ctype = res.headers().firstValue("content-type").orElse("");
    if (ctype.contains("text/event-stream") || body.startsWith("data:")) { //where the JSON-RPC payload is
       String json = body.lines()
        .filter(l -> l.startsWith("data:"))
        .map(l -> l.substring(5).trim()) // strip "data:"
        .reduce((a, b) -> b)             // keep the last event
        .orElse("{}");
      return json;
      }
      return body;
      }
      
      private ResponseEntity<String> postRpc(Map<String, Object> rpc) throws Exception {
        var body = mapper.writeValueAsString(rpc);
        var res = http.send(
          req().POST(HttpRequest.BodyPublishers.ofString(body)).build(),
          HttpResponse.BodyHandlers.ofString()
          );
          return ResponseEntity.status(res.statusCode()).body(unwrapSse(res));
          }
          
          @GetMapping("/tools")
          public ResponseEntity<String> tools() throws Exception {
            // {"jsonrpc":"2.0","id":1,"method":"tools/list","params":{}}
                rpc = Map.of(
                  "jsonrpc", "2.0",
                  "id", ids.getAndIncrement(),
                  "method", "tools/list",
                  "params", Map.of()
                  );
                  return postRpc(rpc);
                  }
                  
            @GetMapping("/indices")
            public ResponseEntity<String> indices(@RequestParam(defaultValue = "*") String pattern) throws Exception {
              var rpc = Map.of(
                "jsonrpc", "2.0",
                "id", ids.getAndIncrement(),
                "method", "tools/call",
                "params", Map.of(
                "name", "list_indices",
                "arguments", Map.of("index_pattern", pattern)
                )
                );
                return postRpc(rpc);
                }
                
            @GetMapping("/mappings")
            public ResponseEntity<String> mappings(@RequestParam String index) throws Exception {
              var rpc = Map.of(
                "jsonrpc", "2.0",
                "id", ids.getAndIncrement(),
                "method", "tools/call",
                "params", Map.of(
                "name", "get_mappings",
                "arguments", Map.of("index", index)
                )
                );
                return postRpc(rpc);
                }
            
            @GetMapping("/shards")
            public ResponseEntity<String> shards(@RequestParam(required = false) String index) throws Exception {
              Map<String, Object> args = (index == null || index.isBlank())
              ? Map.of()
              : Map.of("index", index);
              var rpc = Map.of(
              "jsonrpc", "2.0",
              "id", ids.getAndIncrement(),
              "method", "tools/call",
              "params", Map.of(
              "name", "get_shards",
              "arguments", args
              )
              );
              return postRpc(rpc);
              }

          @PostMapping("/esql")
          public ResponseEntity<String> esql(@RequestBody Map<String,Object> body) throws Exception {
            Object q = body.get("query");
            if (q == null || String.valueOf(q).isBlank()) {
              return ResponseEntity.badRequest().body("{\"error\":\"'query' is required\"}");
              }
              var rpc = Map.of(
                "jsonrpc","2.0","id",ids.getAndIncrement(),"method","tools/call",
                "params", Map.of("name","esql","arguments", Map.of("query", String.valueOf(q)))
                );
                return postRpc(rpc);
                }

          @PostMapping("/search")
          public ResponseEntity<String> search(@RequestBody Map<String, Object> args) throws Exception {
            var rpc = Map.of(
              "jsonrpc", "2.0",
              "id", ids.getAndIncrement(),
              "method", "tools/call",
              "params", Map.of(
              "name", "search",
              "arguments", args // expects {"index":"...","query_body":{...}}
              )
              );
              return postRpc(rpc);
              }
}




