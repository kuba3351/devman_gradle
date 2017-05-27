/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package registerTests.registerTests;

import com.mycompany.devman.BackendSetup;
import com.mycompany.devman.repositories.UserRepository;
import com.mycompany.devman.domain.AccountType;
import com.mycompany.devman.domain.Leave;
import com.mycompany.devman.domain.Message;
import com.mycompany.devman.domain.Project;
import com.mycompany.devman.domain.Task;
import com.mycompany.devman.domain.Team;
import com.mycompany.devman.domain.User;
import com.mycompany.devman.domain.WorkTime;
import javax.validation.Validation;
import javax.validation.ValidatorFactory;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

/**
 *
 * @author bloczek
 */

@RunWith(JUnit4.class)
public class registerTests {
   
    @Before
    public void setup() {
        SessionFactory sessionFactory = new Configuration().addAnnotatedClass(User.class)
                .addAnnotatedClass(Leave.class)
                    .addAnnotatedClass(Message.class)
                    .addAnnotatedClass(Project.class)
                    .addAnnotatedClass(Task.class)
                    .addAnnotatedClass(Team.class)
                    .addAnnotatedClass(WorkTime.class)
                .setProperty("hibernate.dialect", "org.hibernate.dialect.H2Dialect")
                .setProperty("hibernate.connection.driver_class", "org.h2.Driver")
                .setProperty("hibernate.connection.url", "jdbc:h2:mem:")
                .setProperty("hibernate.hbm2ddl.auto", "update")
                    .buildSessionFactory();
        ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
        BackendSetup.setSessionFactory(sessionFactory);
        BackendSetup.setValidatorFactory(validatorFactory);
    }

    @Test
    @Ignore
    public void loginTest() throws Exception {
        User manager = new User();
        manager.setLogin("admin");
        manager.setPassword("devman2017");
        manager.setName("Jan");
        manager.setLastName("Kowalski");
        manager.setPesel("11111111111");
        manager.setEmail("admin@devman.pl");
        manager.setAccountType(AccountType.MANAGER);

        manager = UserRepository.addUserToDatabase(manager);

        User user = new User();
        user.setLogin("kuba3351");
        user.setPassword("devman2017");
        user.setName("Kuba");
        user.setLastName("Sierżęga");
        user.setPesel("11111111111");
        user.setEmail("kuba3351@gmail.com");
        user.setAccountType(AccountType.EMPLOYEE);
        user.setManager(manager);
        user = UserRepository.addUserToDatabase(user);
        assertEquals(UserRepository.findByLoginAndPassword("kuba3351", "devman2017").getLogin(), user.getLogin());
        assertEquals(UserRepository.findByLoginAndPassword("kuba3351", "devman2017").getPassword(), user.getPassword());

    }
    
    @Test(expected = Exception.class)
    public void registerWithPeselTooShortTest() throws Exception {
        User manager = new User();
        manager.setLogin("admin");
        manager.setPassword("devman2017");
        manager.setName("Jan");
        manager.setLastName("Kowalski");
        manager.setPesel("11111111111");
        manager.setEmail("admin@devman.pl");
        manager.setAccountType(AccountType.MANAGER);

        manager = UserRepository.addUserToDatabase(manager);

        User user = new User();
        user.setLogin("kuba3351");
        user.setPassword("devman2017");
        user.setName("Kuba");
        user.setLastName("Sierżęga");
        user.setPesel("111");
        user.setEmail("kuba3351@gmail.com");
        user.setAccountType(AccountType.EMPLOYEE);
        user.setManager(manager);
        UserRepository.addUserToDatabase(user);
    }




}
