package olog.springMCPclient;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class ElasticTools {
  private final McpProxyService mcp;

  public ElasticTools(McpProxyService mcp) { this.mcp = mcp; }

  @Tool(name = "list_indices", description = "List Elasticsearch indices by pattern, e.g. '*', 'logs-*'.")
  public String listIndices(@ToolParam(description = "Index glob pattern") String index_pattern) throws Exception {
    return mcp.listIndices(index_pattern);
  }

  public static class SearchArgs {
    @ToolParam(description = "Index name to search")
    @JsonProperty(required = true) public String index;

    @ToolParam(description = "Elasticsearch Query DSL map")
    @JsonProperty(required = true) public Map<String,Object> query_body;
  }

  @Tool(name = "search", description = "Search an Elasticsearch index using Query DSL.")
  public String search(SearchArgs args) throws Exception {
    return mcp.search(args.index, args.query_body);
  }
}
