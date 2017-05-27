package com.mycompany.devman.repositories;

import com.mycompany.devman.BackendSetup;
import com.mycompany.devman.domain.MailConfig;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.List;

/**
 * Created by jakub on 15.05.17.
 */
public class MailConfigRepository {
    public static void setMailConfig(MailConfig config) throws Exception {
        Session session = BackendSetup.getDatabaseSession();
        Transaction transaction = session.beginTransaction();
        session.save(config);
        transaction.commit();
        session.close();
    }
    public static MailConfig getMailCOnfig() throws Exception {
        Session session = BackendSetup.getDatabaseSession();
        Transaction transaction = session.beginTransaction();
        List<MailConfig> list = session.createQuery("FROM MailConfig").list();
        transaction.commit();
        session.close();
        if(list.size() == 0) {
            return null;
        }
        else if(list.size() == 1) {
            return list.get(0);
        }
        else throw new Exception("Błąd konfiguracji SMTP!");
    }
}
