import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DatabaseManager {
	private static final String DB_URL = "jdbc:mysql://localhost:3306/cineforge";
	private static final String DB_USER = "root";
	private static final String DB_PASSWORD = System.getenv("DB_PASSWORD"); // Load from env variable
	Connection connection;
	public DatabaseManager() {
	    try {
	        if (DB_PASSWORD == null) {
	            throw new RuntimeException("Database password not set! Please set DB_PASSWORD as an environment variable.");
	        }

	        Class.forName("com.mysql.cj.jdbc.Driver");
	        connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
	        createTables();

	    } catch (Exception e) {
	        System.err.println("Database connection failed: " + e.getMessage());
	        e.printStackTrace();
	    }
	}

    
    private void createTables() throws SQLException {
        String createTableSQL = """
            CREATE TABLE IF NOT EXISTS generated_plots (
                id BIGINT AUTO_INCREMENT PRIMARY KEY,
                genre VARCHAR(50) NOT NULL,
                setting TEXT NOT NULL,
                tone VARCHAR(50) NOT NULL,
                keywords TEXT,
                plot_text LONGTEXT NOT NULL,
                generated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                INDEX idx_genre (genre),
                INDEX idx_generated_at (generated_at)
            )
        """;
        
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(createTableSQL);
            System.out.println("Database tables initialized successfully.");
        }
    }
    
    public boolean savePlot(Plot plot) {
        String sql = """
            INSERT INTO generated_plots (genre, setting, tone, keywords, plot_text, generated_at)
            VALUES (?, ?, ?, ?, ?, ?)
        """;
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, plot.getGenre());
            pstmt.setString(2, plot.getSetting());
            pstmt.setString(3, plot.getTone());
            pstmt.setString(4, plot.getKeywords());
            pstmt.setString(5, plot.getPlotText());
            pstmt.setTimestamp(6, new Timestamp(plot.getGeneratedAt().getTime()));
            
            int rowsAffected = pstmt.executeUpdate();
            
            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                if (rs.next()) {
                    plot.setId(rs.getLong(1));
                }
            }
            
            if (rowsAffected > 0) {
                System.out.println("Plot saved successfully with ID: " + plot.getId());
                return true;
            } else {
                System.out.println("No rows affected when saving plot.");
                return false;
            }
            
        } catch (SQLException e) {
            System.err.println("Error saving plot: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    public List<Plot> getAllPlots() {
        List<Plot> plots = new ArrayList<>();
        String sql = "SELECT * FROM generated_plots ORDER BY generated_at DESC LIMIT 50";
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Plot plot = new Plot();
                plot.setId(rs.getLong("id"));
                plot.setGenre(rs.getString("genre"));
                plot.setSetting(rs.getString("setting"));
                plot.setTone(rs.getString("tone"));
                plot.setKeywords(rs.getString("keywords"));
                plot.setPlotText(rs.getString("plot_text"));
                plot.setGeneratedAt(rs.getTimestamp("generated_at"));
                
                plots.add(plot);
            }
            
        } catch (SQLException e) {
            System.err.println("Error loading plots: " + e.getMessage());
            e.printStackTrace();
        }
        
        return plots;
    }
    
    public List<Plot> getAllPlots(String sortOrder) {
        List<Plot> plots = new ArrayList<>();
        String orderByClause = getOrderByClause(sortOrder);
        String sql = "SELECT * FROM generated_plots " + orderByClause + " LIMIT 50";
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Plot plot = new Plot();
                plot.setId(rs.getLong("id"));
                plot.setGenre(rs.getString("genre"));
                plot.setSetting(rs.getString("setting"));
                plot.setTone(rs.getString("tone"));
                plot.setKeywords(rs.getString("keywords"));
                plot.setPlotText(rs.getString("plot_text"));
                plot.setGeneratedAt(rs.getTimestamp("generated_at"));
                
                plots.add(plot);
            }
            
        } catch (SQLException e) {
            System.err.println("Error loading plots: " + e.getMessage());
            e.printStackTrace();
        }
        
        return plots;
    }
    
    private String getOrderByClause(String sortOrder) {
        switch (sortOrder) {
            case "date_asc":
                return "ORDER BY generated_at ASC";
            case "date_desc":
                return "ORDER BY generated_at DESC";
            case "genre_asc":
                return "ORDER BY genre ASC, generated_at DESC";
            case "genre_desc":
                return "ORDER BY genre DESC, generated_at DESC";
            case "setting_asc":
                return "ORDER BY setting ASC, generated_at DESC";
            case "setting_desc":
                return "ORDER BY setting DESC, generated_at DESC";
            default:
                return "ORDER BY generated_at DESC";
        }
    }
    
    public List<Plot> searchPlots(String searchTerm) {
        List<Plot> plots = new ArrayList<>();
        String sql = """
            SELECT * FROM generated_plots 
            WHERE genre LIKE ? OR setting LIKE ? OR keywords LIKE ? OR plot_text LIKE ?
            ORDER BY generated_at DESC LIMIT 20
        """;
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            String searchPattern = "%" + searchTerm + "%";
            pstmt.setString(1, searchPattern);
            pstmt.setString(2, searchPattern);
            pstmt.setString(3, searchPattern);
            pstmt.setString(4, searchPattern);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Plot plot = new Plot();
                    plot.setId(rs.getLong("id"));
                    plot.setGenre(rs.getString("genre"));
                    plot.setSetting(rs.getString("setting"));
                    plot.setTone(rs.getString("tone"));
                    plot.setKeywords(rs.getString("keywords"));
                    plot.setPlotText(rs.getString("plot_text"));
                    plot.setGeneratedAt(rs.getTimestamp("generated_at"));
                    
                    plots.add(plot);
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Error searching plots: " + e.getMessage());
            e.printStackTrace();
        }
        
        return plots;
    }
    
    public boolean deletePlot(long plotId) {
        String sql = "DELETE FROM generated_plots WHERE id = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setLong(1, plotId);
            int rowsAffected = pstmt.executeUpdate();
            
            if (rowsAffected > 0) {
                System.out.println("Plot deleted successfully.");
                reorderPlotIds();
                return true;
            } else {
                System.out.println("No plot found with ID: " + plotId);
                return false;
            }
            
        } catch (SQLException e) {
            System.err.println("Error deleting plot: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean deletePlots(List<Long> plotIds) {
        if (plotIds == null || plotIds.isEmpty()) {
            return false;
        }
        
        StringBuilder sql = new StringBuilder("DELETE FROM generated_plots WHERE id IN (");
        for (int i = 0; i < plotIds.size(); i++) {
            if (i > 0) {
                sql.append(",");
            }
            sql.append("?");
        }
        sql.append(")");
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql.toString())) {
            for (int i = 0; i < plotIds.size(); i++) {
                pstmt.setLong(i + 1, plotIds.get(i));
            }
            
            int rowsAffected = pstmt.executeUpdate();
            
            if (rowsAffected > 0) {
                System.out.println(rowsAffected + " plots deleted successfully.");
                reorderPlotIds();
                return true;
            } else {
                System.out.println("No plots found with the provided IDs.");
                return false;
            }
            
        } catch (SQLException e) {
            System.err.println("Error deleting plots: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    private void reorderPlotIds() throws SQLException {
        int plotCount;
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM generated_plots")) {
            rs.next();
            plotCount = rs.getInt(1);
        }
        
        if (plotCount == 0) {
            try (Statement stmt = connection.createStatement()) {
                stmt.execute("ALTER TABLE generated_plots AUTO_INCREMENT = 1");
            }
            return;
        }
        
        try (Statement stmt = connection.createStatement()) {
            stmt.execute("CREATE TABLE generated_plots_temp LIKE generated_plots");
            
            stmt.execute("SET @count = 0");
            stmt.executeUpdate("INSERT INTO generated_plots_temp (id, genre, setting, tone, keywords, plot_text, generated_at) " +
                             "SELECT @count:=@count+1, genre, setting, tone, keywords, plot_text, generated_at " +
                             "FROM generated_plots ORDER BY id");
            
            stmt.execute("DROP TABLE generated_plots");
            
            stmt.execute("RENAME TABLE generated_plots_temp TO generated_plots");
            
            stmt.execute("ALTER TABLE generated_plots AUTO_INCREMENT = " + (plotCount + 1));
            
            System.out.println("Plot IDs reordered successfully.");
        }
    }
    
    public void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("Database connection closed.");
            }
        } catch (SQLException e) {
            System.err.println("Error closing database connection: " + e.getMessage());
        }
    }
}