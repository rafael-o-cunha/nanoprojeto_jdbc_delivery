package com.nanoprojeto.delivery;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import com.nanoprojeto.delivery.config.ConnectionFactory;
import com.nanoprojeto.delivery.config.EnvFile;
import com.nanoprojeto.delivery.daos.OrderDao;
import com.nanoprojeto.delivery.daos.ProductDao;
import com.nanoprojeto.delivery.entities.Order;
import com.nanoprojeto.delivery.entities.Product;

public class App {
    public static void main(String[] args) {
        try {
        	Connection conn = ConnectionFactory.getConnection(EnvFile.ENV);
        	
        	ProductDao pDao = new ProductDao(conn);
        	List<Product> products = pDao.findAll();
        	products.forEach(System.out::println);
        	
        	System.out.println();
        	
        	OrderDao oDao = new OrderDao(conn);
        	List<Order> orders = oDao.findAll();
        	orders.forEach(System.out::println);
        	
        	System.out.println();
        	
        	List<Order> ordersWithProducts = oDao.findOrdersWithProducts();
        	ordersWithProducts.forEach(System.out::println);
        	
        }
        catch (SQLException e) {
            System.out.println("Erro ao conectar ao banco: " + e.getMessage());
        }
        finally {
        	ConnectionFactory.closeConnection();
        }
    }
    
}
