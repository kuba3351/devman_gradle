package registerTests.registerTests;

import com.mycompany.devman.BackendSetup;
import com.mycompany.devman.repositories.UserRepository;
import com.mycompany.devman.domain.*;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import javax.validation.Validation;
import javax.validation.ValidatorFactory;

import static junit.framework.TestCase.assertEquals;
import org.junit.Ignore;

/**
 * Created by bloczek on 04.04.2017.
 */
@RunWith(JUnit4.class)
public class UserTest {


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
    @Ignore
    @Test
    public void addTest() throws Exception {
        User user = new User();
        user.setName("addUser");
        user.setAccountType(AccountType.EMPLOYEE);
        user.setEmail("sample@email.pl");
        user.setLastName("kowalski");
        user.setPassword("password1");
        user.setPesel("23232312345");
        user.setLogin("test");
        Session session = BackendSetup.getDatabaseSession();
        UserRepository.addUserToDatabase(user);
        assertEquals(user, session.createQuery("FROM User").list().get(0));
    }
    @Ignore
    @Test
    public void findUserById() throws Exception {
        User user = new User();
        user.setLogin("login");
        user.setName("jan");
        user.setAccountType(AccountType.EMPLOYEE);
        user.setEmail("sample@email.pl");
        user.setLastName("kowalski");
        user.setPassword("password1");
        user.setPesel("23232312345");
        UserRepository.addUserToDatabase(user);
        assertEquals(user,UserRepository.findByLoginAndPassword("login","password1"));
    }

    @Test(expected = Exception.class)
    public void findUserByLoginAndPasswordIfNotExist() throws Exception {
        User testUsers = UserRepository.findByLoginAndPassword("6","9");
    }

    @Test (expected = Exception.class)
    public void addUserIfIncorrect() throws Exception {
        User user = new User();
        UserRepository.addUserToDatabase(user);
    }


}
