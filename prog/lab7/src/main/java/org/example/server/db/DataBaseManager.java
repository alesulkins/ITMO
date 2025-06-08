package org.example.server.db;

import org.example.shared.product.Product;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.*;

public class DataBaseManager {
    private static String URL = "jdbc:postgresql://pg:5432/studs";
    private static String username = "s372978";
    private static String password = "AMfn8yvLHeB4tvsL";
    private static Connection connection;

    private static final String GET_USERS = "SELECT * FROM my_users";

    public static void connectToDataBase() {
        try {
            Class.forName("org.postgresql.Driver");
            connection = DriverManager.getConnection(URL, username, password);
            System.out.println("Connection is ready");
        } catch (SQLException | ClassNotFoundException e) {
            System.out.println("Error while connecting to database");
            System.out.println(e.getMessage());
            System.exit(-1);
        }
    }


    private static String GET_USER_BY_USERNAME = "SELECT * FROM my_users WHERE login = ?";

    public static int getUserId(String login) {
        try {
            PreparedStatement getStatement = connection.prepareStatement(GET_USER_BY_USERNAME);
            getStatement.setString(1, login);
            ResultSet rs = getStatement.executeQuery();
            if (rs.next()) {
                return rs.getInt("id");
            }
            return -1;
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return -1;
    }

    private static String INSERT_USER = "INSERT INTO my_users (login, password) VALUES (?,?)";

    public static boolean insertUser(String login, String password) {
        try(PreparedStatement insertStatement = connection.prepareStatement(INSERT_USER)) {
            insertStatement.setString(1, login);
            insertStatement.setString(2, md5(password));
            insertStatement.executeUpdate();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return true;
    }

    private static final String INSERT_PRODUCT = "INSERT INTO products (user_id, name, x, y, date, price, unit_of_measure, owner, height, color, nationality) " +
            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) RETURNING id";

    public static long insertProduct(Product product, String login) {
        int userId = getUserId(login);
        try (PreparedStatement statement = connection.prepareStatement(INSERT_PRODUCT)) {
            statement.setInt(1, userId);
            insertProductDataIntoStatement(product, statement);

            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    return rs.getLong("id");
                } else {
                    throw new SQLException("No ID returned.");
                }
            }
        } catch (SQLException e) {
            System.out.println("Couldn't add product. Reason: " + e.getMessage());
            return -1;
        }
    }


    private static void insertProductDataIntoStatement(Product product, PreparedStatement statement) {
        try {
            statement.setString(2, product.getName());
            statement.setInt(3, product.getCoordinates().getX());
            statement.setInt(4, (int) product.getCoordinates().getY());
            statement.setDate(5, Date.valueOf(product.getCreationDate().toLocalDate()));
            statement.setDouble(6, product.getPrice());
            statement.setString(7, String.valueOf(product.getUnitOfMeasure()));
            statement.setString(8, product.getOwner().getName());
            statement.setInt(9, product.getOwner().getHeight());
            statement.setString(10, product.getOwner().getHairColor().toString());
            statement.setString(11, product.getOwner().getNationality().toString());
        } catch (SQLException e) {
            System.out.println("Couldn't insert product data into statement. Reason: " + e.getMessage());
        }
    }

    public static boolean updateProductById(Product product) {
        String sql = "UPDATE products SET " +
                "name = ?, x = ?, y = ?, date = ?, price = ?, unit_of_measure = ?, " +
                "owner = ?, height = ?, color = ?, nationality = ? " +
                "WHERE id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, product.getName());
            stmt.setInt(2, product.getCoordinates().getX());
            stmt.setInt(3, (int) product.getCoordinates().getY());
            stmt.setDate(4, Date.valueOf(product.getCreationDate().toLocalDate()));
            stmt.setDouble(5, product.getPrice());
            stmt.setString(6, product.getUnitOfMeasure().toString());
            stmt.setString(7, product.getOwner().getName());
            stmt.setInt(8, product.getOwner().getHeight());
            stmt.setString(9, product.getOwner().getHairColor().toString());
            stmt.setString(10, product.getOwner().getNationality().toString());
            stmt.setLong(11, product.getId()); // ← WHERE id = ?

            int rowsUpdated = stmt.executeUpdate();
            return rowsUpdated > 0;
        } catch (SQLException e) {
            System.out.println("Error updating product: " + e.getMessage());
            return false;
        }
    }

    public static boolean removeProductById(long productId, String login) {
        int userId = getUserId(login);
        String sql = "DELETE FROM products WHERE id = ? AND user_id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, productId);
            stmt.setInt(2, userId);
            int rowsAffected = stmt.executeUpdate();

            return rowsAffected > 0;
        } catch (SQLException e) {
            System.out.println("Error deleting product by ID: " + e.getMessage());
            return false;
        }
    }

    public static int getProductOwnerId(long productId) {
        String query = "SELECT user_id FROM products WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setLong(1, productId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("user_id");
            }
        } catch (SQLException e) {
            System.out.println("Error getting product owner ID: " + e.getMessage());
        }
        return -1; //если нет
    }

    public static ConcurrentLinkedDeque<Product> loadAllProducts() {
        ConcurrentLinkedDeque<Product> products = new ConcurrentLinkedDeque<>();

        String sql = "SELECT * FROM products";

        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                try {
                    Product product = new Product(
                            rs.getString("name"),
                            new org.example.shared.product.Coordinates(
                                    rs.getInt("x"),
                                    rs.getInt("y")
                            ),
                            rs.getDouble("price"),
                            org.example.shared.product.UnitOfMeasure.valueOf(rs.getString("unit_of_measure")),
                            (rs.getString("owner") != null)
                                    ? new org.example.shared.product.Person(
                                    rs.getString("owner"),
                                    rs.getObject("height") != null ? rs.getInt("height") : null,
                                    org.example.shared.product.Color.valueOf(rs.getString("color")),
                                    org.example.shared.product.Country.valueOf(rs.getString("nationality"))
                            )
                                    : null
                    );

                    product.setId(rs.getLong("id"));

                    int userId = rs.getInt("user_id");
                    product.setOwnerLogin(getLoginByUserId(userId));

                    products.add(product);
                } catch (Exception e) {
                    System.out.println("Ошибка при сборке продукта из БД: " + e.getMessage());
                }
            }

        } catch (SQLException e) {
            System.out.println("Ошибка загрузки продуктов из БД: " + e.getMessage());
        }

        return products;
    }

    public static boolean clearProductsByUserId(int userId) {
        String sql = "DELETE FROM products WHERE user_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.out.println("Error clearing products by user ID: " + e.getMessage());
            return false;
        }
    }

    public static String getLoginByUserId(int userId) {
        String sql = "SELECT login FROM my_users WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getString("login");
            }
        } catch (SQLException e) {
            System.out.println("Ошибка получения логина по user_id: " + e.getMessage());
        }
        return null;
    }

    public static boolean checkUserCredentials(String login, String rawPassword) {
        String hashed = md5(rawPassword);
        try (PreparedStatement stmt = connection.prepareStatement(GET_USER_BY_USERNAME)) {
            stmt.setString(1, login);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getString("password").equals(hashed);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return false;
    }

    public static void getUsers() {
        try {
            PreparedStatement getStatement = connection.prepareStatement(GET_USERS);
            ResultSet rs = getStatement.executeQuery();
            while (rs.next()) {
                System.out.println(rs.getInt("id") + " " + rs.getString("login"));
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public static String md5(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] digest = md.digest(input.getBytes(StandardCharsets.UTF_8));
            StringBuilder hex = new StringBuilder();
            for (byte b : digest) {
                hex.append(String.format("%02x", b));
            }
            return hex.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

}
