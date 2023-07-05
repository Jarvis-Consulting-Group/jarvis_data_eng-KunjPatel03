package ca.jrvs.apps.jdbc;

import java.sql.Connection;
import java.sql.SQLException;

public class JDBCExecutor {
    public static void main(String[] args) {
        DatabaseConnectionManager dbConnectionManager = new DatabaseConnectionManager("localhost","hplussport","postgres","password");

        try{
            Connection connection = dbConnectionManager.getConnection();

//            CustomerDAO customerDAO = new CustomerDAO(connection);

//            Insert data to customer
//            Customer customer = new Customer();
//            customer.setFirstName("George");
//            customer.setLastName("Washington");
//            customer.setEmail("george.washington@wh.gov");
//            customer.setPhone("(555) 555-1234");
//            customer.setAddress("1234 Main St");
//            customer.setCity("Mount Vernon");
//            customer.setState("VA");
//            customer.setZipCode("22121");

//            Update inserted customer
//            Customer dbCustomer = customerDAO.create(customer);
//            System.out.println(dbCustomer);
//            dbCustomer = customerDAO.findById(dbCustomer.getId());
//            System.out.println(dbCustomer);
//            dbCustomer.setEmail("george.washington@wh.gov");
//            dbCustomer = customerDAO.update(dbCustomer);
//            System.out.println(dbCustomer);

//            Delete last customer
//            customerDAO.delete(dbCustomer.getId());

            OrderDAO orderDAO = new OrderDAO(connection);
            Order order = orderDAO.findById(1000);
            System.out.println(order);

        }catch (SQLException e){
            e.printStackTrace();
        }
    }
}