// src/main/java/com/subtrack/service/SubscriptionRenewalService.java
package com.subtrack.service;

import java.time.LocalDate;
import java.util.List;

import com.subtrack.DAO.PaymentDAO;
import com.subtrack.DAO.SubscriptionDAO;
import com.subtrack.models.Payment;
import com.subtrack.models.Subscription;

public class SubscriptionRenewalService {

    public static void processSubscriptionsRenewal() {
        SubscriptionDAO subDao = new SubscriptionDAO();
        PaymentDAO payDao = new PaymentDAO();

        List<Subscription> subs = subDao.findAll();
        List<Payment> pays = payDao.findAll();
        LocalDate today = LocalDate.now();

        for (Subscription sub : subs) {
            // only active subscriptions
            if (!sub.isActive()) continue;

            LocalDate nextDue = sub.getNextDueDate();
            int durationDays = sub.getDuration();

            if (today.isAfter(nextDue)) {
                // next due date passed → deactivate
                sub.setActive(false);
                subDao.update(sub);

            } else if (!sub.isAutoPay()) {
                // auto-pay OFF & still before due date → look for manual payment
                LocalDate windowStart = nextDue.minusDays(durationDays);

                for (Payment pay : pays) {
                    if ((pay.getSubscriptionName().equals(sub.getName())) && (pay.getPlatformName().equals(sub.getPlatform())) && (!pay.getRenewed())) {
                        LocalDate pd = pay.getPaymentDate();
                        boolean inWindow =
                            ( !pd.isBefore(windowStart) ) &&
                            ( !pd.isAfter(nextDue) );

                        if (inWindow) {
                            // manual payment found → extend next due date
                            sub.setNextDueDate(nextDue.plusDays(durationDays));
                            subDao.update(sub);
                            pay.setRenewed(true);
                            payDao.update(pay);
                            break; // move to next subscription
                        }
                    }
                }
            }
        }
    }

    public static void processAutoPayments() {
        SubscriptionDAO subDao = new SubscriptionDAO();
        PaymentDAO payDao = new PaymentDAO();

        List<Subscription> subs = subDao.findAll();
        LocalDate today = LocalDate.now();

        for (Subscription sub : subs) {
            if (!sub.isActive() || !sub.isAutoPay()) continue;

            int durationDays = sub.getDuration();
            LocalDate cycleDate = sub.getNextDueDate();

            // Loop through each billing window up to today
            while (!cycleDate.isAfter(today)) {
                // For this cycleDate, check if an auto-payment exists
                List<Payment> payments = payDao.findByNameAndPlatform(sub.getName(), sub.getPlatform());
                boolean paid = false;
                for (Payment pmt : payments) {
                    if (pmt.isAutoPaid() && pmt.getPaymentDate().equals(cycleDate)) {
                        paid = true;
                        break;
                    }
                }

                if (!paid) {
                    // Record auto-payment
                    Payment payment = new Payment(
                        sub.getPlatform(),
                        sub.getName(),
                        true,
                        sub.getCost(),
                        cycleDate,
                        true
                    );
                    payDao.add(payment);
                }

                // Advance to next billing date
                cycleDate = cycleDate.plusDays(durationDays);
            }

            // Finally update subscription's nextDueDate to the next future cycleDate
            sub.setNextDueDate(cycleDate);
            subDao.update(sub);
        }
    }

}

