package com.subtrack.DAO;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.subtrack.models.Payment;
import com.subtrack.utils.DBConnection;

public class PaymentDAO {
    private static final String INSERT_SQL =
        "INSERT INTO payments (platform_name, subscription_name, is_auto_paid, amount, payment_date, sub_renewed) VALUES (?, ?, ?, ?, ?, ?)";
    private static final String SELECT_BY_ID =
        "SELECT * FROM payments WHERE id = ?";
    private static final String SELECT_ALL =
        "SELECT * FROM payments";
    private static final String SELECT_BY_PLATFORM =
        "SELECT * FROM payments WHERE platform_name = ?";
    private static final String SELECT_BY_SUBSCRIPTION =
        "SELECT * FROM payments WHERE subscription_name = ?";
    private static final String SELECT_BY_DATE =
        "SELECT * FROM payments WHERE payment_date = ?";
    private static final String SELECT_BY_AUTOPAY =
        "SELECT * FROM payments WHERE is_auto_paid = ?";
    private static final String SELECT_BY_RENEWED = 
        "SELECT * FROM payments WHERE sub_renewed = ?";
    private static final String UPDATE_BY_ID =
        "UPDATE payments SET platform_name = ?, subscription_name = ?, is_auto_paid = ?, amount = ?, payment_date = ?, sub_renewed = ? WHERE id = ?";
    private static final String DELETE_BY_ID =
        "DELETE FROM payments WHERE id = ?";

    public Payment add(Payment p) {
        try (Connection con = DBConnection.getConnection()) {
            PreparedStatement pst = con.prepareStatement(INSERT_SQL, PreparedStatement.RETURN_GENERATED_KEYS);
            pst.setString(1, p.getPlatformName());
            pst.setString(2, p.getSubscriptionName());
            pst.setBoolean(3, p.isAutoPaid());
            pst.setDouble(4, p.getAmount());
            pst.setDate(5, Date.valueOf(p.getPaymentDate()));
            pst.setBoolean(6, p.getRenewed());

            int affected = pst.executeUpdate();
            if (affected == 0) {
                throw new SQLException("Creating payment failed, no rows affected.");
            }

            try (ResultSet keys = pst.getGeneratedKeys()) {
                if (keys.next()) {
                    p.setId(keys.getInt(1));
                } else {
                    throw new SQLException("Creating payment failed, no ID obtained.");
                }
            }
            return p;
        } catch (SQLException e) {
            throw new RuntimeException("Error creating payment", e);
        }
    }

    public Payment findByID(int id) {
        try (Connection con = DBConnection.getConnection()) {
            PreparedStatement pst = con.prepareStatement(SELECT_BY_ID);
            pst.setInt(1, id);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                return mapRowToPayment(rs);
            }
            return null;
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching payment by ID", e);
        }
    }

    public List<Payment> findAll() {
        List<Payment> list = new ArrayList<>();
        try (Connection con = DBConnection.getConnection()) {
            PreparedStatement pst = con.prepareStatement(SELECT_ALL);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                list.add(mapRowToPayment(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching all payments", e);
        }
        return list;
    }

    public List<Payment> findByPlatformName(String platform) {
        List<Payment> list = new ArrayList<>();
        try (Connection con = DBConnection.getConnection()) {
            PreparedStatement pst = con.prepareStatement(SELECT_BY_PLATFORM);
            pst.setString(1, platform);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) list.add(mapRowToPayment(rs));
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching payments by platform", e);
        }
        return list;
    }

    public List<Payment> findByIsRenewed(boolean renewed) {
        List<Payment> list = new ArrayList<>();
        try (Connection con = DBConnection.getConnection()) {
            PreparedStatement pst = con.prepareStatement(SELECT_BY_RENEWED);
            pst.setBoolean(1, renewed);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                list.add(mapRowToPayment(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching payments by renewal status", e);
        }
        return list;
    }

    public List<Payment> findByNameAndPlatform(String subName, String platform) {
        List<Payment> list = new ArrayList<>();
        String query = "SELECT * FROM payments WHERE subscription_name = ? AND platform_name = ?";
        try (Connection con = DBConnection.getConnection()) {
            PreparedStatement pst = con.prepareStatement(query);
            pst.setString(1, subName);
            pst.setString(2, platform);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                list.add(mapRowToPayment(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching payments by name and platform", e);
        }
        return list;
    }

    public List<Payment> findBySubscriptionName(String subName) {
        List<Payment> list = new ArrayList<>();
        try (Connection con = DBConnection.getConnection()) {
            PreparedStatement pst = con.prepareStatement(SELECT_BY_SUBSCRIPTION);
            pst.setString(1, subName);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) list.add(mapRowToPayment(rs));
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching payments by subscription name", e);
        }
        return list;
    }

    public List<Payment> findByPaymentDate(LocalDate date) {
        List<Payment> list = new ArrayList<>();
        try (Connection con = DBConnection.getConnection()) {
            PreparedStatement pst = con.prepareStatement(SELECT_BY_DATE);
            pst.setDate(1, Date.valueOf(date));
            ResultSet rs = pst.executeQuery();
            while (rs.next()) list.add(mapRowToPayment(rs));
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching payments by date", e);
        }
        return list;
    }

    public List<Payment> findByIsAutoPaid(boolean auto) {
        List<Payment> list = new ArrayList<>();
        try (Connection con = DBConnection.getConnection()) {
            PreparedStatement pst = con.prepareStatement(SELECT_BY_AUTOPAY);
            pst.setBoolean(1, auto);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) list.add(mapRowToPayment(rs));
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching payments by auto-pay flag", e);
        }
        return list;
    }

    public void update(Payment p) {
        try (Connection con = DBConnection.getConnection()) {
            PreparedStatement pst = con.prepareStatement(UPDATE_BY_ID);
            pst.setString(1, p.getPlatformName());
            pst.setString(2, p.getSubscriptionName());
            pst.setBoolean(3, p.isAutoPaid());
            pst.setDouble(4, p.getAmount());
            pst.setDate(5, Date.valueOf(p.getPaymentDate()));
            pst.setBoolean(6, p.getRenewed());
            pst.setInt(7, p.getId());
            int affected = pst.executeUpdate();
            if (affected == 0) {
                throw new SQLException("Updating payment failed, no rows affected.");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error updating payment", e);
        }
    }

    public void deleteByID(int id) {
        try (Connection con = DBConnection.getConnection()) {
            PreparedStatement pst = con.prepareStatement(DELETE_BY_ID);
            pst.setInt(1, id);
            int affected = pst.executeUpdate();
            if (affected == 0) {
                throw new SQLException("Deleting payment failed, no rows affected.");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting payment by ID", e);
        }
    }


    private Payment mapRowToPayment(ResultSet rs) throws SQLException {
        return new Payment(
            rs.getInt("id"),
            rs.getString("platform_name"),
            rs.getString("subscription_name"),
            rs.getBoolean("is_auto_paid"),
            rs.getDouble("amount"),
            rs.getDate("payment_date").toLocalDate(),
            rs.getBoolean("sub_renewed"),
            rs.getTimestamp("created_at").toLocalDateTime()
        );
    }
}
