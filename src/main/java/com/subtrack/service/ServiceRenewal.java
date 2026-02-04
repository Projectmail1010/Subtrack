package com.subtrack.service;

import java.util.List;

import com.subtrack.DAO.BillDAO;
import com.subtrack.DAO.BillPaymentDAO;
import com.subtrack.models.Bill;
import com.subtrack.models.BillPayment;

public class ServiceRenewal {

    public static void reconcileBillPayments() {
        BillDAO billDao = new BillDAO();
        BillPaymentDAO payDao = new BillPaymentDAO();

        // Fetch all bills where is_paid = false
        List<Bill> unpaidBills = billDao.findByField("is_paid", "false");

        for (Bill bill : unpaidBills) {
            String ref = bill.getReferenceNumber();

            // Look for any payment matching this reference
            List<BillPayment> matches = payDao.findByField("bill_reference_number", ref);
            if (matches.isEmpty()) {
                // no payment found yet
                continue;
            }

            // If one or more payments exist, pick the earliest (or last) and update
            BillPayment payment = matches.get(0);
            bill.setPaidDate(payment.getPaymentDate());
            bill.setPaid(true);

            // Persist changes
            billDao.update(bill);
        }
    }
}

