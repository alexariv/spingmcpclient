package olog.springMCPclient;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@Service
public class McpProxyService {
  private final HttpClient http = HttpClient.newHttpClient();
  private final ObjectMapper mapper = new ObjectMapper();

  public String listIndices(String pattern) throws Exception {
    var uri = URI.create(
        "http://localhost:8081/mcp/indices?pattern=" +
        URLEncoder.encode(pattern, StandardCharsets.UTF_8)
    );
    var res = http.send(HttpRequest.newBuilder(uri).GET().build(),
        HttpResponse.BodyHandlers.ofString());
    return res.body(); // JSON string from your proxy
  }

  public String search(String index, Map<String, Object> queryBody) throws Exception {
    var json = mapper.writeValueAsString(Map.of("index", index, "query_body", queryBody));
    var req = HttpRequest.newBuilder(URI.create("http://localhost:8081/mcp/search"))
        .header("Content-Type", "application/json")
        .POST(HttpRequest.BodyPublishers.ofString(json))
        .build();
    var res = http.send(req, HttpResponse.BodyHandlers.ofString());
    return res.body(); // JSON string from your proxy
  }
}
