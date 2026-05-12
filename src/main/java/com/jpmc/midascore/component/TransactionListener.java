package com.jpmc.midascore.component;
import com.jpmc.midascore.entity.TransactionRecord;
import com.jpmc.midascore.entity.UserRecord;
import com.jpmc.midascore.foundation.Balance;
import com.jpmc.midascore.foundation.Transaction;
import com.jpmc.midascore.repository.TransactionRepository;
import com.jpmc.midascore.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.kafka.annotation.*;
import org.springframework.stereotype.Component;
import jakarta.annotation.PostConstruct;
import org.springframework.web.client.RestTemplate;

@Component
public class TransactionListener{
    private final UserRepository userRepository;
    private final TransactionRepository transactionRepository;
    private final RestTemplate restTemplate;

    public TransactionListener(UserRepository userRepository, TransactionRepository transactionRepository, RestTemplate restTemplate) {
        this.userRepository = userRepository;
        this.transactionRepository = transactionRepository;
        this.restTemplate = restTemplate;
    }

    @PostConstruct
    public void init() {
        System.out.println("TransactionListener loaded");
    }

    @KafkaListener(topics = "${general.kafka-topic}")
    @Transactional
    public void listen(Transaction transaction) {
        System.out.println("Received: " + transaction);

        //Find id for sender and recipient
        UserRecord sender = userRepository.findById(transaction.getSenderId());
        UserRecord recipient = userRepository.findById(transaction.getRecipientId());

        //Validation
        if(sender == null || recipient == null)
        {
            return;
        }
        if(sender.getBalance()<transaction.getAmount())
        {
            return;
        }

        //call Incentive
        Balance incentiveResponse = restTemplate.postForObject("http://localhost:8080/incentive",transaction,Balance.class);
        float incentive = (incentiveResponse!=null)?incentiveResponse.getAmount():0;

        //UpdateBalance
        sender.setBalance(sender.getBalance()-transaction.getAmount());
        recipient.setBalance(recipient.getBalance()+transaction.getAmount()+incentive);

        //Save TransactionRecord
        TransactionRecord record = new TransactionRecord(sender,recipient,transaction.getAmount(),incentive);
        transactionRepository.save(record);
        userRepository.save(sender);
        userRepository.save(recipient);
        System.out.println((int)(userRepository.findByName("wilbur").getBalance()));
    }
}
