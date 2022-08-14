import org.apache.log4j.Logger;

import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class ImageRepositoryImpl implements ImageRepository {

    private static final String INSERT_INTO_IMAGES = "INSERT INTO images (name, time, key, size) VALUES (?, ?, ?, ?)";
    private static final String SELECT_ALL_ORDER_BY_SIZE_DESC = "SELECT * FROM images ORDER BY size DESC";
    private static final String SELECT_BY_SIZE_RANGE = "SELECT * FROM images WHERE size BETWEEN ? AND ? ORDER BY size DESC";
    private static final String ERROR_MESSAGE = "An error occurred during executing query or setting up connection";
    private static final Logger log = Logger.getLogger(ImageRepositoryImpl.class); // TODO: rename from log to logger

    private final DataSource dataSource;

    public ImageRepositoryImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public void save(Image image) {
        try (final PreparedStatement preparedStatement = dataSource.getConnection().prepareStatement(INSERT_INTO_IMAGES)) {
            preparedStatement.setString(1, image.getName());
            preparedStatement.setTimestamp(2, Timestamp.valueOf(image.getCreatingTimestamp()));
            preparedStatement.setString(3, image.getS3ObjectKey());
            preparedStatement.setInt(4, image.getSize());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            log.error(ERROR_MESSAGE);
            // TODO: create custom exception which wrap SQLException and throw your custom exception. We need more informative message with custom exception
        }
    }

    // TODO: what is global top? rename to more informative, also rename from get to find, f.e. findAll()
    @Override
    public List<Image> getGlobalTop() {
        try (final PreparedStatement preparedStatement = dataSource.getConnection().prepareStatement(SELECT_ALL_ORDER_BY_SIZE_DESC)) {
            return getImages(preparedStatement.executeQuery());
        } catch (SQLException e) {
            return catchConnectionOrQueryExecutionException(e);
        }
    }

    @Override
    public List<Image> getTopBySizeRange(int from, int to) { // TODO: rename from get to find
        try (final PreparedStatement preparedStatement = dataSource.getConnection().prepareStatement(SELECT_BY_SIZE_RANGE)) {
            preparedStatement.setInt(1, from * 1000); // TODO: extract 1000 to constant
            preparedStatement.setInt(2, to * 1000); // TODO: extract 1000 to constant
            return getImages(preparedStatement.executeQuery());
        } catch (SQLException e) {
            return catchConnectionOrQueryExecutionException(e);
        }
    }

    private List<Image> getImages(ResultSet resultSet) throws SQLException {
        List<Image> imagesList = new ArrayList<>();
        while (resultSet.next()) {
            imagesList.add(new Image(
                resultSet.getString("name"),
                resultSet.getTimestamp("time").toLocalDateTime(),
                resultSet.getString("key"), // TODO: rename "key" to more informative
                resultSet.getInt("size"))
            );
        }
        return imagesList;
    }

    // TODO: rename to more appropriate - in this method you dont catch Connection or QExecution Exceptions, you just return empty list and log some problem
    private List<Image> catchConnectionOrQueryExecutionException(Exception e) {
        log.error(ERROR_MESSAGE, e);
        return new ArrayList<>(); // TODO: you can use Collections.emptyList()
    }
}
