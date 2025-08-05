import java.util.Date;

public class Plot {
    private long id;
    private String genre;
    private String setting;
    private String tone;
    private String characters;
    private String keywords;
    private String plotText;
    private Date generatedAt;
    
    public Plot() {
        this.generatedAt = new Date();
    }
    
    public Plot(String genre, String setting, String tone, String characters, String keywords, String plotText) {
        this.genre = genre;
        this.setting = setting;
        this.tone = tone;
        this.characters = characters;
        this.keywords = keywords;
        this.plotText = plotText;
        this.generatedAt = new Date();
    }
    
    public long getId() { return id; }
    public void setId(long id) { this.id = id; }
    
    public String getGenre() { return genre; }
    public void setGenre(String genre) { this.genre = genre; }
    
    public String getSetting() { return setting; }
    public void setSetting(String setting) { this.setting = setting; }
    
    public String getTone() { return tone; }
    public void setTone(String tone) { this.tone = tone; }
    
    public String getCharacters() { return characters; }
    public void setCharacters(String characters) { this.characters = characters; }
    
    public String getKeywords() { return keywords; }
    public void setKeywords(String keywords) { this.keywords = keywords; }
    
    public String getPlotText() { return plotText; }
    public void setPlotText(String plotText) { this.plotText = plotText; }
    
    public Date getGeneratedAt() { return generatedAt; }
    public void setGeneratedAt(Date generatedAt) { this.generatedAt = generatedAt; }
    
    @Override
    public String toString() {
        return String.format("%s - %s", genre, setting);
    }
}