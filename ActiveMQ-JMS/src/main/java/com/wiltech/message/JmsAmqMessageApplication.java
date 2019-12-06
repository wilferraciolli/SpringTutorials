package com.wiltech.message;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class JmsAmqMessageApplication {

    public static void main(String[] args) throws Exception  {

//        // start the embedded server to test. PS do not do this in prod
        //        ActiveMQServer server = ActiveMQServers.newActiveMQServer(new ConfigurationImpl()
        //                .setPersistenceEnabled(false)
        //                .setJournalDirectory("target/data/journal")
        //                .setSecurityEnabled(false)
        //                .addAcceptorConfiguration("invm", "vm://0"));
        //
        //        server.start();

        SpringApplication.run(JmsAmqMessageApplication.class, args);
    }

}
