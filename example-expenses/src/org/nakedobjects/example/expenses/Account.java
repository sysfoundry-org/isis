

package org.nakedobjects.example.expenses;

import org.nakedobjects.application.value.Money;
import org.nakedobjects.application.value.TextString;

import java.util.Enumeration;
import java.util.Vector;


public class Account extends BaseObject {
    private final TextString accountNumber;
    private final Vector outStandingClaims;
    private final Vector claims;
    private final Vector payments;

    public Account() {
        accountNumber = new TextString();
        outStandingClaims = new Vector();
        claims = new Vector();
        payments = new Vector();
    }

    public String titleString() {
       TextString account = getAccountNumber();
        return account.isEmpty() ? account.titleString() : "New Account";
    }

    public Money deriveBalance() {
        Money balance = new Money();
        Enumeration claims = getClaims().elements();

        while (claims.hasMoreElements()) {
            Claim newClaim = (Claim) claims.nextElement();
            balance.add(newClaim.deriveTotal());
        }

        Enumeration payments = getPayments().elements();

        while (payments.hasMoreElements()) {
            Payment newPayment = (Payment) payments.nextElement();
            balance.subtract(newPayment.getPaymentAmount());
        }

        return balance;
    }

    public Vector getClaims() {
        return claims;
    }

    public void addToClaims(Claim claim) {
        claims.addElement(claim);
        objectChanged();
    }

    public void  removeFromClaims(Claim claim) {
        claims.removeElement(claim);
        objectChanged();
    }
    
    public Vector getPayments() {
        return payments;
    }

    public void addToPayments(Payment payment) {
        payments.addElement(payment);
        objectChanged();
    }

    public void  removeFromPayments(Payment payment) {
        payments.removeElement(payment);
        objectChanged();
    }

    public TextString getAccountNumber() {
        return accountNumber;
    }

    public Vector getOutStandingClaims() {
        return outStandingClaims;
    }

    public void addToOutStandingClaims(Claim claim) {
        outStandingClaims.addElement(claim);
        objectChanged();
    }

    public void  removeFromOutStandingClaims(Claim claim) {
        outStandingClaims.removeElement(claim);
        objectChanged();
    }
    
 
    public void actionBalanceAccount() {
        Payment newPayment = new Payment();
        newPayment.getPaymentAmount().setValue(deriveBalance());
        getPayments().add(newPayment);

        Enumeration claims = getClaims().elements();

        while (claims.hasMoreElements()) {
            Claim newClaim = (Claim) claims.nextElement();
            Enumeration expenses = newClaim.getExpenses().elements();

            while (expenses.hasMoreElements()) {
                Expense newExpenseItem = (Expense) expenses.nextElement();
                newExpenseItem.getStatus().setValue("Paid");
                newExpenseItem.objectChanged();
            }
        }
    }

    public Payment actionGenerateAdvance() {
        Payment newPayment = new Payment();
        getPayments().add(newPayment);
        return newPayment;
    }
}


/*
Naked Objects - a framework that exposes behaviourally complete
business objects directly to the user.
Copyright (C) 2000 - 2005  Naked Objects Group Ltd

This program is free software; you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation; either version 2 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA

The authors can be contacted via www.nakedobjects.org (the
registered address of Naked Objects Group is Kingsway House, 123 Goldworth
Road, Woking GU21 1NR, UK).
*/
