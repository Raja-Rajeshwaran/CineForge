import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class OpenRouterClient {
    private static final String API_URL = "https://openrouter.ai/api/v1/chat/completions";
    private static final String API_TOKEN = System.getenv("OPENROUTER_API_KEY");
    
    private final HttpClient httpClient;
    
    public OpenRouterClient() {
        this.httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(30))
            .build();
    }
    
    public String generatePlot(String genre, String setting, String tone, String cinematicTone, String characters, String keywords) throws Exception {
        String prompt = buildPlotPrompt(genre, setting, tone, cinematicTone, characters, keywords);
        
        String jsonPayload = String.format(
            """
            {
              "model": "mistralai/mixtral-8x7b-instruct",
              "messages": [
                {"role": "system", "content": "You are a master storyteller crafting cinematic narratives with powerful emotional impact. Your stories feature compelling titles, natural two-part structure, and multiple goosebumps-inducing moments."},
                {"role": "user", "content": "%s"}
              ],
              "temperature": 0.85,
              "max_tokens": 3000,
              "stop": null
            }
            """, escapeJson(prompt)
        );
        
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(API_URL))
            .timeout(Duration.ofSeconds(150))  // Increased timeout for longer stories
            .header("Authorization", "Bearer " + API_TOKEN)
            .header("Content-Type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(jsonPayload))
            .build();
            
        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                return parseResponse(response.body());
            } else {
                System.out.println("OpenRouter Error Code: " + response.statusCode());
                System.out.println("Body: " + response.body());
                return "⚠️ Failed to generate plot. Try again or check API key.";
            }
        } catch (IOException | InterruptedException e) {
            System.err.println("API call failed: " + e.getMessage());
            return "⚠️ Network error occurred while generating plot.";
        }
    }
    
    private String buildPlotPrompt(String genre, String setting, String tone, String cinematicTone, String characters, String keywords) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("Create a complete, immersive ")
              .append(cinematicTone)
              .append(" narrative with the following requirements:\n\n")
              .append("TITLE: Begin with a compelling, cinematic title that captures the essence of the story.\n\n")
              .append("SETTING: Set in ")
              .append(setting)
              .append(". This is a ")
              .append(genre.toLowerCase())
              .append(" story with a ")
              .append(tone.toLowerCase())
              .append(" tone.\n\n");
              
        if (characters != null && !characters.trim().isEmpty()) {
            prompt.append("CHARACTERS: ").append(characters).append(". Develop these characters with depth, motivations, and meaningful arcs.\n\n");
        } else {
            prompt.append("CHARACTERS: Create compelling, multi-dimensional characters whose journeys drive the narrative.\n\n");
        }
              
        if (keywords != null && !keywords.trim().isEmpty()) {
            prompt.append("THEMES: Naturally incorporate these themes: ").append(keywords).append(".\n\n");
        }
        
        prompt.append("STRUCTURE:\n")
              .append("- First Half: Establish the world, introduce characters, and build the central conflict. Create intrigue and emotional investment.\n")
              .append("- Second Half: Escalate the stakes, intensify the conflict, and drive toward a powerful, satisfying resolution.\n\n")
              .append("GOOSEBUMPS MOMENTS: Include at least 4-5 moments throughout the story that give the audience goosebumps. These could be:\n")
              .append("  * Shocking revelations or plot twists\n")
              .append("  * Intense emotional confrontations\n")
              .append("  * Breathtaking action sequences\n")
              .append("  * Profound character realizations\n")
              .append("  * Haunting or beautiful imagery\n")
              .append("  * Moments of pure terror or ecstasy\n\n")
              .append("WRITING STYLE:\n")
              .append("- Write as a flowing narrative without section breaks\n")
              .append("- Create vivid, cinematic scenes with sensory details\n")
              .append("- Build tension gradually and release it dramatically\n")
              .append("- Use authentic dialogue that reveals character\n")
              .append("- Maintain the ")
              .append(cinematicTone)
              .append(" style throughout\n")
              .append("- Ensure the story feels complete and immersive\n\n")
              .append("Begin with the TITLE, then write the complete narrative that delivers all these elements.");
              
        return prompt.toString();
    }
    
    private String parseResponse(String responseBody) {
        try {
            JsonObject obj = JsonParser.parseString(responseBody).getAsJsonObject();
            String content = obj
                .getAsJsonArray("choices")
                .get(0).getAsJsonObject()
                .getAsJsonObject("message")
                .get("content").getAsString();
                
            if (content.length() > 800 && (content.endsWith(".") || content.endsWith("!") || content.endsWith("?") || content.endsWith("\""))) {
                String[] lines = content.split("\n", 2);
                if (lines.length > 1 && lines[0].trim().length() > 0) {
                    return content;
                } else {
                    return "TITLE: Untitled\n\n" + content;
                }
            } else {
                return content + "\n\n[Note: The story may be incomplete. Try regenerating for a fuller narrative.]";
            }
        } catch (Exception e) {
            System.err.println("Error parsing OpenRouter response: " + e.getMessage());
            return "⚠️ Could not parse response from OpenRouter.";
        }
    }
    
    private String escapeJson(String text) {
        return text.replace("\"", "\\\"")
                   .replace("\n", "\\n")
                   .replace("\r", "\\r")
                   .replace("\t", "\\t");
    }
}