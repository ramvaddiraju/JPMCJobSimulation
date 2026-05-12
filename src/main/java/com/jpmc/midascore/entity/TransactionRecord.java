package com.jpmc.midascore.entity;

import com.jpmc.midascore.foundation.Transaction;
import jakarta.persistence.*;
@Entity
public class TransactionRecord extends Transaction{
    @Id
    @GeneratedValue
    private long id;

    @ManyToOne
    private UserRecord senderid;

    @ManyToOne
    private UserRecord recipientId;

    private float amount;

    private float incentive;

    private TransactionRecord()
    {
    }
    public TransactionRecord(UserRecord senderid,UserRecord recipientId, float amount, float incentive)
    {
        this.senderid = senderid;
        this.recipientId=recipientId;
        this.amount=amount;
        this.incentive = incentive;
    }
}
