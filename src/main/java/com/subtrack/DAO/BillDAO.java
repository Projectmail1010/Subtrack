package com.subtrack.DAO;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.subtrack.models.Bill;
import com.subtrack.utils.DBConnection;

public class BillDAO {
    private static final String INSERT_SQL =
        "INSERT INTO utility_bills (service_type, reference_number, bill_date, amount, due_date, paid_date, is_paid, overdue_charge_perday, description) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
    private static final String SELECT_BY_ID =
        "SELECT * FROM utility_bills WHERE bill_id = ?";
    private static final String SELECT_ALL = "SELECT * FROM utility_bills";
    private static final String UPDATE_SQL =
        "UPDATE utility_bills SET service_type=?, reference_number=?, bill_date=?, amount=?, due_date=?, paid_date=?, is_paid=?,overdue_charge_perday=?, description=? WHERE bill_id = ?";
    private static final String DELETE_SQL = "DELETE FROM utility_bills WHERE bill_id = ?";

    public Bill add(Bill b) {
        try (Connection con = DBConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(INSERT_SQL, Statement.RETURN_GENERATED_KEYS)) {
            pst.setString(1, b.getServiceType());
            pst.setString(2, b.getReferenceNumber());
            pst.setDate(3, Date.valueOf(b.getBillDate()));
            pst.setDouble(4, b.getAmount());
            pst.setDate(5, Date.valueOf(b.getDueDate()));
            if (b.getPaidDate() != null) {
                pst.setDate(6, Date.valueOf(b.getPaidDate()));
            } else {
                pst.setNull(6, java.sql.Types.DATE);
            }
            pst.setBoolean(7, b.isPaid());
            pst.setDouble(8, b.getOverdueChargePerDay());
            pst.setString(9, b.getDescription());
            int affected = pst.executeUpdate();
            if (affected == 0) throw new SQLException("Creating bill failed, no rows affected.");
            try (ResultSet keys = pst.getGeneratedKeys()) {
                if (keys.next()) b.setBillId(keys.getInt(1));
            }
            return b;
        } catch (SQLException e) {
            throw new RuntimeException("Error creating bill", e);
        }
    }

    public Bill findById(int id) {
        try (Connection con = DBConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(SELECT_BY_ID)) {
            pst.setInt(1, id);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) return mapRow(rs);
            return null;
        } catch (SQLException e) {
            throw new RuntimeException("Error finding bill by ID", e);
        }
    }

    public List<Bill> findAll() {
        List<Bill> bills = new ArrayList<>();
        try (Connection con = DBConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(SELECT_ALL);
             ResultSet rs = pst.executeQuery()) {
            while (rs.next()) bills.add(mapRow(rs));
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching all bills", e);
        }
        return bills;
    }

    public void update(Bill b) {
        try (Connection con = DBConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(UPDATE_SQL)) {
            pst.setString(1, b.getServiceType());
            pst.setString(2, b.getReferenceNumber());
            pst.setDate(3, Date.valueOf(b.getBillDate()));
            pst.setDouble(4, b.getAmount());
            pst.setDate(5, Date.valueOf(b.getDueDate()));
            if (b.getPaidDate() != null) {
                pst.setDate(6, Date.valueOf(b.getPaidDate()));
            } else {
                pst.setNull(6, java.sql.Types.DATE);
            }
            pst.setBoolean(7, b.isPaid());
            pst.setDouble(8, b.getOverdueChargePerDay());
            pst.setString(9, b.getDescription());
            pst.setInt(10, b.getBillId());
            if (pst.executeUpdate() == 0) throw new SQLException("Updating bill failed, no rows affected.");
        } catch (SQLException e) {
            throw new RuntimeException("Error updating bill", e);
        }
    }

    public void deleteById(int id) {
        try (Connection con = DBConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(DELETE_SQL)) {
            pst.setInt(1, id);
            if (pst.executeUpdate() == 0) throw new SQLException("Deleting bill failed, no rows affected.");
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting bill", e);
        }
    }

    //common finder method
    public List<Bill> findByField(String field, Object value) {
        String sql = "SELECT * FROM utility_bills WHERE " + field + " = ?";
        List<Bill> list = new ArrayList<>();
        try (Connection con = DBConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(sql)) {
            if (value instanceof String) pst.setString(1, (String) value);
            else if (value instanceof Integer) pst.setInt(1, (Integer) value);
            else if (value instanceof Double) pst.setDouble(1, (Double) value);
            else if (value instanceof Boolean) pst.setBoolean(1, (Boolean) value);
            else if (value instanceof LocalDate) pst.setDate(1, Date.valueOf((LocalDate) value));
            else throw new IllegalArgumentException("Unsupported field type");

            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding bills by field", e);
        }
        return list;
    }

    private Bill mapRow(ResultSet rs) throws SQLException {
        return new Bill(
            rs.getInt("bill_id"),
            rs.getString("service_type"),
            rs.getString("reference_number"),
            rs.getDate("bill_date").toLocalDate(),
            rs.getDouble("amount"),
            rs.getDate("due_date").toLocalDate(),
            rs.getDate("paid_date") != null ? rs.getDate("paid_date").toLocalDate() : null,
            rs.getBoolean("is_paid"),
            rs.getDouble("overdue_charge_perday"),
            rs.getString("description"),
            rs.getTimestamp("created_at").toLocalDateTime()
        );
    }
}
