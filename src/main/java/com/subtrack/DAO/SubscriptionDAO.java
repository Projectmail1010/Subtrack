package com.subtrack.DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.subtrack.models.Subscription;
import com.subtrack.utils.DBConnection;

public class SubscriptionDAO {
    private static final String INSERT_SQL = "INSERT INTO subscriptions (platform, name, cost,  auto_pay, billing_cycle, duration_days, start_date, next_due_date) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
    private static final String SELECT_BY_ID =
        "SELECT * FROM subscriptions WHERE id = ?";
    private static final String SELECT_ALL = "SELECT * FROM subscriptions";
    private static final String SELECT_BY_PLATFORM = "SELECT * FROM subscriptions WHERE platform = ?";
    private static final String SELECT_BY_NAME = "SELECT * FROM subscriptions WHERE name = ?";
    private static final String SELECT_BY_NEXT_DUE_DATE = "SELECT * FROM subscriptions WHERE next_due_date = ?";
    private static final String 
    SELECT_BY_IS_ACTIVE = "SELECT * FROM subscriptions WHERE is_active = ?";
    private static final String UPDATE_BY_ID = "UPDATE subscriptions SET platform = ?, name = ?, cost = ?, billing_cycle = ?, duration_days = ?, next_due_date = ?, auto_pay = ?, start_date = ?, is_active = ? WHERE id = ?";
    private static final String DELETE_BY_ID = "DELETE FROM subscriptions WHERE id = ?";
    private static final String DELETE_BY_PLATFORM = "DELETE FROM subscriptions WHERE platform = ?";
    private static final String DELETE_BY_NAME = "DELETE FROM subscriptions WHERE name = ?";

    public Subscription add(Subscription s){
        try (Connection con = DBConnection.getConnection()) {
            PreparedStatement pst = con.prepareStatement(INSERT_SQL, java.sql.Statement.RETURN_GENERATED_KEYS);
            pst.setString(1, s.getPlatform());
            pst.setString(2, s.getName());
            pst.setDouble(3, s.getCost());
            pst.setBoolean(4, s.isAutoPay());
            pst.setString(5, s.getBillingCycle());
            pst.setInt(6, s.getDuration());
            pst.setDate(7, java.sql.Date.valueOf(s.getStartDate()));
            pst.setDate(8, java.sql.Date.valueOf(s.getNextDueDate()));
            
            

            int affectedRows = pst.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating subscription failed, no rows affected.");
            }

            try (ResultSet generatedKeys = pst.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    s.setID(generatedKeys.getInt(1));
                } else {
                    throw new SQLException("Creating subscription failed, no ID obtained.");
                }
            }
            return s;
        } catch (SQLException e) {
            throw new RuntimeException("Error creating subscription", e);
        }
    }

    public Subscription findByID(int id) {
        try (Connection con = DBConnection.getConnection()) {
            PreparedStatement pst = con.prepareStatement(SELECT_BY_ID);
            pst.setInt(1, id);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                return mapRowToSubscription(rs);
            } else {
                return null;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching subscription by ID", e);
        }
    }

    public List<Subscription> findByPlatform(String platform) {
        List<Subscription> results = new ArrayList<>();
        try (Connection con = DBConnection.getConnection()) {
            PreparedStatement pst = con.prepareStatement(SELECT_BY_PLATFORM);
            pst.setString(1, platform);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                results.add(mapRowToSubscription(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching subscriptions by platform", e);
        }
        return results;
    }
    public List<Subscription> findByName(String name) {
        List<Subscription> results = new ArrayList<>();
        try (Connection con = DBConnection.getConnection()) {
            PreparedStatement pst = con.prepareStatement(SELECT_BY_NAME);
            pst.setString(1, name);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                results.add(mapRowToSubscription(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching subscriptions by name", e);
        }
        return results;
    }
    public List<Subscription> findByNextDueDate(java.sql.Date nextDueDate) {
        List<Subscription> results = new ArrayList<>();
        try (Connection con = DBConnection.getConnection()) {
            PreparedStatement pst = con.prepareStatement(SELECT_BY_NEXT_DUE_DATE);
            pst.setDate(1, nextDueDate);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                results.add(mapRowToSubscription(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching subscriptions by next due date", e);
        }
        return results;
    }
    public List<Subscription> findByIsActive(boolean isActive) {
        List<Subscription> results = new ArrayList<>();
        try (Connection con = DBConnection.getConnection()) {
            PreparedStatement pst = con.prepareStatement(SELECT_BY_IS_ACTIVE);
            pst.setBoolean(1, isActive);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                results.add(mapRowToSubscription(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching subscriptions by isActive", e);
        }
        return results;
    }
    public List<Subscription> findAll() {
        List<Subscription> subscriptions = new ArrayList<>();
        try (Connection con = DBConnection.getConnection()) {
            PreparedStatement pst = con.prepareStatement(SELECT_ALL);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                subscriptions.add(mapRowToSubscription(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching all subscriptions", e);
        }
        return subscriptions;
    }
    public void update(Subscription s) {
        try (Connection con = DBConnection.getConnection()) {
            PreparedStatement pst = con.prepareStatement(UPDATE_BY_ID);
            pst.setString(1, s.getPlatform());
            pst.setString(2, s.getName());
            pst.setDouble(3, s.getCost());
            pst.setString(4, s.getBillingCycle());
            pst.setInt(5, s.getDuration());
            pst.setDate(6, java.sql.Date.valueOf(s.getNextDueDate()));
            pst.setBoolean(7, s.isAutoPay());
            pst.setDate(8, java.sql.Date.valueOf(s.getStartDate()));
            pst.setBoolean(9, s.isActive());
            pst.setInt(10, s.getID());

            int affectedRows = pst.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Updating subscription failed, no rows affected.");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error updating subscription", e);
        }
    }
    public void deleteByID(int id) {
        try (Connection con = DBConnection.getConnection()) {
            PreparedStatement pst = con.prepareStatement(DELETE_BY_ID);
            pst.setInt(1, id);
            int affectedRows = pst.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Deleting subscription failed, no rows affected.");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting subscription by ID", e);
        }
    }
    public void deleteByPlatform(String platform) {
        try (Connection con = DBConnection.getConnection()) {
            PreparedStatement pst = con.prepareStatement(DELETE_BY_PLATFORM);
            pst.setString(1, platform);
            int affectedRows = pst.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Deleting subscription failed, no rows affected.");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting subscription by platform", e);
        }
    }
    public void deleteByName(String name) {
        try (Connection con = DBConnection.getConnection()) {
            PreparedStatement pst = con.prepareStatement(DELETE_BY_NAME);
            pst.setString(1, name);
            int affectedRows = pst.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Deleting subscription failed, no rows affected.");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting subscription by name", e);
        }
    }
    private Subscription mapRowToSubscription(ResultSet rs) throws SQLException {
        return new Subscription(
            rs.getInt("id"),
            rs.getString("platform"),
            rs.getString("name"),
            rs.getDouble("cost"),
            rs.getString("billing_cycle"),
            rs.getInt("duration_days"),
            rs.getDate("next_due_date").toLocalDate(),
            rs.getBoolean("auto_pay"),
            rs.getDate("start_date").toLocalDate(),
            rs.getBoolean("is_active"),
            rs.getTimestamp("created_at").toLocalDateTime()
        );
    }
}
