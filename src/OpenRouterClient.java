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
    private static final String API_TOKEN = System.getenv("OPEN_ROUTER_API");

    private final HttpClient httpClient;

    public OpenRouterClient() {
        this.httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(100))
            .build();
    }

    // Main function that generates the full story in chunks
    public String generatePlot(String genre, String setting, String tone, String cinematicTone, String characters, String keywords) throws Exception {
        StringBuilder story = new StringBuilder();

        // Generate Intro
        String introPrompt = buildPlotPrompt(genre, setting, tone, cinematicTone, characters, keywords, "intro");
        story.append(sendRequest(introPrompt)).append("\n\n");

        // Generate First Half
        String firstHalfPrompt = buildPlotPrompt(genre, setting, tone, cinematicTone, characters, keywords, "first_half");
        firstHalfPrompt = "Continue the story based on the intro:\n" + story + "\n" + firstHalfPrompt;
        story.append(sendRequest(firstHalfPrompt)).append("\n\n");

        // Generate Second Half
        String secondHalfPrompt = buildPlotPrompt(genre, setting, tone, cinematicTone, characters, keywords, "second_half");
        secondHalfPrompt = "Continue the story based on the first half:\n" + story + "\n" + secondHalfPrompt;
        story.append(sendRequest(secondHalfPrompt));

        return story.toString();
    }

    // Send request to OpenRouter
    private String sendRequest(String prompt) throws IOException, InterruptedException {
        String jsonPayload = String.format(
            """
            {
              "model": "mistralai/mixtral-8x7b-instruct",
              "messages": [
                {"role": "system", "content": "You are a master storyteller crafting cinematic narratives with powerful emotional impact."},
                {"role": "user", "content": "%s"}
              ],
              "temperature": 0.85,
              "max_tokens": 800
            }
            """, escapeJson(prompt)
        );

        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(API_URL))
            .timeout(Duration.ofSeconds(60))
            .header("Authorization", "Bearer " + API_TOKEN)
            .header("Content-Type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(jsonPayload))
            .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200) {
            return parseResponse(response.body());
        } else {
            System.out.println("OpenRouter Error Code: " + response.statusCode());
            System.out.println("Body: " + response.body());
            return "⚠️ Failed to generate plot. Check API key or try again.";
        }
    }

    // Build concise prompt per chunk
    private String buildPlotPrompt(String genre, String setting, String tone, String cinematicTone, String characters, String keywords, String part) {
        StringBuilder prompt = new StringBuilder();
        switch (part) {
            case "intro":
                prompt.append("Write a cinematic story title and brief setup for a ")
                      .append(genre.toLowerCase())
                      .append(" story set in ").append(setting)
                      .append(" with a ").append(tone.toLowerCase())
                      .append(" tone. Introduce main characters and motivations. Keep it concise, ~150 words.");
                break;
            case "first_half":
                prompt.append("Develop the first half of the story: build the world, introduce conflict, include suspense and emotional moments. Limit ~400 words.");
                break;
            case "second_half":
                prompt.append("Conclude the story: resolve conflicts, escalate stakes, include 2-3 climactic moments. Limit ~400 words.");
                break;
            default:
                prompt.append("Write a compelling cinematic story based on the given setting and characters.");
        }

        if (characters != null && !characters.trim().isEmpty()) {
            prompt.append("\nCharacters: ").append(characters).append(".");
        }
        if (keywords != null && !keywords.trim().isEmpty()) {
            prompt.append("\nThemes: ").append(keywords).append(".");
        }
        return prompt.toString();
    }

    // Parse JSON response
    private String parseResponse(String responseBody) {
        try {
            JsonObject obj = JsonParser.parseString(responseBody).getAsJsonObject();
            String content = obj.getAsJsonArray("choices")
                                .get(0).getAsJsonObject()
                                .getAsJsonObject("message")
                                .get("content").getAsString();
            return content;
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
