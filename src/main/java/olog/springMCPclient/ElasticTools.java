package olog.springMCPclient;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

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
  @Tool(name = "get_mappings", description = "Get field mappings for a specific Elasticsearch index.")
public String getMappings(@ToolParam(description = "Index name") String index) throws Exception {
  return mcp.getMappings(index);
}

public static class EsqlArgs {
  @ToolParam(description = "Complete ES|QL query, e.g. 'from my_index | limit 5'")
  @JsonProperty(required = true) public String query;
}

@Tool(name = "esql", description = "Perform an Elasticsearch ES|QL query.")
public String esql(EsqlArgs args) throws Exception {
  return mcp.esql(args.query);
}

public static class ShardsArgs {
  @ToolParam(description = "Optional index name (omit for all indices)")
  public String index; // optional
}

@Tool(name = "get_shards", description = "Get shard information for all or a specific index.")
public String getShards(ShardsArgs args) throws Exception {
  String idx = (args == null) ? null : args.index;
  return mcp.getShards(idx);
}
}

