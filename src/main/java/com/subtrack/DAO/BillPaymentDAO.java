package com.subtrack.DAO;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.subtrack.models.BillPayment;
import com.subtrack.utils.DBConnection;

public class BillPaymentDAO {
    private static final String INSERT_SQL =
        "INSERT INTO utility_bills_payments (service_type, bill_id, bill_reference_number, bill_amount, total_overdue_charge, total_amount,  payment_date) VALUES (?, ?, ?, ?, ?, ?, ?)";

    private static final String SELECT_BY_ID = "SELECT * FROM utility_bills_payments WHERE payment_id = ?";
    private static final String SELECT_ALL = "SELECT * FROM utility_bills_payments";
    private static final String DELETE_SQL = "DELETE FROM utility_bills_payments WHERE payment_id = ?";

    public BillPayment add(BillPayment p) {
        try (Connection con = DBConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(INSERT_SQL, Statement.RETURN_GENERATED_KEYS)) {
            pst.setString(1, p.getServiceType());
            pst.setInt(2, p.getBillId());
            pst.setString(3, p.getBillReferenceNumber());
            pst.setDouble(4, p.getBillAmount());
            pst.setDouble(5, p.getTotalOverdueCharge());
            pst.setDouble(6, p.getTotalAmount());
            pst.setDate(7, Date.valueOf(p.getPaymentDate()));
            int affected = pst.executeUpdate();
            if (affected == 0) throw new SQLException("Creating payment failed, no rows affected.");
            try (ResultSet keys = pst.getGeneratedKeys()) {
                if (keys.next()) p.setPaymentId(keys.getInt(1));
            }
            return p;
        } catch (SQLException e) {
            throw new RuntimeException("Error creating bill payment", e);
        }
    }

    public List<BillPayment> findAll() {
        List<BillPayment> list = new ArrayList<>();
        try (Connection con = DBConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(SELECT_ALL);
             ResultSet rs = pst.executeQuery()) {
            while (rs.next()) {
                BillPayment p = new BillPayment(
                    rs.getInt("payment_id"),
                    rs.getString("service_type"),
                    rs.getInt("bill_id"),
                    rs.getString("bill_reference_number"),
                    rs.getDouble("bill_amount"),
                    rs.getDouble("total_overdue_charge"),
                    rs.getDouble("total_amount"),
                    rs.getDate("payment_date").toLocalDate()
                );
                list.add(p);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching bill payments", e);
        }
        return list;
    }

    public List<BillPayment> findByField(String column, String value) {
        String sql = "SELECT * FROM utility_bills_payments WHERE " + column + " = ?";
        List<BillPayment> list = new ArrayList<>();
        try (Connection con = DBConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setString(1, value);
            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    BillPayment p = new BillPayment(
                        rs.getInt("payment_id"),
                        rs.getString("service_type"),
                        rs.getInt("bill_id"),
                        rs.getString("bill_reference_number"),
                        rs.getDouble("bill_amount"),
                        rs.getDouble("total_overdue_charge"),
                        rs.getDouble("total_amount"),
                        rs.getDate("payment_date").toLocalDate()
                    );
                    list.add(p);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error in BillPaymentDAO.findByField", e);
        }
        return list;
    }

    public void deleteById(int id) {
        try (Connection con = DBConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(DELETE_SQL)) {
            pst.setInt(1, id);
            if (pst.executeUpdate() == 0) throw new SQLException("Deleting payment failed, no rows affected.");
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting bill payment", e);
        }
    }
}
